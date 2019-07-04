package com.example.daithinh.doan.Game;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Dai Thinh on 2/24/2017.
 */

public class GameInfoManager extends SQLiteOpenHelper {

    public GameInfoManager(Context context) {
        super(context, "GameInfo", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table gameinfo(causo integer PRIMARY KEY, link integer, soluongochu integer, dapankhongdau text , dapancodau text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "delete table if exists gameinfo";
        db.execSQL(sql);
        onCreate(db);
    }

    public boolean themCauHoi(GameInfo g){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("causo" , g.soCau);
        values.put("link" , g.linkHinhAnh);
        values.put("soluongochu" , g.soOChu);
        values.put("dapankhongdau" , g.dapAnKhongDau);
        values.put("dapancodau" , g.dapAnCoDau);

        long id = db.insert("gameinfo" , null , values);
        return (id != -1);
    }
    public ArrayList<GameInfo> getAllGameInfo(){
        ArrayList<GameInfo> listGameInfo = new ArrayList<GameInfo>();
        String sql = "select * from gameinfo";

        // b1. Lay db
        SQLiteDatabase db = getReadableDatabase();

        // b2. Thuc hien truy van, tra ve du lieu tho
        Cursor c = db.rawQuery(sql, null);

        while (c.moveToNext()) {
            int causo = c.getInt(0);
            int link = c.getInt(1);
            int soluongochu = c.getInt(2);
            String dapankhongdau = c.getString(3);
            String dapancodau = c.getString(4);

            GameInfo g = new GameInfo(causo, link, soluongochu ,dapankhongdau ,dapancodau);
            listGameInfo.add(g);
        }
        return listGameInfo;
    }
}
