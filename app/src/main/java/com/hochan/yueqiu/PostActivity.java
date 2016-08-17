package com.hochan.yueqiu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    public final static String LATLONPOINT = "lat_lon_point";

    private LatLonPoint mFieldGeo;
    private String mName;
    private String mFieldID;

    private Button btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_backarrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFieldGeo = getIntent().getExtras().getParcelable(MyApplication.FIELD_GEO);
        mName = getIntent().getExtras().getString(MyApplication.FIELD_NAME);
        mFieldID = getIntent().getExtras().getString(MyApplication.FIELD_ID);

        btnPost = (Button) findViewById(R.id.btn_post);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AVUser avUser = AVUser.getCurrentUser();
                if(avUser == null){
                    System.out.println("未登录！");
                    AVUser.logInInBackground("chenzhd", "1234567", new LogInCallback<AVUser>() {
                        @Override
                        public void done(AVUser avUser, AVException e) {
                            System.out.println("登陆成功："+avUser.getEmail());

                        }
                    });
                }else{
                    System.out.println(avUser.getUsername());
                }

                Map<String, Object> data = new HashMap<String, Object>();
                data.put(MyApplication.STATUS_FIELD_NAME, mName);
                Date startDate = getDateWithDateString("2015-11-11 07:10:00");
                Date endtDate = getDateWithDateString("2015-11-11 07:10:00");
                data.put(MyApplication.STATUS_START_TIME, startDate);
                data.put(MyApplication.STATUS_END_TIME, endtDate);
                data.put(MyApplication.STATUS_PARTICIPANTS_COUNT, 1);
                data.put(MyApplication.STATUS_PARTICIPANTS, Arrays.asList(avUser));
                final AVGeoPoint avGeoPoint = new AVGeoPoint();
                avGeoPoint.setLatitude(mFieldGeo.getLatitude());
                avGeoPoint.setLongitude(mFieldGeo.getLongitude());

                final AVStatus avStatus = AVStatus.createStatusWithData(data);
                avStatus.put(MyApplication.STATUS_TARGET_FIELD,
                        AVObject.createWithoutData(MyApplication.OBJECT_FIELD, mFieldID));
                avStatus.setInboxType(mFieldID);
                avStatus.sendInBackgroundWithBlock(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e != null){
                            e.printStackTrace();
                            return;
                        }
                        System.out.println("发布约球消息成功！");
                         final AVObject avObject = AVObject.createWithoutData(MyApplication.OBJECT_FIELD,
                                mFieldID);
                        AVRelation<AVObject> relation = avObject.getRelation(MyApplication.FIELD_STATUS);
                        relation.add(avStatus);
                        avObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if(e != null){
                                    e.printStackTrace();
                                    return;
                                }
                                System.out.println("-------------------");
                                avObject.increment(MyApplication.FIELD_STATUS_COUNT);
                                avObject.setFetchWhenSave(true);
                                avObject.saveInBackground();
                            }
                        });

                        AVRelation<AVObject> userRelation = avUser.getRelation(MyApplication.USER_ORIGIN_STATUS);
                        userRelation.add(avStatus);
                        avUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if(e != null){
                                    e.printStackTrace();
                                    return;
                                }
                                avUser.increment(MyApplication.USER_ORIGIN_STATUS_COUNT);
                                avUser.setFetchWhenSave(true);
                                avUser.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        Toast.makeText(PostActivity.this, "已发布！", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(PostActivity.this, MainActivity.class));
                                    }
                                });

                            }
                        });
                    }
                });
            }
        });
    }

    private Date getDateWithDateString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
