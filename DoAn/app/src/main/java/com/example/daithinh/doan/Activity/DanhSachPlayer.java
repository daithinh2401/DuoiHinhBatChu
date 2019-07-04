package com.example.daithinh.doan.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.daithinh.doan.Player.ListAdapter;
import com.example.daithinh.doan.Player.PlayerInfo;
import com.example.daithinh.doan.Player.PlayerInfoManager;
import com.example.daithinh.doan.R;
import com.example.daithinh.doan.Service.MyService;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Dai Thinh on 2/28/2017.
 */

public class DanhSachPlayer extends Activity {

    ListView lv ;
    ArrayList<PlayerInfo> listPlayer;
    PlayerInfoManager manager;
    ProfilePictureView profilePictureView;
    ProgressDialog pDialog;


    // URL dùng để truy xuất bảng PlayerInfo
    private String url = "http://thinhtdt.esy.es/display.php";


    public byte[] convertImageToByte(ProfilePictureView pfv){
        ImageView img = ((ImageView)pfv.getChildAt(0));
        Bitmap bitmap  = ((BitmapDrawable)img.getDrawable()).getBitmap();


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    public ProfilePictureView getProfilePictureView(String id){
        profilePictureView = new ProfilePictureView(getApplicationContext());
        profilePictureView.setProfileId(id);
        return profilePictureView;
    }


    public void getDS(){
        new GetPlayerInfo().execute();
    }

    class GetPlayerInfo extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            MyService jsonParser = new MyService();
            String json = jsonParser.callService(url, MyService.GET);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray jsonArray = jsonObj.getJSONArray("player_info");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = (JSONObject) jsonArray.get(i);
                            try {
                                manager.Insert(new PlayerInfo(obj.getString("id"), obj.getString("tennguoidung"), convertImageToByte(getProfilePictureView(obj.getString("id"))), obj.getInt("socaudaqua"), obj.getInt("sorubyhienco")));
                            }catch (Exception e){
                                manager.UpdateTuServer(obj.getString("tennguoidung"), obj.getInt("socaudaqua"), obj.getInt("sorubyhienco"),obj.getString("id"));

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DanhSachPlayer.this);
            pDialog.setMessage("Loading..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (pDialog.isShowing())
                pDialog.dismiss();
            listPlayer = manager.getAllPlayerInfo();
            DanhSachPlayer.this.onPause();
            DanhSachPlayer.this.onResume();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_highscore);


        lv = (ListView) findViewById(R.id.listView);
        listPlayer = new ArrayList<PlayerInfo>();

        manager = new PlayerInfoManager(getApplicationContext());
        listPlayer = manager.getAllPlayerInfo();

        getDS();



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder b = new AlertDialog.Builder(DanhSachPlayer.this);

                b.setTitle("Đi tới Facebook ?");
                b.setPositiveButton("Có", new DialogInterface. OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse("http://www.facebook.com/" + listPlayer.get(position).getId()));
                        startActivity(intent);
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




    }

    @Override
    protected void onResume() {
        super.onResume();

        Collections.sort(listPlayer, new SoSanh());
        ListAdapter adapter = new ListAdapter(getApplicationContext(), R.layout.list_item , listPlayer);
        lv.setAdapter(adapter);
    }

    public class SoSanh implements Comparator<PlayerInfo>{

        @Override
        public int compare(PlayerInfo p1, PlayerInfo p2) {
            int socau_1 = p1.getSoCauDaVuotQua();
            int socau_2 = p2.getSoCauDaVuotQua();
            if(socau_1 < socau_2){
                return 1;
            }
            else if(socau_1 == socau_2){
                return 0;
            }
            else {
                return -1;
            }
        }
    }


}
