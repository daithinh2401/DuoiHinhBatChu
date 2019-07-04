package com.example.daithinh.doan.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.daithinh.doan.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class MainActivity extends Activity {

    private Button btn_Start;
    private Button btn_Exit;
    private Button btn_Info;
    private Button btn_HighScore;
    private ImageView image;
    private Button btn_Tutorial;
    private Button btn_ShareLink;
    ShareDialog shareDialog;
    CallbackManager callbackManager;

    public void taoSharePreferences(){
        SharedPreferences pre = getSharedPreferences("player_data" , MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("ID" , "");
        editor.putString("TenNguoiChoi" , "");
        editor.putInt("So cau hien tai" , 0);
        editor.putInt("Ruby" , 0);
        editor.putBoolean("Dang nhap" , false);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        image = (ImageView)findViewById(R.id.imageView);
        image.setImageResource(R.drawable.logo);


        btn_ShareLink = (Button) findViewById(R.id.btnShareLink);
        btn_ShareLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse("https://drive.google.com/uc?export=download&id=0BwdGCKu4YaNpMExjNXdxMGFiaGs"))
                            .setContentTitle("Game đuổi hình bắt chữ")
                            .setImageUrl(Uri.parse("http://4.bp.blogspot.com/-Jwqva0_a-_U/U-mxzTOgI4I/AAAAAAAAEjg/kVI6-KBDq7Q/s1600/batchu.png"))
                            .setContentDescription("Game đuổi hình bắt chữ")
                            .build();
                    shareDialog.show(content);
                } catch(Exception e) {
                    Log.e("Lỗi" , "Ko thể up");
                }
            }
        });

        btn_Exit = (Button) findViewById(R.id.btnExit);
        btn_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startActivity(startMain);
                finish();
            }
        });

        btn_Tutorial = (Button) findViewById(R.id.btnTutorial);
        btn_Tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TutorialActivity.class);
                startActivity(intent);
            }
        });

        btn_Info = (Button) findViewById(R.id.btnInfo);
        btn_Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(MainActivity.this , InfoActivity.class);
                startActivity(login);
            }
        });


        btn_Start = (Button)findViewById(R.id.btnStart);
        btn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);

            }
        });

        btn_HighScore = (Button) findViewById(R.id.btnHighscore);
        btn_HighScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(MainActivity.this, DanhSachPlayer.class);
                    startActivity(intent);

                }
                catch (Exception e ){
                    Toast.makeText(MainActivity.this, e.toString() , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 10){
            MainActivity.this.onResume();
        }
    }

}
