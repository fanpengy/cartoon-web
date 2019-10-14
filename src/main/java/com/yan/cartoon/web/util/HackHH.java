package com.yan.cartoon.web.util;

import javafx.util.Pair;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.script.ScriptException;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.yan.cartoon.web.constant.ObjectConstants.AES_COMPUTE_FILE;
import static com.yan.cartoon.web.constant.StringConstants.*;

@Component
public class HackHH {

    @Value("${haha.baseurl}")
    private  String baseUrl;

    @Value("${haha.aeskey}")
    private  String key;

    @Value("${haha.update}")
    public String updateList;

    @Value("${haha.finish}")
    private String finishChangeList;

    @Value("${mhdownloadfolder}")
    private String mhDownloadFolder;

    @Value("${haha.mhlistpostfix}")
    private String mhListPostfix;

    @Value("${haha.mhcatalogpostfix}")
    private String mhCatalogPostfix;

    @Value("${haha.mhchapterpostfix}")
    private String mhChapterPostfix;

    @Value("${haha.menupostfix}")
    private String menuPostfix;

    @Autowired
    private ScriptCaller caller;


    public void update() throws IOException {
        String[] cartoons = updateList.split("\\|");
        for (String cartoon : cartoons) {
            String[] values = cartoon.split(":");
            downloadMHWeb(new Pair<>(values[0],String.format(menuPostfix,values[1])),baseUrl,key,
                    mhDownloadFolder,values[2]);
        }
    }

    public void finishChange() throws IOException {

        String[] cartoons = finishChangeList.split("\\|");
        for (String cartoon : cartoons) {
            String[] values = cartoon.split(":");
            downloadMHWeb(new Pair<>(values[0],String.format(menuPostfix,values[1])),baseUrl,key,
                    mhDownloadFolder,values[2]);
        }
    }

    public void hackhh(String baseUrl, String key, boolean serializable, String dir) throws IOException, ScriptException, NoSuchMethodException {
        //首先获取漫画列表

        for (int page = 1; page < 10; page++) {

            String listString = this.postRetryWithDefaultProxys(
                    String.format(mhListPostfix,
                            baseUrl,
                            serializable ? "no" : "yes",
                            page),null).string();
            String decryptListHtml = caller.call(AES_COMPUTE_FILE, "aesDecrypt", listString, key);

            //然后获取单个漫画进行下载
            List<Pair<String, String>> list = HtmlParser.parseList(decryptListHtml);
            for (Pair<String, String> pair : list) {
                //downloadMH(pair,baseUrl,key,dir);
            }
        }
    }

    public void downloadMHWeb(Pair<String, String> pair,String baseUrl,
                              String key, String dir,String name) throws IOException {

        System.out.println("正在解析 " + name);
        //addMHtoHome
        addMHtoHome(pair, dir, name);
        //创建文件夹
        File d = new File(dir + "/images/" + pair.getKey());
        if (!d.exists()) {
            Files.createDirectory(d.toPath());
        }

        //这里把已下载的章节筛选出来
        File[] jpgs = d.listFiles(pathname -> pathname.isFile() && pathname.getName().contains("jpg"));
        List<String> jpgNames = new ArrayList<>();
        if (jpgs != null) {
            jpgNames = Stream.of(jpgs).map(File::getName).distinct().collect(Collectors.toList());
        }

        String catalogueString = this.postRetryWithDefaultProxys(String.format(mhCatalogPostfix,
                baseUrl,
                new Date().getTime(),
                pair.getValue().substring(pair.getValue().indexOf("?") + 1)
        ), null).string();
        String decryptCatalogueHtml = caller.call(AES_COMPUTE_FILE, "aesDecrypt", catalogueString, key);
        List<Pair<Integer, String>> catelogues = HtmlParser.parseCatalogue(decryptCatalogueHtml);
        //创建漫画页面文件夹
        File dic = new File(dir + "/views/" + pair.getKey());
        if (!dic.exists()) {
            Files.createDirectory(dic.toPath());
        }
        //在这里生成目录页
        String catelogPage = dir + "/views/" + pair.getKey() + File.separator + pair.getKey() + ".jsp";
        StringBuilder page = new StringBuilder();
        page.append(pageEncoding);
        //最后下载漫画
        for (int i = 0; i < catelogues.size(); i++) {

            Pair<Integer, String> catelog = catelogues.get(i);
            List<String> pics = jpgNames.stream().filter(jpgname -> jpgname.startsWith(catelog.getKey() + "")).
                    distinct().sorted(nameComparator).collect(Collectors.toList());
            if (pics.size() == 0) {
                System.out.println(String.format("正在下载第%s章",i+1));
                pics = downloadChapter(pair, catelog, baseUrl, key, dir + "/images");
                System.out.println(String.format("第%s章下载完成",i+1));
            }

            //generateChapterPage
            generateChapterPage(dir, i, pair.getKey(), pics);

            page.append(String.format(jumpUrl,30,pair.getKey() + i,String.format("%s第%s章", name, i + 1)));

        }
        page.append(String.format(jumpUrl,30,"homepage","首页"));
        InputStream pis = new ByteArrayInputStream(page.toString().getBytes());
        FileOutputStream pos = new FileOutputStream(catelogPage);
        byte[] pbuf = new byte[1024];
        int plen = 0;
        while ((plen = pis.read(pbuf)) > 0) {
            pos.write(pbuf,0,plen);
        }
        pos.close();

    }

