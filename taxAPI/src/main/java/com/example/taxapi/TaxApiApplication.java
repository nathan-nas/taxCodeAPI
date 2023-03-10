package com.example.taxapi;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class TaxApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaxApiApplication.class, args);

    }

    @GetMapping(path = "/data")
    @ResponseBody
    public List<Company> data(@RequestParam String date, @RequestParam int page) {
        ArrayList<String> resLinks = new ArrayList<>();
        ArrayList<Company> comList = new ArrayList<>();
        getLinks(date, page, resLinks);
        for (String link : resLinks) {
            comList.add(getInfo(link));
        }
        for (Company cty : comList) {
            cty.show();
        }
        return comList;
    }
    @Autowired
    private ExcelService excelService;
    @GetMapping("export")
    public ResponseEntity<Resource> exportData(@RequestParam String date, @RequestParam int page){
        ArrayList<String> resLinks = new ArrayList<>();
        ArrayList<Company> comList = new ArrayList<>();
        getLinks(date, page, resLinks);
        for (String link : resLinks) {
            comList.add(getInfo(link));
        }

        ResourceDTO resourceDTO = excelService.export(comList);

        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add("Content-Disposition",
                "attachment; filename="+"User.xlsx");

        return ResponseEntity.ok()
                .contentType(resourceDTO.getMediaType())
                .headers(httpHeaders)
                .body(resourceDTO.getResource());
    }
    static void getLinks(String date, int pageNum, ArrayList resLinks) {
        Document doc = null;
        for (int i = 1; i <= pageNum; i++) {
            try {
                doc = Jsoup.connect("https://hosocongty.vn/ngay-" + date + "/page-" + i).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements list = doc.select("ul.hsdn");
            Elements links = list.select("a");
            for (Element el : links) {
                String link = el.toString().split(" ")[1];
                String res = link.replace("\"", "").replace("href=", "");
                if (!resLinks.contains(res)) {
                    resLinks.add(res);
                }
            }
        }
    }

    static Company getInfo(String link) {
        Document profile = null;
        try {
            profile = Jsoup.connect("https://hosocongty.vn/" + link).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Company temp = new Company();
        temp.setLink("https://hosocongty.vn/" + link);
        temp.setTen(profile.select("h1").text());
        Elements li = profile.select("li");
        for (int j = 3; j < 11; j++) {
            Element el = li.get(j);
            if (el.text().contains("?????a ch??? thu???")) {
                String diachi[] = el.text().split(", ");
                String tp = diachi[diachi.length - 1];
                temp.setDiaChi(tp);
                continue;
            }
            if (el.text().contains("??i???n tho???i")) {
                String str[] = el.text().split(":");
                temp.setdThoai(str[str.length - 1].trim());
            }
            if (el.text().contains("Ng??y c???p")) {
                String str[] = el.text().split(":");
                temp.setNgayCap(str[str.length - 1].trim());
            }
            if (el.text().contains("Ng??nh ngh??? ch??nh")) {
                String str[] = el.text().split(":");
                temp.setNganhNgheChinh(str[str.length - 1]);
            }
            if (el.text().contains("M?? s??? thu???")) {
                String str[] = el.text().split(":");
                temp.setTaxCode(str[str.length - 1]);
            }
            if (el.text().contains("?????i di???n ph??p lu???t")) {
                String str[] = el.text().split(":");
                temp.setDaiDien(str[str.length - 1]);
            }
        }
        return temp;
    }


}

