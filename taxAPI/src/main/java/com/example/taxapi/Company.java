package com.example.taxapi;

public class Company {
    private String link;
    private String ten;
    private String taxCode;
    private String dThoai;
    private String diaChi;
    private String ngayCap;
    private String nganhNgheChinh;
    private String daiDien;

    public Company (String link, String ten, String taxCode, String dThoai, String diaChi,String ngayCap, String nganhNgheChinh, String daiDien) {
        this.link = link;
        this.ten = ten;
        this.taxCode = taxCode;
        this.dThoai = dThoai;
        this.diaChi = diaChi;
        this.ngayCap = ngayCap;
        this.nganhNgheChinh = nganhNgheChinh;
        this.daiDien = daiDien;
    }

    public  Company(){}

    public void show(){
        System.out.println(link+","+ten+","+taxCode+","+dThoai+","+diaChi+","+ngayCap+","+nganhNgheChinh+","+daiDien);
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public void setdThoai(String dThoai) {
        this.dThoai = dThoai;
    }

    public void setDaiDien(String daiDien) {
        this.daiDien = daiDien;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public void setNganhNgheChinh(String nganhNgheChinh) {
        this.nganhNgheChinh = nganhNgheChinh;
    }

    public void setNgayCap(String ngayCap) {
        this.ngayCap = ngayCap;
    }

    public String getDaiDien() {
        return daiDien;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public String getdThoai() {
        return dThoai;
    }

    public String getLink() {
        return link;
    }

    public String getNganhNgheChinh() {
        return nganhNgheChinh;
    }

    public String getNgayCap() {
        return ngayCap;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public String getTen() {
        return ten;
    }
}
