package com.example.daithinh.doan.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daithinh.doan.Game.GameInfo;
import com.example.daithinh.doan.Game.GameInfoManager;
import com.example.daithinh.doan.Player.PlayerInfo;
import com.example.daithinh.doan.Player.PlayerInfoManager;
import com.example.daithinh.doan.R;
import com.example.daithinh.doan.Service.MyService;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.ProfilePictureView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends Activity {
    private SoundPool soundPool;
    private AudioManager audioManager;

    private static final int MAX_STREAMS = 5;
    private static final int streamType = AudioManager.STREAM_MUSIC;
    private boolean loaded;
    private float volume;

    private int soundGround;
    private int soundBravo;
    private int soundButton;

    ArrayList<PlayerInfo> listPlayer ;
    Bitmap bitmap;
    PlayerInfoManager playerManager ;
    GameInfoManager manager;
    private ImageView image;
    private TextView txt_1, txt_2, txt_3, txt_4, txt_5, txt_6, txt_7, txt_8,
            txt_9, txt_10, txt_11, txt_12, txt_13, txt_14, txt_15, txt_16;

    private Button btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8,
            btn_9, btn_10, btn_11, btn_12, btn_13, btn_14,btn_15;

    private Button btn_Back , btn_Help, btn_Facebook;
    private TextView txt_ShowQuestNumber , txt_ShowRuby;

    private int count = 0;
    int ruby = 0;
    int soOChu;

    SharedPreferences pre;

    Random ran = new Random();
    ArrayList<TextView> listText; // Mảng chứa các ô chữ ( TextView )
    ArrayList<Button> btnRandom; // Mảng Button chứa các kí tự Random
    ArrayList<Button> ans; // Các Button khi được nhấn sẽ lưu vào mảng này
    ArrayList<Integer> arrSo ; // Mảng số tự nhiên được tạo Random
    ArrayList<Character> dapan; // Mảng chứa đáp án của từng câu
    ArrayList<GameInfo> listGameInfo;


    public void capQuyen(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GameActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 50);

        }
        else {
            chupHinh();
        }

    }
    private Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
    private static File saveBitmap(Bitmap bm, String fileName){
        final String path = "/sdcard/DuoiHinhBatChu/" + "screenshot_game.jpg";
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    public boolean troGiup(){
        try {
            if (ruby >= 10) {
                ruby -= 10;
                txt_ShowRuby.setText(ruby + "");
                for (int i = 0; i < dapan.size(); i++) {
                    if (!(listText.get(i).getText().toString().equals(dapan.get(i).toString()))) {
                        listText.get(i).setText(dapan.get(i).toString());
                        listText.get(i).setTextColor(Color.WHITE);
                        for(int j = 0; j < btnRandom.size() ; j++){
                            if(listText.get(i).getText().toString().equals(btnRandom.get(j).getText().toString())){
                                btnRandom.get(j).setVisibility(View.INVISIBLE);
                                if (ans.size() <= 0) {
                                    ans.add(btn_1);
                                } else {
                                    if (kiemTraPhanTu() != -1) {
                                        ans.set(kiemTraPhanTu(), btn_1);
                                    } else {
                                        ans.add(btn_1);
                                    }
                                }
                                ans.get(i).setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                        listText.get(i).setEnabled(false);
                        break;
                    }
                }
                return true;
            } else {
                Toast.makeText(GameActivity.this, "Bạn không đủ Ruby !! ", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){
            Toast.makeText(GameActivity.this, e.toString() , Toast.LENGTH_LONG).show();
        }
        return false;
    }
    public void layTuSharedPreferences(){
        SharedPreferences pre = getSharedPreferences("player_data", MODE_PRIVATE);
        count = pre.getInt("So cau hien tai" , 0);
        ruby = pre.getInt("Ruby" , 0);
    }
    public void luuSharedPreferences(){
        SharedPreferences pre = getSharedPreferences("player_data", MODE_PRIVATE);
        SharedPreferences.Editor edit = pre.edit();
        edit.putInt("So cau hien tai" , count);
        edit.putInt("Ruby" , ruby);
        playerManager = new PlayerInfoManager(getApplicationContext());
        playerManager.Update(count , ruby , pre.getString("ID" , null));
        edit.commit();
    }


    public void chayDatabase(){
            manager = new GameInfoManager(this);
            try {
                manager.themCauHoi(new GameInfo(1, R.drawable.taihoa, 6, "TAIHOA", "TAI HỌA"));
                manager.themCauHoi(new GameInfo(2, R.drawable.thankhoc, 8, "THANKHOC", "THANKHÓC"));
                manager.themCauHoi(new GameInfo(3, R.drawable.bactinh, 7, "BACTINH", "BẠC TÌNH"));
                manager.themCauHoi(new GameInfo(4, R.drawable.badausautay, 11, "BADAUSAUTAY", "BA ĐẦU SÁU TAY"));
                manager.themCauHoi(new GameInfo(5, R.drawable.baothuc, 7, "BAOTHUC", "BÁO THỨC"));
                manager.themCauHoi(new GameInfo(6, R.drawable.batcoc, 6, "BATCOC", "BẮT CÓC"));
                manager.themCauHoi(new GameInfo(7, R.drawable.batron, 6, "BATRON", "BA TRỢN"));
                manager.themCauHoi(new GameInfo(8, R.drawable.binhdoan, 8, "BINHDOAN", "BINH ĐOÀN"));
                manager.themCauHoi(new GameInfo(9, R.drawable.bongda, 6, "BONGDA", "BÓNG ĐÁ"));
                manager.themCauHoi(new GameInfo(10, R.drawable.cacao, 5, "CACAO", "CA CAO"));
                manager.themCauHoi(new GameInfo(11, R.drawable.camchan, 7, "CAMCHAN", "CẦM CHÂN"));
                manager.themCauHoi(new GameInfo(12, R.drawable.canhdong, 8, "CANHDONG", "CÁNH ĐỒNG"));
                manager.themCauHoi(new GameInfo(13, R.drawable.cannhac, 7, "CANNHAC", "CÂN NHẮC"));
                manager.themCauHoi(new GameInfo(14, R.drawable.caohoc, 6, "CAOHOC", "CAO HỌC"));
                manager.themCauHoi(new GameInfo(15, R.drawable.caumay, 6, "CAUMAY", "CẦU MÂY"));
                manager.themCauHoi(new GameInfo(16, R.drawable.chidiem, 7, "CHIDIEM", "CHỈ ĐIỂM"));
                manager.themCauHoi(new GameInfo(17, R.drawable.chithi, 6, "CHITHI", "CHỈ THỊ"));
                manager.themCauHoi(new GameInfo(18, R.drawable.chotreomeoday, 13, "CHOTREOMEODAY", "CHÓ TREO MÈO ĐẬY"));
                manager.themCauHoi(new GameInfo(19, R.drawable.congtrai, 8, "CONGTRAI", "CÔNG TRÁI"));
                manager.themCauHoi(new GameInfo(20, R.drawable.dauthu, 6, "DAUTHU", "ĐẦU THÚ"));
                manager.themCauHoi(new GameInfo(21, R.drawable.dautranh, 8, "DAUTRANH", "ĐẤU TRANH"));
                manager.themCauHoi(new GameInfo(22, R.drawable.bandao, 6, "BANDAO", "BÁN ĐẢO"));
                manager.themCauHoi(new GameInfo(23, R.drawable.tongbithu, 9, "TONGBITHU", "TỔNG BÍ THƯ"));
                manager.themCauHoi(new GameInfo(24, R.drawable.bomtan, 6, "BOMTAN", "BOM TẤN"));
                manager.themCauHoi(new GameInfo(25, R.drawable.caucu, 5, "CAUCU", "CÂU CÚ"));
                manager.themCauHoi(new GameInfo(26, R.drawable.caugio, 6, "CAUGIO", "CÂU GIỜ"));
                manager.themCauHoi(new GameInfo(27, R.drawable.chamcong, 8, "CHAMCONG", "CHẤM CÔNG"));
                manager.themCauHoi(new GameInfo(28, R.drawable.coloa, 5, "COLOA", "CỔ LOA"));
                manager.themCauHoi(new GameInfo(29, R.drawable.dothi, 5, "DOTHI", "ĐÔ THỊ"));
                manager.themCauHoi(new GameInfo(30, R.drawable.noigian, 7, "NOIGIAN", "NỘI GIÁN"));
                manager.themCauHoi(new GameInfo(31, R.drawable.khaucung, 8, "KHAUCUNG", "KHẨU CUNG"));
                manager.themCauHoi(new GameInfo(32, R.drawable.kinhdo, 6, "KINHDO", "KINH ĐỘ"));
                manager.themCauHoi(new GameInfo(33, R.drawable.laothanh, 8, "LAOTHANH", "LÃO THÀNH"));
                manager.themCauHoi(new GameInfo(34, R.drawable.lucduc, 6, "LUCDUC", "LỤC ĐỤC"));
                manager.themCauHoi(new GameInfo(35, R.drawable.saobang, 7, "SAOBANG", "SAO BĂNG"));
                manager.themCauHoi(new GameInfo(36, R.drawable.soctrang, 8, "SOCTRANG", "SÓC TRĂNG"));
                manager.themCauHoi(new GameInfo(37, R.drawable.tatyeu, 6, "TATYEU", "TẤT YẾU"));
                manager.themCauHoi(new GameInfo(38, R.drawable.tinhtruong, 10, "TINHTRUONG", "TÌNH TRƯỜNG"));
                manager.themCauHoi(new GameInfo(39, R.drawable.tuongthich, 10, "TUONGTHICH", "TƯƠNG THÍCH"));
                manager.themCauHoi(new GameInfo(40, R.drawable.dongcam, 7, "DONGCAM", "ĐỒNG CẢM"));
                manager.themCauHoi(new GameInfo(41, R.drawable.dongcamcongkho, 14, "DONGCAMCONGKHO", "ĐỒNGCAM CỘNGKHỔ"));
                manager.themCauHoi(new GameInfo(42, R.drawable.epcung, 6, "EPCUNG", "ÉP CUNG"));
                manager.themCauHoi(new GameInfo(43, R.drawable.gaungua, 7, "GAUNGUA", "GẤU NGỰA"));
                manager.themCauHoi(new GameInfo(44, R.drawable.gokien, 6, "GOKIEN", "GÕ KIẾN"));
                manager.themCauHoi(new GameInfo(45, R.drawable.guongchieuhau, 13, "GUONGCHIEUHAU", "GƯƠNG CHIẾU HẬU"));
                manager.themCauHoi(new GameInfo(46, R.drawable.haclao, 6, "HACLAO", "HẮC LÀO"));
                manager.themCauHoi(new GameInfo(47, R.drawable.hamho, 5, "HAMHO", "HÀM HỒ"));
                manager.themCauHoi(new GameInfo(48, R.drawable.hangngu, 7, "HANGNGU", "HÀNG NGŨ"));
                manager.themCauHoi(new GameInfo(49, R.drawable.hatnhan, 7, "HATNHAN", "HẠT NHÂN"));
                manager.themCauHoi(new GameInfo(50, R.drawable.hungthu, 7, "HUNGTHU", "HỨNG THÚ"));
                manager.themCauHoi(new GameInfo(51, R.drawable.khongkich, 9, "KHONGKICH", "KHÔNG KÍCH"));
                manager.themCauHoi(new GameInfo(52, R.drawable.langthang, 9, "LANGTHANG", "LANG THANG"));
                manager.themCauHoi(new GameInfo(53, R.drawable.laothanh, 8, "LAOTHANH", "LÃO THÀNH"));
                manager.themCauHoi(new GameInfo(54, R.drawable.lucduc, 6, "LUCDUC", "LỤC ĐỤC"));
                manager.themCauHoi(new GameInfo(55, R.drawable.luclac, 6, "LUCLAC", "LỤC LẠC"));
                manager.themCauHoi(new GameInfo(56, R.drawable.muabongmay, 10, "MUABONGMAY", "MƯA BÓNG MÂY"));
                manager.themCauHoi(new GameInfo(57, R.drawable.muinhon, 7, "MUINHON", "MŨI NHỌN"));
                manager.themCauHoi(new GameInfo(58, R.drawable.nambancau, 9, "NAMBANCAU", "NAM  BÁN CẦU"));
                manager.themCauHoi(new GameInfo(59, R.drawable.nemdagiautay, 12, "NEMDAGIAUTAY", "NÉM ĐÁ  GIẤU TAY"));
                manager.themCauHoi(new GameInfo(60, R.drawable.ngangu, 6, "NGANGU", "NGÃ NGŨ"));
                manager.themCauHoi(new GameInfo(61, R.drawable.nghesinhandan, 13, "NGHESINHANDAN", "NGHỆ SĨ NHÂN DÂN"));
                manager.themCauHoi(new GameInfo(62, R.drawable.nguao, 5, "NGUAO", "NGỰA Ô"));
                manager.themCauHoi(new GameInfo(63, R.drawable.ngucoc, 6, "NGUCOC", "NGŨ CỐC"));
                manager.themCauHoi(new GameInfo(64, R.drawable.nhachoctroi, 11, "NHACHOCTROI", "NHÀ  CHỌC TRỜI"));
                manager.themCauHoi(new GameInfo(65, R.drawable.nhahat, 6, "NHAHAT", "NHÀ HÁT"));
                manager.themCauHoi(new GameInfo(66, R.drawable.nhosi, 5, "NHOSI", "NHO SĨ"));
                manager.themCauHoi(new GameInfo(67, R.drawable.noigian, 7, "NOIGIAN", "NỘI GIÁN"));
                manager.themCauHoi(new GameInfo(68, R.drawable.noithat, 7, "NOITHAT", "NỘI THẤT"));
                manager.themCauHoi(new GameInfo(69, R.drawable.quanham, 7, "QUANHAM", "QUÂN HÀM"));
                manager.themCauHoi(new GameInfo(70, R.drawable.sangsua, 7, "SANGSUA", "SÁNG SỦA"));
                manager.themCauHoi(new GameInfo(71, R.drawable.saobang, 7, "SAOBANG", "SAO BĂNG"));
                manager.themCauHoi(new GameInfo(72, R.drawable.saunang, 7, "SAUNANG", "SÂU NẶNG"));
                manager.themCauHoi(new GameInfo(73, R.drawable.thongtan, 8, "THONGTAN", "THÔNG TẤN"));
                manager.themCauHoi(new GameInfo(74, R.drawable.tinhtao, 7, "TINHTAO", "TỈNH TÁO"));
                manager.themCauHoi(new GameInfo(75, R.drawable.tranhsondau, 11, "TRANHSONDAU", "TRANH SƠN DẦU"));
                manager.themCauHoi(new GameInfo(76, R.drawable.tranhthu, 8, "TRANHTHU", "TRANH THỦ"));
                manager.themCauHoi(new GameInfo(77, R.drawable.xichlo, 6, "XICHLO", "XÍCH LÔ"));
            }catch (Exception e){
                Log.e("Lỗi","Đã có csdl");
            }

        listGameInfo = manager.getAllGameInfo();
        soOChu = listGameInfo.get(count).getSoOChu();
    }
    public void taoMangDapAn(int so){
        String da = listGameInfo.get(count).getDapAnKhongDau().toString();
        char[] ch = new char[so + 1];
        da.getChars(0, da.length(), ch , 0);
        for(int i = 0 ; i < ch.length; i ++ ){
            dapan.add(ch[i]);
        }

    } // Mảng danh sách chứa các kí tự đáp án
    public boolean kiemTraDapAn(int so){
            for(int i = 0 ; i < so; i++){
                if(!listText.get(i).getText().toString().equals(dapan.get(i) + "")){
                    return false;
                }
            }
        return true;
    } // So sánh mảng Ô chữ ( Text View ) với mảng đáp án , giống nhau trả về true
    public boolean kiemTraOChuDuocNhapHet(int so){
        for(int i = 0 ; i < so; i++){
            if(listText.get(i).getText().toString().equals("")){
                return false;
            }
        }
        return true;
    } // Kiểm tra xem tất cả các ô có được nhập hết chưa
    public void themDapAnVaoOChu(int so){
        for(int i = 0; i < so; i ++){
            btnRandom.get(arrSo.get(i)).setText(dapan.get(i) + "");
        }
    } // Thêm các kí tự trong mảng đáp án vào các Button
    public void taoMangSoRanDom(int so){
        arrSo = new ArrayList<Integer>();
        int inew;
        for(int i = 0 ; i < so ;){
            inew = ran.nextInt(btnRandom.size());
            if(!arrSo.contains(inew)){
                i++;
                arrSo.add(inew);
            }
        }
    } // Tạo ra mảng các số ngẫu nhiên từ 0 tới số ô chữ, các số không trùng nhau
    public void themChuCaiRanDom(int so_O_Chu){
        for(int i = 0 ; i < btnRandom.size(); i ++){
            if((ranDom() != 'F') && (ranDom() != 'W') && (ranDom() != 'J') && (ranDom() != 'Z')){
                btnRandom.get(i).setText(ranDom() + "");
            }
        }

        themDapAnVaoOChu(so_O_Chu);
    } // Tạo ra các chữ cái ngẫu nhiên, đưa vào các Button

    public char ranDom(){
        return (char)(ran.nextInt(22) + 'A');
    } // Hàm trả về 1 kí tự ngẫu nhiên trong bảng chữ cái tiếng Anh
    public void taoOChu( int so){
        for(int i = 0 ; i < so ; i++){
            listText.get(i).setVisibility(View.VISIBLE);
        }
    } // Đáp án có bao nhiêu chữ thì tạo bấy nhiêu ô
    public int kiemTraPhanTu(){
        for(int i = 0 ; i < ans.size(); i ++){
            if(ans.get(i) == btn_15){
                return i;
            }
        }
        return -1;
    } // Kiểm tra button tạm btn_15
    public void layIdCuaButton(){
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_3 = (Button) findViewById(R.id.btn_3);
        btn_4 = (Button) findViewById(R.id.btn_4);
        btn_5 = (Button) findViewById(R.id.btn_5);
        btn_6 = (Button) findViewById(R.id.btn_6);
        btn_7 = (Button) findViewById(R.id.btn_7);
        btn_8 = (Button) findViewById(R.id.btn_8);
        btn_9 = (Button) findViewById(R.id.btn_9);
        btn_10 = (Button) findViewById(R.id.btn_10);
        btn_11 = (Button) findViewById(R.id.btn_11);
        btn_12 = (Button) findViewById(R.id.btn_12);
        btn_13 = (Button) findViewById(R.id.btn_13);
        btn_14 = (Button) findViewById(R.id.btn_14);
    } // Lấy id button
    public void layIdCuaTextView(){
        txt_1 = (TextView) findViewById(R.id.txt_1);
        txt_2 = (TextView) findViewById(R.id.txt_2);
        txt_3 = (TextView) findViewById(R.id.txt_3);
        txt_4 = (TextView) findViewById(R.id.txt_4);
        txt_5 = (TextView) findViewById(R.id.txt_5);
        txt_6 = (TextView) findViewById(R.id.txt_6);
        txt_7 = (TextView) findViewById(R.id.txt_7);
        txt_8 = (TextView) findViewById(R.id.txt_8);
        txt_9 = (TextView) findViewById(R.id.txt_9);
        txt_10 = (TextView) findViewById(R.id.txt_10);
        txt_11 = (TextView) findViewById(R.id.txt_11);
        txt_12 = (TextView) findViewById(R.id.txt_12);
        txt_13 = (TextView) findViewById(R.id.txt_13);
        txt_14 = (TextView) findViewById(R.id.txt_14);
        txt_15 = (TextView) findViewById(R.id.txt_15);
        txt_16 = (TextView) findViewById(R.id.txt_16);


    } // Lấy id textview
    public void themTextViewVaoList(){
        listText.add(txt_1);
        listText.add(txt_2);
        listText.add(txt_3);
        listText.add(txt_4);
        listText.add(txt_5);
        listText.add(txt_6);
        listText.add(txt_7);
        listText.add(txt_8);
        listText.add(txt_9);
        listText.add(txt_10);
        listText.add(txt_11);
        listText.add(txt_12);
        listText.add(txt_13);
        listText.add(txt_14);
        listText.add(txt_15);
        listText.add(txt_16);

        txt_1.setVisibility(View.GONE);
        txt_2.setVisibility(View.GONE);
        txt_3.setVisibility(View.GONE);
        txt_4.setVisibility(View.GONE);
        txt_5.setVisibility(View.GONE);
        txt_6.setVisibility(View.GONE);
        txt_7.setVisibility(View.GONE);
        txt_8.setVisibility(View.GONE);
        txt_9.setVisibility(View.GONE);
        txt_10.setVisibility(View.GONE);
        txt_11.setVisibility(View.GONE);
        txt_12.setVisibility(View.GONE);
        txt_13.setVisibility(View.GONE);
        txt_14.setVisibility(View.GONE);
        txt_15.setVisibility(View.GONE);
        txt_16.setVisibility(View.GONE);

        txt_1.setEnabled(true);
        txt_2.setEnabled(true);
        txt_3.setEnabled(true);
        txt_4.setEnabled(true);
        txt_5.setEnabled(true);
        txt_6.setEnabled(true);
        txt_7.setEnabled(true);
        txt_8.setEnabled(true);
        txt_9.setEnabled(true);
        txt_10.setEnabled(true);
        txt_11.setEnabled(true);
        txt_12.setEnabled(true);
        txt_13.setEnabled(true);
        txt_14.setEnabled(true);
        txt_15.setEnabled(true);
        txt_16.setEnabled(true);

        txt_1.setTextColor(Color.YELLOW);
        txt_2.setTextColor(Color.YELLOW);
        txt_3.setTextColor(Color.YELLOW);
        txt_4.setTextColor(Color.YELLOW);
        txt_5.setTextColor(Color.YELLOW);
        txt_6.setTextColor(Color.YELLOW);
        txt_7.setTextColor(Color.YELLOW);
        txt_8.setTextColor(Color.YELLOW);
        txt_9.setTextColor(Color.YELLOW);
        txt_10.setTextColor(Color.YELLOW);
        txt_11.setTextColor(Color.YELLOW);
        txt_12.setTextColor(Color.YELLOW);
        txt_13.setTextColor(Color.YELLOW);
        txt_14.setTextColor(Color.YELLOW);
        txt_15.setTextColor(Color.YELLOW);
        txt_16.setTextColor(Color.YELLOW);





    } // Đưa các text view vào ds để quản lí
    public void themButtonVaoList(){
        btnRandom.add(btn_1);
        btnRandom.add(btn_2);
        btnRandom.add(btn_3);
        btnRandom.add(btn_4);
        btnRandom.add(btn_5);
        btnRandom.add(btn_6);
        btnRandom.add(btn_7);
        btnRandom.add(btn_8);
        btnRandom.add(btn_9);
        btnRandom.add(btn_10);
        btnRandom.add(btn_11);
        btnRandom.add(btn_12);
        btnRandom.add(btn_13);
        btnRandom.add(btn_14);
    } // Đưa các button vào ds để quản lí
    public void xuLyNhanButton(){
          btn_1.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_1.setVisibility(View.INVISIBLE);

                      if (ans.size() <= 0) {
                          ans.add(btn_1);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_1);
                          } else {
                              ans.add(btn_1);
                          }
                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_1.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {

                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }

                  }
              }
          });

          btn_2.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_2.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_2);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_2);
                          } else {
                              ans.add(btn_2);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_2.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_3.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_3.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_3);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_3);
                          } else {
                              ans.add(btn_3);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_3.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_4.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_4.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_4);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_4);
                          } else {
                              ans.add(btn_4);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_4.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_5.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_5.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_5);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_5);
                          } else {
                              ans.add(btn_5);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_5.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_6.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_6.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_6);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_6);
                          } else {
                              ans.add(btn_6);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_6.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_7.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_7.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_7);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_7);
                          } else {
                              ans.add(btn_7);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_7.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_8.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_8.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_8);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_8);
                          } else {
                              ans.add(btn_8);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_8.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_9.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_9.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_9);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_9);
                          } else {
                              ans.add(btn_9);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_9.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_10.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_10.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_10);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_10);
                          } else {
                              ans.add(btn_10);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_10.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_11.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_11.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_11);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_11);
                          } else {
                              ans.add(btn_11);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_11.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_12.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_12.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_12);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_12);
                          } else {
                              ans.add(btn_12);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_12.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_13.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_13.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_13);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_13);
                          } else {
                              ans.add(btn_13);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_13.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

          btn_14.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  playSoundButton();
                  if (!(kiemTraOChuDuocNhapHet(soOChu))) {
                      btn_14.setVisibility(View.INVISIBLE);
                      if (ans.size() <= 0) {
                          ans.add(btn_14);
                      } else {
                          if (kiemTraPhanTu() != -1) {
                              ans.set(kiemTraPhanTu(), btn_14);
                          } else {
                              ans.add(btn_14);
                          }

                      }
                      for (int i = 0; i < listText.size(); i++) {
                          if (listText.get(i).getText().toString().equals("")) {
                              listText.get(i).setText(btn_14.getText().toString());
                              break;
                          }
                      }
                      if(kiemTraOChuDuocNhapHet(soOChu)){
                          if(kiemTraDapAn(soOChu)) {
                              congratulation();
                              playSoungBravo();
                          }
                          else {
                              Toast.makeText(GameActivity.this ,"Lêu lêu , sai rồi :)))" , Toast.LENGTH_LONG).show();
                              playSoundLaugh();
                          }
                      }
                  }
              }
          });

    } // Xử lý sự kiện nhất button
    public void xuLyNhanTextView(){
        txt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_1.getText().toString().equals(""))) {
                    txt_1.setText("");
                    ans.get(0).setVisibility(View.VISIBLE);
                    ans.set(0, btn_15);
                }
            }
        });

        txt_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_2.getText().toString().equals(""))) {
                    txt_2.setText("");
                    ans.get(1).setVisibility(View.VISIBLE);
                    ans.set(1,btn_15);
                }
            }
        });

        txt_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_3.getText().toString().equals(""))) {
                    txt_3.setText("");
                    ans.get(2).setVisibility(View.VISIBLE);
                    ans.set(2,btn_15);
                }
            }
        });

        txt_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_4.getText().toString().equals(""))) {
                    txt_4.setText("");
                    ans.get(3).setVisibility(View.VISIBLE);
                    ans.set(3,btn_15);
                }
            }
        });

        txt_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_5.getText().toString().equals(""))) {
                    txt_5.setText("");
                    ans.get(4).setVisibility(View.VISIBLE);
                    ans.set(4,btn_15);
                }
            }
        });

        txt_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_6.getText().toString().equals(""))) {
                    txt_6.setText("");
                    ans.get(5).setVisibility(View.VISIBLE);
                    ans.set(5,btn_15);
                }
            }
        });

        txt_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_7.getText().toString().equals(""))) {
                    txt_7.setText("");
                    ans.get(6).setVisibility(View.VISIBLE);
                    ans.set(6,btn_15);
                }
            }
        });

        txt_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_8.getText().toString().equals(""))) {
                    txt_8.setText("");
                    ans.get(7).setVisibility(View.VISIBLE);
                    ans.set(7,btn_15);
                }
            }
        });

        txt_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_9.getText().toString().equals(""))) {
                    txt_9.setText("");
                    ans.get(8).setVisibility(View.VISIBLE);
                    ans.set(8,btn_15);
                }
            }
        });

        txt_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_10.getText().toString().equals(""))) {
                    txt_10.setText("");
                    ans.get(9).setVisibility(View.VISIBLE);
                    ans.set(9,btn_15);
                }
            }
        });

        txt_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_11.getText().toString().equals(""))) {
                    txt_11.setText("");
                    ans.get(10).setVisibility(View.VISIBLE);
                    ans.set(10,btn_15);
                }
            }
        });

        txt_12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_12.getText().toString().equals(""))) {
                    txt_12.setText("");
                    ans.get(11).setVisibility(View.VISIBLE);
                    ans.set(11,btn_15);
                }
            }
        });

        txt_13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_13.getText().toString().equals(""))) {
                    txt_13.setText("");
                    ans.get(12).setVisibility(View.VISIBLE);
                    ans.set(12,btn_15);
                }
            }
        });

        txt_14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_14.getText().toString().equals(""))) {
                    txt_14.setText("");
                    ans.get(13).setVisibility(View.VISIBLE);
                    ans.set(13,btn_15);
                }
            }
        });

        txt_15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_15.getText().toString().equals(""))) {
                    txt_15.setText("");
                    ans.get(14).setVisibility(View.VISIBLE);
                    ans.set(14,btn_15);
                }
            }
        });

        txt_16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(txt_16.getText().toString().equals(""))) {
                    txt_16.setText("");
                    ans.get(15).setVisibility(View.VISIBLE);
                    ans.set(15,btn_15);
                }
            }
        });
    } // Xử lý sự kiện nhất textview

    public void congratulation(){
        Intent intent = new Intent(GameActivity.this, CongratulationActivity.class);
        String dapancodau = listGameInfo.get(count).getDapAnCoDau();
        intent.putExtra("dapAnCoDau", dapancodau);
        startActivityForResult(intent ,1);
        GameActivity.this.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);


        btn_Back = (Button)findViewById(R.id.btnBack);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameActivity.this.onPause();
                Intent data = new Intent();

                setResult(10 , data);
                finish();
            }
        });

        btn_Help = (Button)findViewById(R.id.btnHelp);
        btn_Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder b = new AlertDialog.Builder(GameActivity.this);

                b.setTitle("Mở ô đáp án");
                b.setMessage("Mở một ô đáp án, bạn sẽ bị trừ 10 Ruby, bạn có chắc muốn mở ?");
                b.setPositiveButton("Có", new DialogInterface. OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        troGiup();
                    }});

                b.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });

                b.create().show();
            }
        });

        btn_Facebook = (Button)findViewById(R.id.btnFacebook);
        btn_Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capQuyen();

            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        layTuSharedPreferences();
        chayDatabase();
        chayAmThanh();


        listText = new ArrayList<TextView>();
        listText.clear();
        ans = new ArrayList<Button>();
        ans.clear();
        btnRandom = new ArrayList<Button>();
        btnRandom.clear();
        dapan = new ArrayList<Character>();
        dapan.clear();



        txt_ShowRuby = (TextView) findViewById(R.id.txt_ShowRuby);
        txt_ShowRuby.setText(ruby + "");

        txt_ShowQuestNumber = (TextView) findViewById(R.id.txtShowQuestNumber);
        txt_ShowQuestNumber.setText((count + 1) + "");


        image = (ImageView)findViewById(R.id.imageQuestion);
        image.setImageResource(listGameInfo.get(count).getLinkHinhAnh());

        layIdCuaTextView();
        layIdCuaButton();
        themTextViewVaoList();
        themButtonVaoList();
        taoMangDapAn(soOChu);
        taoMangSoRanDom(soOChu);
        themChuCaiRanDom(soOChu);
        taoOChu(soOChu);
        xuLyNhanButton();
        xuLyNhanTextView();

        playerManager = new PlayerInfoManager(getApplicationContext());
        listPlayer = new ArrayList<PlayerInfo>();
        listPlayer = playerManager.getAllPlayerInfo();

        for(PlayerInfo i : listPlayer){
            insertPlayer(i);
        }


        for(int i = 0 ; i < listText.size() ; i ++){
            listText.get(i).setText("");
        }
        for(int i  = 0 ; i < btnRandom.size(); i++){
            btnRandom.get(i).setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onPause() {
        super.onPause();
        luuSharedPreferences();
        pre = getSharedPreferences("player_data" , MODE_PRIVATE);
        updatePlayer(pre.getString("ID" , "") , pre.getString("TenNguoiChoi" , ""), pre.getInt("So cau hien tai" , 0) , pre.getInt("Ruby" , 0));

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(resultCode == 100){
            if(count >= listGameInfo.size() - 1 ){
                count--;
                Toast.makeText(GameActivity.this , "Chúc mừng bạn đã chơi hết game :)) " , Toast.LENGTH_LONG).show();
                GameActivity.this.onResume();
            }
            else{
                count++;
                ruby += 4;
                GameActivity.this.onPause();
                GameActivity.this.onResume();
            }
        }
    }

    public void playSoungBravo()  {
        if(loaded)  {
            float leftVolumn = volume;
            float rightVolumn = volume;

            int streamId = this.soundPool.play(this.soundBravo,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }
    public void playSoundButton()  {
        if(loaded)  {
            float leftVolumn = volume;
            float rightVolumn = volume;

            int streamId = this.soundPool.play(this.soundButton,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }
    public void playSoundLaugh()  {
        if(loaded)  {
            float leftVolumn = volume;
            float rightVolumn = volume;

            int streamId = this.soundPool.play(this.soundGround,leftVolumn, rightVolumn, 1, 0, 1f);
        }
    }
    public void chayAmThanh(){
        // Đối tượng AudioManager sử dụng để điều chỉnh âm lượng.
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Chỉ số âm lượng hiện tại của loại luồng nhạc cụ thể (streamType).
        float currentVolumeIndex = (float) audioManager.getStreamVolume(streamType);


        // Chỉ số âm lượng tối đa của loại luồng nhạc cụ thể (streamType).
        float maxVolumeIndex  = (float) audioManager.getStreamMaxVolume(streamType);

        // Âm lượng  (0 --> 1)
        this.volume = currentVolumeIndex / maxVolumeIndex;

        // Cho phép thay đổi âm lượng các luồng kiểu 'streamType' bằng các nút
        // điều khiển của phần cứng.
        this.setVolumeControlStream(streamType);



        if (Build.VERSION.SDK_INT >= 21 ) {

            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder= new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            this.soundPool = builder.build();
        }
        // Với phiên bản Android SDK < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }
        // Sự kiện SoundPool đã tải lên bộ nhớ thành công.
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        this.soundBravo = this.soundPool.load(this, R.raw.bravo, 1);
        this.soundButton = this.soundPool.load(this, R.raw.button11 ,1);
        this.soundGround = this.soundPool.load(this, R.raw.type_wrong ,1);


    }

    public void chupHinh(){
        Bitmap bm = screenShot(findViewById(R.id.activity_game));
        File file = saveBitmap(bm, "screenshot_game.jpg");
        Log.i("chase", "filepath: " + file.getAbsolutePath());
        Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 50: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        try {
                           chupHinh();
                        } catch (Exception e) {
                            Toast.makeText(GameActivity.this, "Lỗi khi tải hình, thử lại !!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(GameActivity.this , "Bạn cần cấp quyền ứng dụng trước !!" , Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    class UpdatePlayer extends AsyncTask {

        String id;
        String ten;
        int socau;
        int soruby;

        public UpdatePlayer(String id , String ten, int socau , int soruby){
            this.socau = socau;
            this.ten = ten;
            this.soruby = soruby;
            this.id = id;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            MyService sv = new MyService();

            // Tạo danh sách tham số gửi đến máy chủ
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("id", id));
            args.add(new BasicNameValuePair("tennguoidung", ten));
            args.add(new BasicNameValuePair("socaudaqua", socau + ""));
            args.add(new BasicNameValuePair("sorubyhienco", soruby + ""));

            // Lấy đối tượng JSON
            String json = sv.makeService("http://thinhtdt.esy.es/update.php", MyService.POST, args);
            return null;
        }
    }
    public void updatePlayer(String id ,String ten, int cau , int rb){
        new UpdatePlayer(id ,ten, cau , rb).execute();
    }

    class InsertPlayer extends AsyncTask {

        PlayerInfo p;

        public InsertPlayer(PlayerInfo p){
            this.p = p;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }

        @Override
        protected Object doInBackground(Object[] params) {

            MyService jsonParse = new MyService();

            // Tạo danh sách tham số gửi đến máy chủ
            List<NameValuePair> args = new ArrayList<NameValuePair>();
            args.add(new BasicNameValuePair("id", p.getId()));
            args.add(new BasicNameValuePair("tennguoidung", p.getTenNguoiDung()));
            args.add(new BasicNameValuePair("socaudaqua", p.getSoCauDaVuotQua() + ""));
            args.add(new BasicNameValuePair("sorubyhienco", p.getSoRubyHienTai() + ""));

            // Lấy đối tượng JSON
            String json = jsonParse.makeService("http://thinhtdt.esy.es/create.php", MyService.POST, args);
            return null;
        }
    }
    public void insertPlayer(PlayerInfo p){
        new InsertPlayer(p).execute();
    }


}

