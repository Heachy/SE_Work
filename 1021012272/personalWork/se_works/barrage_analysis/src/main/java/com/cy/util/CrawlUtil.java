package com.cy.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

/**
 * @author Haechi
 */
public class CrawlUtil {

    private static  final int MAX_COUNT = 300;

    private static final int TOP_NUMB = 20;

    public static Map<String,Object> getBarrage(){

        //设置关键字
        String keyWord = "日本核污染水排海";

        //获取数据
        Map<String,Integer> cidMap;

        Map<String,Integer> bvidMap;

        //记录弹幕
        Map<String,Integer> barrageMap =new HashMap<>(MAX_COUNT);

        //获取bvid
        bvidMap=getBvid(keyWord);

        //获取cid
        cidMap=getCid(bvidMap);

        //获取弹幕
        cidMap.forEach((k,v)->{
            String urlCid = "https://api.bilibili.com/x/v1/dm/list.so?oid=" + k;

            Document document;
            try {
                //解析xml
                document = Jsoup.parse(new URL(urlCid), 100000);

                Elements elements = document.getElementsByTag("d");

                elements.forEach(element -> {
                    String barrage = element.text();

                    barrageMap.put(barrage,barrageMap.containsKey(barrage)?barrageMap.get(barrage)+v:v);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //输出
        return output(barrageMap);

    }
    public static Map<String,Object> output(Map<String,Integer> barrageMap){
        //列出数量排名前20的弹幕 并保存到Excel
        List<Map.Entry<String, Integer>> list = new ArrayList<>(barrageMap.entrySet());

        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        Map<String,Object> map=new HashMap<>(2);

        List<String> nameList =new ArrayList<>();

        List<Integer> countList =new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook()) {
            //创建表1
            Sheet sheet = workbook.createSheet("Sheet1");

            //设置标题行
            Row headerRow = sheet.createRow(0);

            Cell headerCell1 = headerRow.createCell(0);

            headerCell1.setCellValue("排名");

            Cell headerCell2 = headerRow.createCell(1);

            headerCell2.setCellValue("弹幕");

            Cell headerCell3 = headerRow.createCell(2);

            headerCell3.setCellValue("数量");

            for (int i = 0; i < TOP_NUMB; i++) {
                System.out.println(list.get(i));

                Row dataRow = sheet.createRow(i+1);

                //排名
                Cell dataCell1 = dataRow.createCell(0);

                dataCell1.setCellValue(i+1);

                //弹幕
                Cell dataCell2 = dataRow.createCell(1);

                dataCell2.setCellValue(list.get(i).getKey());

                nameList.add(list.get(i).getKey());

                //数量
                Cell dataCell3 = dataRow.createCell(2);

                dataCell3.setCellValue(list.get(i).getValue());

                countList.add(list.get(i).getValue());

            }

            //输出
            FileOutputStream fileOutputStream = new FileOutputStream("output.xlsx");

            workbook.write(fileOutputStream);

            fileOutputStream.close();

            map.put("barrageName",nameList);
            map.put("barrageCount",countList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取bvid
     */
    public static Map<String,Integer> getBvid(String keyWord){
        AtomicInteger count = new AtomicInteger();

        Map<String,Integer> bvidMap =new HashMap<>(MAX_COUNT);

        int page=1;

        //获得300个视频的bvid
        while(count.get()<MAX_COUNT){

            //解析数据
            String data = getData(keyWord, page);

            JSONObject jsonObject = JSONObject.parseObject(data);

            List<Map<String,Object>> result = (List<Map<String, Object>>) jsonObject.get("result");

            //记录bvid
            for (Map<String, Object> map : result) {
                String bvid = map.get("bvid").toString();

                if (!bvidMap.containsKey(bvid)){
                    bvidMap.put(bvid,1);
                }else{
                    bvidMap.put(bvid,bvidMap.get(bvid)+1);
                }

                if (count.incrementAndGet()>=MAX_COUNT){
                    break;
                }
            }
            page++;
        }

        return bvidMap;
    }

    /**
     * 获取cid
     */
    public static Map<String,Integer> getCid(Map<String,Integer> bvidMap){
        RestTemplate restTemplate = new RestTemplate();

        Map<String,Integer> cidMap = new HashMap<>(MAX_COUNT);

        //调用接口通过bvid获取cid
        bvidMap.forEach((k,v)->{
            String urlBvid = "https://api.bilibili.com/x/web-interface/view?bvid=" + k;

            String result = restTemplate.getForObject(urlBvid, String.class);

            //解析数据
            Map<String, Object> map = JSON.parseObject(result, Map.class);

            Map<String, Object> data = null;
            if (map != null) {
                data = (Map<String, Object>) map.get("data");
            }

            if (data != null) {
                cidMap.put(data.get("cid").toString(),v);
            }
        });

        return cidMap;
    }

    /**
     * 获取弹幕
     */
    private static String getData(String keyword,int page){
        //设置url
        String url = "https://api.bilibili.com/x/web-interface/wbi/search/type";

        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        HashMap<String, Object> map = new HashMap<>(MAX_COUNT*100);

        //设置参数
        map.put("__refresh__", true);
        map.put("_extra", "");
        map.put("context", "");
        map.put("page", page);
        map.put("page_size", 42);
        map.put("from_source", "");
        map.put("from_spmid", "333.337");
        map.put("platform", "pc");
        map.put("highlight", 1);
        map.put("single_column", 0);
        map.put("keyword", keyword);
        map.put("qv_id", "tDHApclpyoR8Kb26Z5vd6dtPqBFeERUD");
        map.put("ad_resource", 5654);
        map.put("source_tag", 3);
        map.put("gaia_vtoken", "");
        map.put("category_id", "");
        map.put("search_type", "video");
        map.put("dynamic_offset", 42);
        map.put("web_location", 1430654);
        map.put("w_rid", "c706af2a4e4eb7af3d3c13ea997abe70");
        map.put("wts", 1693991559);

        //拼接url
        for (Map.Entry<String, Object> e : map.entrySet()) {
            builder.queryParam(e.getKey(), e.getValue());
        }

        String realUrl = builder.build().toString();

        HttpHeaders headers = new HttpHeaders();

        //设置请求头
        headers.add("accept","application/json, text/plain, */*");
        headers.add("accept-encoding", "gzip, deflate, br");
        headers.add("accept-language", "zh-CN,zh;q=0.9");
        headers.add("cookie","buvid3=9CDA3592-08FD-E035-989C-627B2590138014319infoc; i-wanna-go-back=-1; buvid4=6409C119-573B-B304-0614-770A4F1E9D5B75655-022012519-NTxSyWCwNJM3U95G1DieKg%3D%3D; LIVE_BUVID=AUTO4716431581618404; CURRENT_BLACKGAP=0; buvid_fp_plain=undefined; is-2022-channel=1; b_nut=100; rpdid=|(RYkm|YuYR0J'uYY)~YmuR~; hit-new-style-dyn=1; go_old_video=-1; header_theme_version=CLOSE; CURRENT_PID=601718e0-c887-11ed-b8c9-f1c42fd235d4; nostalgia_conf=-1; FEED_LIVE_VERSION=V8; DedeUserID=302602082; DedeUserID__ckMd5=ff5dd2f25d329f32; b_ut=5; home_feed_column=4; go-back-dyn=0; CURRENT_QUALITY=80; browser_resolution=1366-738; CURRENT_FNVAL=4048; _uuid=D46F3A47-12C9-33A9-52EE-1091861E1A83819186infoc; hit-dyn-v2=1; fingerprint=8edb4bc2423d7030f26cb9b3cc753ce6; bili_ticket=eyJhbGciOiJIUzI1NiIsImtpZCI6InMwMyIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2OTQwODU0ODcsImlhdCI6MTY5MzgyNjI4NywicGx0IjotMX0.tu3cubDlOQvUPJpzpRAuSf5qSx9NP8g1G4tBPwgZypE; bili_ticket_expires=1694085487; SESSDATA=a88927ba%2C1709388853%2C01f0e%2A92__4eI1dXYXDZnBPHOZ4jRegbd2ncdbzoY1tECDXmOHB9xDsKLnz95n-CLry5CZ4WjPqt3QAAUgA; bili_jct=3e9f807567c9e2f0b408a0e7e6c35543; bp_video_offset_302602082=838080421651546288; innersign=0; b_lsid=FD16CF74_18A69AF8775; buvid_fp=8edb4bc2423d7030f26cb9b3cc753ce6; PVID=2; sid=dta52drm");
        headers.add("origin", "https://search.bilibili.com");
        headers.add("referer", "https://search.bilibili.com/all?keyword=%E5%93%88%E5%93%88&from_source=webtop_search&spm_id_from=333.1007&search_source=3&page=2&o=42");
        headers.add("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"92\", \"Microsoft Edge\";v=\"92\"");
        headers.add("sec-ch-ua-mobile", "?0");
        headers.add("sec-fetch-dest", "empty");
        headers.add("sec-fetch-mode", "cors");
        headers.add("sec-fetch-site", "same-site");
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 SLBrowser/8.0.1.4031 SLBChan/103");

        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(null, headers);

        //发送请求
        ResponseEntity<byte[]> forEntity  = restTemplate.exchange(realUrl, HttpMethod.GET, httpEntity, byte[].class, map);

        //解压
        byte[] videoData;
        try {
            videoData = unGzip(
                    new ByteArrayInputStream(Objects.requireNonNull(forEntity.getBody())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String body = new String(videoData, StandardCharsets.UTF_8);


        //解析数据
        JSONObject jsonObject = JSONObject.parseObject(body);

        return String.valueOf(jsonObject.get("data"));
    }

    /**
     * 解压Gzip
     */
    private static byte[] unGzip(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
            byte[] buf = new byte[4096];
            int len;
            while ((len = gzipInputStream.read(buf, 0, buf.length)) != -1) {
                byteArrayOutputStream.write(buf, 0, len);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}
