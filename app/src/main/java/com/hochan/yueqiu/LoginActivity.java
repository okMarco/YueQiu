package com.hochan.yueqiu;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnRegister, btnLogin;
    private ViewPager mViewPager;
    private LinearLayout llSlogan;
    private View vLogin, vRegister;
    private EditText edLoginName, edLoginPassword, edRegisterName,
            edRegisterEmail, edRegisterPassword;

    private final static int FINISH = 0;

    private int[] mWallPics = new int[]{R.drawable.wall_picture_0, R.drawable.wall_picture_1,
        R.drawable.wall_picture_2, R.drawable.wall_picture_3};
    //private int[] mAvatar = new int[]{R.drawable.avatar_0, R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3};

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1){
                case FINISH:
                    loginFinish();
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorWallPicBackgroundDark));

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        llSlogan = (LinearLayout) findViewById(R.id.ll_slogan);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        initShowingPages();

        vLogin = LayoutInflater.from(this).inflate(R.layout.layout_login, null, false);
        edLoginName = (EditText) vLogin.findViewById(R.id.ed_login_name);
        edLoginPassword = (EditText) vLogin.findViewById(R.id.ed_login_password);

        vRegister = LayoutInflater.from(this).inflate(R.layout.layout_register, null, false);
        edRegisterName = (EditText) vRegister.findViewById(R.id.ed_register_name);
        edRegisterEmail = (EditText) vRegister.findViewById(R.id.ed_register_email);
        edRegisterPassword = (EditText) vRegister.findViewById(R.id.ed_register_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                if(vLogin.getParent() != null)
                    ((ViewGroup)vLogin.getParent()).removeView(vLogin);
                new AlertDialog.Builder(this)
                        .setTitle("登录")
                        .setView(vLogin)
                        .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                login();
                            }
                        })
                        .show();
                break;
            case R.id.btn_register:
                if(vRegister.getParent() != null)
                    ((ViewGroup)vRegister.getParent()).removeView(vRegister);
                new AlertDialog.Builder(this)
                        .setTitle("注册")
                        .setView(vRegister)
                        .setPositiveButton("注册", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            register();
                            }
                        })
                        .show();
                break;
        }
    }

    private void login(){
        AVUser.logInInBackground(edLoginName.getText().toString(),
                edLoginPassword.getText().toString(), new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        if(e == null){
                            loginFinish();
                        }else{
                            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void register(){
        final AVUser avUser = new AVUser();
        avUser.setUsername(edRegisterName.getText().toString());
        avUser.setPassword(edRegisterPassword.getText().toString());
        avUser.setEmail(edRegisterEmail.getText().toString());
        avUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    System.out.println("注册成功");
                    saveAvatar();
                }else{
                    System.out.println("注册失败");
                    switch (e.getCode()){
                        case 202:
                            Toast.makeText(LoginActivity.this, "该用户名已被使用", Toast.LENGTH_LONG).show();
                            break;
                        case 203:
                        case 214:
                            Toast.makeText(LoginActivity.this, "该邮箱已被注册", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
    }

    private void saveAvatar(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AVFile avFile = AVFile.parseFileWithObjectId("5742bf0d5bbb500048681428");
                    AVUser avUser = AVUser.getCurrentUser();
                    avUser.put("avatar", avFile);
                    avUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            Message message = mHandler.obtainMessage();
                            message.arg1 = FINISH;
                            mHandler.sendMessage(message);
                        }
                    });
                } catch (AVException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void loginFinish(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Intent intent = new Intent(LoginActivity.this, SoccerFieldActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }else{
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private void initShowingPages() {

        final ArrayList<ImageView> imageViews = new ArrayList<>();
        for(int i = 0; i < 4; i++){
            ImageView imageView = new ImageView(LoginActivity.this);
            ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
            layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewPager.LayoutParams.MATCH_PARENT;
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(mWallPics[i]);
            imageViews.add(imageView);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 4;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(imageViews.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {

                container.addView(imageViews.get(position));
                return imageViews.get(position);
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //llSlogan.scrollTo(positionOffsetPixels*3, 0);
                //llSlogan.setAlpha(255 - (int) (positionOffset*255));
                imageViews.get(position).setImageAlpha(255 - (int) (positionOffset*255));
                if((position+1) < 4)
                    imageViews.get(position+1).setImageAlpha((int) (positionOffset*255));
                if((position-1) >= 0){
                    imageViews.get(position-1).setImageAlpha((int) (positionOffset*255));
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
