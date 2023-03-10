package com.example.taxapi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@SpringBootApplication
public class TaxApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaxApiApplication.class, args);

    }

    @GetMapping(path = "/data")
    @ResponseBody
    public List<Company> data(@RequestParam String date, @RequestParam int page) {
        ArrayList<String> resLinks = new ArrayList<>();
        ArrayList<Company> comList = new ArrayList<>();
        getLinks(date,page,resLinks);
        for (String link : resLinks) {
            comList.add(getInfo(link));
        }
        for(Company cty : comList) {
            cty.show();
        }
        return comList;
    }
    static void getLinks(String date,int pageNum, ArrayList resLinks) {
        Document doc = null;
        for(int i = 1; i <= pageNum;i++ ){
            try {
                doc = Jsoup.connect("https://hosocongty.vn/ngay-"+ date +"/page-"+i).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements list = doc.select("ul.hsdn");
            Elements links = list.select("a");
            for(Element el : links) {
                String link = el.toString().split(" ")[1];
                String res = link.replace("\"","").replace("href=","");
                if(!resLinks.contains(res)) {
                    resLinks.add(res);
                }
            }
        }
    }
    static Company getInfo(String link) {
        Document profile = null;
        try {
            profile = Jsoup.connect("https://hosocongty.vn/"+link).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Company temp = new Company();
        System.out.print("https://hosocongty.vn/"+link+",");
        temp.setLink("https://hosocongty.vn/"+link);
        System.out.print(profile.select("h1").text()+",");
        temp.setTen(profile.select("h1").text());
        Elements li = profile.select("li");
        for(int j = 3; j < 11; j++) {
            Element el = li.get(j);
            if(!el.text().contains("Địa chỉ thuế") && !el.text().contains("Điện thoại")
                    && !el.text().contains("Ngày cấp") && !el.text().contains("Ngành nghề chính")
                    && !el.text().contains("Mã số thuế") && !el.text().contains("Đại diện pháp luật")) continue;
            if(el.text().contains("Địa chỉ thuế")) {
                String diachi[] = el.text().split(", ");
                String tp = diachi[diachi.length-1];
                System.out.print(tp+",");
                temp.setDiaChi(tp);
                continue;
            }
            if(el.text().contains("Điện thoại")) {
                String str[] = el.text().split(":");
                temp.setdThoai(str[str.length-1].trim());
            }
            if(el.text().contains("Ngày cấp")) {
                String str[] = el.text().split(":");
                temp.setNgayCap(str[str.length-1].trim());
            }
            if(el.text().contains("Ngành nghề chính")) {
                String str[] = el.text().split(":");
                temp.setNganhNgheChinh(str[str.length-1]);
            }
            if(el.text().contains("Mã số thuế")) {
                String str[] = el.text().split(":");
                temp.setTaxCode(str[str.length-1]);
            }
            if(el.text().contains("Đại diện pháp luật")) {
                String str[] = el.text().split(":");
                temp.setDaiDien(str[str.length-1]);
            }
            System.out.print(el.text().replace(",","-")+",");
        }
        System.out.println("\n");
        return temp;
    }
}
