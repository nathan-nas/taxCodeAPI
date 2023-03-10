package com.example.taxapi;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
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
    @GetMapping("/download/companies.csv")
    public void downloadCSV(HttpServletResponse response,@RequestParam String date, @RequestParam int page) throws IOException{
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; file=customers.csv");
        ArrayList<String> resLinks = new ArrayList<>();
        ArrayList<Company> comList = new ArrayList<>();
        getLinks(date, page, resLinks);
        for (String link : resLinks) {
            comList.add(getInfo(link));
        }
        writeDataToCSV(response.getWriter(),comList);
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
            if (el.text().contains("Địa chỉ thuế")) {
                String diachi[] = el.text().split(", ");
                String tp = diachi[diachi.length - 1];
                temp.setDiaChi(tp);
                continue;
            }
            if (el.text().contains("Điện thoại")) {
                String str[] = el.text().split(":");
                temp.setdThoai(str[str.length - 1].trim());
            }
            if (el.text().contains("Ngày cấp")) {
                String str[] = el.text().split(":");
                temp.setNgayCap(str[str.length - 1].trim());
            }
            if (el.text().contains("Ngành nghề chính")) {
                String str[] = el.text().split(":");
                temp.setNganhNgheChinh(str[str.length - 1]);
            }
            if (el.text().contains("Mã số thuế")) {
                String str[] = el.text().split(":");
                temp.setTaxCode(str[str.length - 1]);
            }
            if (el.text().contains("Đại diện pháp luật")) {
                String str[] = el.text().split(":");
                temp.setDaiDien(str[str.length - 1]);
            }
        }
        return temp;
    }
    static void writeDataToCSV(PrintWriter writer, List<Company> companyList) {
        try (
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                        .withHeader("Link", "Mã Số Thuế", "Tên", "Số Điện Thoại", "Đại Diện", "Địa Chỉ", "Ngày Cấp", "Ngành Nghề Chính"));
        ) {
            for (Company company : companyList) {
                List<String> data = Arrays.asList(
                        company.getLink(),
                        company.getTaxCode(),
                        company.getTen(),
                        company.getdThoai(),
                        company.getDaiDien(),
                        company.getDiaChi(),
                        company.getNgayCap(),
                        company.getNganhNgheChinh()
                );

                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
        } catch (Exception e) {
            System.out.println("Writing CSV error!");
            e.printStackTrace();
        }
    }
}

