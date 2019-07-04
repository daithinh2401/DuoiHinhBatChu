package com.example.daithinh.doan.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.example.daithinh.doan.R;

import java.util.ArrayList;

/**
 * Created by Dai Thinh on 2/24/2017.
 */

public class CongratulationActivity extends Activity {
    String dapancodau;
    Button btnTiepTuc;
    private TextView txt_1, txt_2, txt_3, txt_4, txt_5, txt_6, txt_7, txt_8,
            txt_9, txt_10, txt_11, txt_12, txt_13, txt_14, txt_15, txt_16;


    ArrayList<TextView> listText;
    ArrayList<Character> dapan;

    public void taoMangDapAn(int so){
        char[] ch = new char[dapancodau.length()];
        dapancodau.getChars(0, dapancodau.length() , ch , 0);
        for(int i = 0 ; i < ch.length; i ++ ){
            dapan.add(ch[i]);
        }

    }
    public void layIdTextView(){
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
    }
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

    }
    public void hienDapAn(){
        for ( int i = 0 ; i < dapan.size() ; i ++){
            listText.get(i).setText(dapan.get(i).toString());
        }
        for(int i = 0 ; i < dapancodau.length() ; i ++){
            if(listText.get(i).getText().toString().equals(" ")){
                listText.get(i).setVisibility(View.INVISIBLE);
            }
        }
    }
    public void taoOChu(int so){
        for(int i = 0 ; i < so ; i++){
            listText.get(i).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_congrat);


        listText = new ArrayList<TextView>();
        dapan = new ArrayList<Character>();
        dapancodau = getIntent().getStringExtra("dapAnCoDau");
        layIdTextView();
        themTextViewVaoList();
        taoOChu(dapancodau.length());
        taoMangDapAn(dapancodau.length());
        hienDapAn();


        btnTiepTuc = (Button) findViewById(R.id.btn_TiepTuc) ;
        btnTiepTuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = 4;
                Intent data = new Intent();
                data.putExtra("Ruby" , value);

                setResult(100 , data);
                finish();
            }
        });

    }

}
