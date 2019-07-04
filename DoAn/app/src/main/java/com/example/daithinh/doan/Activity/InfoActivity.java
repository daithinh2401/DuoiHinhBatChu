package com.example.daithinh.doan.Activity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.IDNA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.daithinh.doan.Player.PlayerInfo;
import com.example.daithinh.doan.Player.PlayerInfoManager;
import com.example.daithinh.doan.R;
import com.example.daithinh.doan.Service.MyService;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dai Thinh on 2/24/2017.
 */

public class InfoActivity extends Activity {



    String tenTemp;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProgressDialog pDialog;

    ArrayList<PlayerInfo> listPlayer;
    PlayerInfoManager manager;

    private ProfilePictureView profilePictureView ;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;

    private TextView textView1 , textView2, textView3 , txtID , txt_soCauVuotQua , txt_RubyHienTai;
    TextView userName;


    public byte[] convertImageToByte(ProfilePictureView pfv){
        ImageView img = ((ImageView)pfv.getChildAt(0));
        Bitmap bitmap  = ((BitmapDrawable)img.getDrawable()).getBitmap();


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG , 100 , stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_info);

        manager = new PlayerInfoManager(getApplicationContext());
        listPlayer = new ArrayList<PlayerInfo>();


        userName = (TextView) findViewById(R.id.pfName);

        profilePictureView = new ProfilePictureView(getApplicationContext());
        profilePictureView = (ProfilePictureView) findViewById(R.id.image_Facebook);


        textView1 = (TextView) findViewById(R.id.textView_1);
        textView2 = (TextView) findViewById(R.id.textView_2);
        textView3 = (TextView) findViewById(R.id.textView_3);
        txtID = (TextView) findViewById(R.id.txt_Id);
        txt_soCauVuotQua = (TextView) findViewById(R.id.txt_Socau);
        txt_RubyHienTai = (TextView) findViewById(R.id.txt_Ruby);


        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                Toast.makeText(InfoActivity.this, "Successful", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(InfoActivity.this, "Login attempt canceled.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(InfoActivity.this, "Login attempt failed.", Toast.LENGTH_LONG).show();
            }
        });

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };


        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        //nếu đăng nhập thì hiển thị Ảnh đại diện và tên người dùng
        if (isLoggedIn()){
            userName.setVisibility(View.VISIBLE);
            profilePictureView.setVisibility(View.VISIBLE);

        }
        //Nếu chưa đăng nhập thì ẩn
        else {

            userName.setText("Chưa đăng nhập");

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        getDanhSach();
        SharedPreferences pre = getSharedPreferences("player_data" ,MODE_PRIVATE);

        if(isLoggedIn()) {
            txtID.setText(pre.getString("ID", ""));
            userName.setText(pre.getString("TenNguoiChoi", ""));
            txt_soCauVuotQua.setText(pre.getInt("So cau hien tai", 0) + "");
            txt_RubyHienTai.setText(pre.getInt("Ruby", 0) + "");

            userName.setVisibility(View.VISIBLE);
            txtID.setVisibility(View.VISIBLE);
            userName.setVisibility(View.VISIBLE);
            txt_soCauVuotQua.setVisibility(View.VISIBLE);
            txt_RubyHienTai.setVisibility(View.VISIBLE);
            profilePictureView.setProfileId(pre.getString("ID", ""));
        }
        else {
            userName.setText("Chưa đăng nhập");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //Hàm kiểm tra trạng thái đăng nhập
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    //Hàm hiển thị Ảnh đại diện và tên người dùng
    private void displayMessage(Profile profile) {
        PlayerInfo pi = new PlayerInfo();

        if (profile != null) {

            userName.setText(profile.getName());
            profilePictureView.setProfileId(profile.getId());
            tenTemp = profile.getName();


            SharedPreferences pre = getSharedPreferences("player_data", MODE_PRIVATE);
            SharedPreferences.Editor editor = pre.edit();

                try {
                    //Thêm vào 1 player , nếu ko thêm được ---> đã có trong csdl ----> nhảy xuống catch
                    manager.Insert(new PlayerInfo(profile.getId(), profile.getName(), convertImageToByte(profilePictureView),  0, 0));

                    editor.putString("ID", profile.getId().toString());
                    editor.putString("TenNguoiChoi", profile.getName());
                    editor.putBoolean("Dang nhap", true);
                    editor.putInt("So cau hien tai", 0);
                    editor.putInt("Ruby", 0);
                    editor.commit();

                }catch (Exception e){

                    pi = manager.Select(profile.getId());

                    editor.putString("ID", pi.getId());
                    editor.putString("TenNguoiChoi", pi.getTenNguoiDung());
                    editor.putBoolean("Dang nhap", true);
                    editor.putInt("So cau hien tai", pi.getSoCauDaVuotQua());
                    editor.putInt("Ruby", pi.getSoRubyHienTai());
                    editor.commit();

                }

            txtID.setText(pre.getString("ID", ""));
            userName.setText(pre.getString("TenNguoiChoi" , ""));
            txt_soCauVuotQua.setText(pre.getInt("So cau hien tai", 0) + "");
            txt_RubyHienTai.setText(pre.getInt("Ruby", 0) + "");

            txtID.setVisibility(View.VISIBLE);
            userName.setVisibility(View.VISIBLE);
            txt_soCauVuotQua.setVisibility(View.VISIBLE);
            txt_RubyHienTai.setVisibility(View.VISIBLE);


            }
        }



    public void getDanhSach(){
        new GetPlayer().execute();
    }

    class GetPlayer extends AsyncTask {


    @Override
        protected Object doInBackground(Object[] params) {
            MyService jsonParser = new MyService();
            String json = jsonParser.callService("http://thinhtdt.esy.es/display.php", MyService.GET);

            if (json != null) {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    if (jsonObj != null) {
                        JSONArray jsonArray = jsonObj.getJSONArray("player_info");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = (JSONObject) jsonArray.get(i);
                            try {
                                manager.Insert(new PlayerInfo(obj.getString("id"), obj.getString("tennguoidung"), convertImageToByte(setPicture(obj.getString("id"))) , obj.getInt("socaudaqua"), obj.getInt("sorubyhienco")));
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
                pDialog = new ProgressDialog(InfoActivity.this);
                pDialog.setMessage("Loading..");
                pDialog.setCancelable(false);
                pDialog.show();

            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (pDialog.isShowing())
                    pDialog.dismiss();
//
                listPlayer = manager.getAllPlayerInfo();

            }
    }

    public ProfilePictureView setPicture(String id){
        profilePictureView.setProfileId(id);
        return profilePictureView;
    }

}

