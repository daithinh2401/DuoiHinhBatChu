package com.example.daithinh.doan.Player;

/**
 * Created by Dai Thinh on 2/27/2017.
 */

public class PlayerInfo {
    String id;
    String tenNguoiDung;
    byte[] anhDaiDien;
    int soCauDaVuotQua;
    int soRubyHienTai;

    public PlayerInfo(){}
    public PlayerInfo(String id , String tenNguoiDung , byte[] anhDaiDien , int soCauDaVuotQua , int soRubyHienTai){
        this.id = id;
        this.tenNguoiDung = tenNguoiDung;
        this.anhDaiDien = anhDaiDien;
        this.soCauDaVuotQua = soCauDaVuotQua;
        this.soRubyHienTai = soRubyHienTai;
    }

    public String getId() {
        return id;
    }
    public String getTenNguoiDung() {
        return tenNguoiDung;
    }
    public byte[] getAnhDaiDien() {
        return anhDaiDien;
    }
    public int getSoCauDaVuotQua() {
        return soCauDaVuotQua;
    }
    public int getSoRubyHienTai() {
        return soRubyHienTai;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setTenNguoiDung(String tenNguoiDung) {
        this.tenNguoiDung = tenNguoiDung;
    }
    public void setAnhDaiDien(byte[] anhDaiDien) {
        this.anhDaiDien = anhDaiDien;
    }
    public void setSoCauDaVuotQua(int soCauDaVuotQua) {
        this.soCauDaVuotQua = soCauDaVuotQua;
    }
    public void setSoRubyHienTai(int soRubyHienTai) {
        this.soRubyHienTai = soRubyHienTai;
    }
}
