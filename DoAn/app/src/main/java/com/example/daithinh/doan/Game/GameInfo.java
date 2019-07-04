package com.example.daithinh.doan.Game;


/**
 * Created by Dai Thinh on 2/23/2017.
 */

public class GameInfo {
    int soCau;
    int soOChu;
    int linkHinhAnh;
    String dapAnKhongDau;
    String dapAnCoDau;

    public GameInfo(int soCau,int linkHinhAnh, int soOChu, String dapAnKhongDau , String dapAnCoDau ){
        this.soCau = soCau;
        this.soOChu = soOChu;
        this.linkHinhAnh = linkHinhAnh;
        this.dapAnCoDau = dapAnCoDau;
        this.dapAnKhongDau = dapAnKhongDau;
    }

    public int getSoCau() {
        return soCau;
    }
    public int getLinkHinhAnh() {
        return linkHinhAnh;
    }
    public int getSoOChu() {
        return soOChu;
    }
    public String getDapAnKhongDau() {
        return dapAnKhongDau;
    }
    public String getDapAnCoDau() {
        return dapAnCoDau;
    }


    public void setSoCau(int soCau) {
        this.soCau = soCau;
    }
    public void setLinkHinhAnh(int linkHinhAnh) {
        this.linkHinhAnh = linkHinhAnh;
    }
    public void setSoOChu(int soOChu) {
        this.soOChu = soOChu;
    }
    public void setDapAnKhongDau(String dapAnKhongDau) {
        this.dapAnKhongDau = dapAnKhongDau;
    }
    public void setDapAnCoDau(String dapAnCoDau) {
        this.dapAnCoDau = dapAnCoDau;
    }

}