    private void generateChapterPage(String dir, int chapterId, String title, List<String> pics) throws IOException {
        String directUrl = "%s/views/%s/%s%s.jsp";
        StringBuilder html = new StringBuilder();
        html.append(htmlPrefix0);
        html.append(title);
        html.append(htmlPrefixweb1);
        for (String pic : pics) {
            html.append(String.format(pictureLine,"/images/" + title + File.separator + pic));
        }
        if (chapterId > 0) {
            html.append(String.format(jumpUrl,20,String.format("%s%s",title, chapterId - 1),"上一章"));
        }
        html.append(String.format(jumpUrl,20,String.format("%s%s",title, chapterId + 1),"下一章"));

        html.append(String.format(jumpUrl,20,title,"目录"));
        html.append(htmlPostfix);
        InputStream inputStream = new ByteArrayInputStream(html.toString().getBytes());
        FileOutputStream outputStream = new FileOutputStream(String.format(directUrl,dir,title,title,chapterId));
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf,0,len);
        }
        outputStream.close();
    }

    private void addMHtoHome(Pair<String, String> pair, String dir, String name) throws IOException {
        File homepage = new File(dir + "/views/homepage.jsp");
        if (!homepage.exists()) {
            Files.createFile(homepage.toPath());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(homepage,true)));
            writer.write(pageEncoding);
            writer.flush();
            writer.close();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(homepage)));
        String line = null;
        boolean contain = false;
        while ((line = reader.readLine()) != null) {
            if (line.contains(pair.getKey())) {
                contain = true;
                break;
            }
        }
        reader.close();
        if (!contain) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(homepage,true)));
            writer.write(String.format(jumpUrl,20,pair.getKey(),name));
            writer.flush();
            writer.close();
        }
    }


    public List<String> downloadChapter(Pair<String, String> pair,Pair<Integer, String> catalogue,String baseUrl,
                                       String key, String dir) {
        //解析本章图片地址
        List<String> pics = new ArrayList<>();
        try {
            String picString = this.postRetryWithDefaultProxys(String.format(mhChapterPostfix,
                    baseUrl,
                    new Date().getTime(),
                    catalogue.getValue().substring(catalogue.getValue().indexOf("?") + 1)), null).string();
            String decryptPicHtml = caller.call(AES_COMPUTE_FILE, "aesDecrypt", picString, key);
            String picture = HtmlParser.parsePicture(decryptPicHtml);
            if (picture != null) {
                //这里开始下载
                int parallelNum = 10;
                int[] total = new int[500];
                for (int i = 0; i < total.length; i++) {
                    total[i] = i + 1;
                }
                int[] indexBuf;
                List<Integer> retry = new ArrayList<>();
                List<Integer> fail = new ArrayList<>();
                int baseIndex = 0;
                while (retry.size() > 0 || fail.size() == 0) {
                    if (retry.size() > 0 && (retry.size() >= 10 || fail.size() > 0)) {
                        //重试失败
                        List<Integer> success = retry.parallelStream().map(pIndex -> {
                            try {
                                downloadPicture(String.format(picture, pIndex),
                                        dir + File.separator + String.format("%s/%s-%s.jpg",
                                                pair.getKey(), catalogue.getKey(), pIndex));
                                pics.add(String.format("%s-%s.jpg", catalogue.getKey(), pIndex));
                                return pIndex;
                            } catch (FileNotFoundException e) {
                                fail.add(pIndex);
                                return pIndex;
                            } catch (Exception e) {
                                //失败什么也不做
                            }
                            return null;

                        }).filter(Objects::nonNull).collect(Collectors.toList());
                        retry.removeAll(success);
                    } else {
                        //下载新的图片
                        //1 copy数组
                        indexBuf = Arrays.copyOfRange(total,baseIndex,baseIndex + parallelNum);
                        Arrays.stream(indexBuf).filter(Objects::nonNull).parallel().forEach(pIndex -> {
                            try {
                                downloadPicture(String.format(picture,pIndex),
                                        dir + File.separator + String.format("%s/%s-%s.jpg",
                                                pair.getKey(),catalogue.getKey(),pIndex));
                                pics.add(String.format("%s-%s.jpg",catalogue.getKey(),pIndex));
                            } catch (FileNotFoundException e) {
                                fail.add(pIndex);
                            } catch (Exception e) {
                                retry.add(pIndex);
                            }
                        });
                        baseIndex += parallelNum;
                    }

                }
                //这里需要排序
                pics.sort(nameComparator);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pics;
    }

    //链接url下载图片
    private void downloadPicture(String urlPara,String imageName) throws Exception {
        File f = new File(imageName);
        if (f.exists()) {
            return;
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            InputStream inputStream = OKHttpUtils.get(urlPara,true).inputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf,0,len);
            }
            outputStream.close();
            inputStream.close();
        } catch (FileNotFoundException fnfe) {
            Files.delete(f.toPath());
            throw fnfe;
        } catch (IOException e) {
            Files.delete(f.toPath());
            throw new Exception("");
        }
    }

    private OKHttpUtils.HttpResult postRetryWithDefaultProxys(String url, RequestBody body) {
        try {
            return OKHttpUtils.post(url,body,true);
        } catch (Exception e) {
            return postRetryWithDefaultProxys(url, body);
        }
    }



    private static final Comparator<String> nameComparator = (a,b) -> {
        String[] as = a.split("-");
        String[] bs = b.split("-");
        Integer[] asi = {Integer.parseInt(as[0]),Integer.parseInt(as[1].split("\\.")[0])};
        Integer[] bsi = {Integer.parseInt(bs[0]),Integer.parseInt(bs[1].split("\\.")[0])};
        int compare = asi[0].compareTo(bsi[0]);
        if (compare != 0) {
            return compare;
        }
        return asi[1].compareTo(bsi[1]);
    };

}
