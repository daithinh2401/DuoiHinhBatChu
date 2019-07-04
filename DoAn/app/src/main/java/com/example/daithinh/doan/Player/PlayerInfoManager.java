package com.example.daithinh.doan.Player;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;


/**
 * Created by Dai Thinh on 2/27/2017.
 */

public class PlayerInfoManager extends SQLiteOpenHelper {

    public PlayerInfoManager(Context context) {
        super(context, "PlayerInfo", null, 1);
    }

    public void Insert(PlayerInfo player){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO Player VALUES(?,?,?,?,?)";
        SQLiteStatement statement = db.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, player.id);
        statement.bindString(2, player.tenNguoiDung);
        statement.bindBlob(3, player.anhDaiDien);
        statement.bindDouble(4, player.soCauDaVuotQua);
        statement.bindDouble(5, player.soRubyHienTai);

        statement.executeInsert();
    }

    public boolean UpdateTuServer(String ten, int soCau ,int ruBy, String id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("TenNguoiDung" , ten);
        values.put("SoCauDaQua" , soCau);
        values.put("SoRuBy" , ruBy);
        int res = db.update("Player", values, "ID=?" , new String[] {id});
        if(res == 0 ){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean Update(int soCau ,int ruBy, String id){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("SoCauDaQua" , soCau);
        values.put("SoRuBy" , ruBy);
        int res = db.update("Player", values, "ID=?" , new String[] {id});
        if(res == 0 ){
            return false;
        }
        else {
            return true;
        }
    }

    public PlayerInfo Select(String id){
        PlayerInfo p = new PlayerInfo();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from Player where ID like " + id ;

        Cursor c = db.rawQuery(sql , null);

        while (c.moveToNext()){
            String _id = c.getString(0);
            String ten = c.getString(1);
            byte[] hinh = c.getBlob(2);
            int soCau = c.getInt(3);
            int ruby = c.getInt(4);

            p = new PlayerInfo(_id, ten, hinh, soCau, ruby);
        }

        return p;
    }

    public ArrayList<PlayerInfo> getAllPlayerInfo(){
        ArrayList<PlayerInfo> listGameInfo = new ArrayList<PlayerInfo>();
        String sql = "select * from Player";

        // b1. Lay db
        SQLiteDatabase db = getReadableDatabase();

        // b2. Thuc hien truy van, tra ve du lieu tho
        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            String id = c.getString(0);
            String ten = c.getString(1);
            byte[] hinh = c.getBlob(2);
            int soCau = c.getInt(3);
            int ruby = c.getInt(4);

            PlayerInfo p = new PlayerInfo(id, ten, hinh, soCau, ruby);
            listGameInfo.add(p);
        }
        return listGameInfo;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table Player(ID TEXT PRIMARY KEY , TenNguoiDung TEXT , AnhDaiDien BLOB , SoCauDaQua INTEGER , SoRuBy INTEGER)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sql = "delete table if exists Player";
        db.execSQL(sql);
        onCreate(db);
    }
}
