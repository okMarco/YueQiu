package com.hochan.yueqiu;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVStatus;
import com.avos.avoscloud.AVStatusQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.StatusListCallback;
import com.avos.avoscloud.search.AVSearchQuery;
import com.hochan.adapter.StatusAdapter;
import com.hochan.fragment.RouteFragment;
import com.hochan.fragment.StatusFragment;
import com.hochan.yueqiu.R;

import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@SuppressWarnings("deprecation")
public class SoccerFieldActivity extends AppCompatActivity implements View.OnClickListener{

    public final static String NAME = "name";
    public final static String LOCATION= "location";
    public final static String STARTPOINT= "startPoint";
    public final static String ENDPOINT= "endPoint";
    public SwipeRefreshLayout mRefreshLayout;
    //private TextView tvName;
    private TextView tvLocation;
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private String mName;
    private String mFieldID;
    private ImageButton ivbtnDrive;
    private ImageButton ivbtnBus;
    private ImageButton ivbtnWalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soccer_field);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mName = getIntent().getExtras().getString(NAME);
        toolbar.setTitle(mName);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_backarrow));

        setSupportActionBar(toolbar);

        ivbtnDrive = (ImageButton) findViewById(R.id.ibtn_drive);
        ivbtnBus = (ImageButton) findViewById(R.id.ibtn_bus);
        ivbtnWalk = (ImageButton) findViewById(R.id.ibtn_walk);
        ivbtnDrive.setOnClickListener(this);
        ivbtnBus.setOnClickListener(this);
        ivbtnWalk.setOnClickListener(this);

        mStartPoint = getIntent().getExtras().getParcelable(STARTPOINT);
        mEndPoint = getIntent().getExtras().getParcelable(ENDPOINT);
        mFieldID = getIntent().getExtras().getString(MyApplication.FIELD_ID);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvLocation.setText(getIntent().getExtras().getString(LOCATION));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.rl_statusFragment,
                StatusFragment.newInstance(StatusFragment.SOCCERFIELD, mFieldID)).commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_drive:
                RouteFragment.newInstance(mStartPoint, mEndPoint, RouteFragment.ROUTE_TYPE_DRIVE).show(getSupportFragmentManager(), "route");
                break;
            case R.id.ibtn_bus:
                RouteFragment.newInstance(mStartPoint, mEndPoint, RouteFragment.ROUTE_TYPE_BUS).show(getSupportFragmentManager(), "route");
                break;
            case R.id.ibtn_walk:
                RouteFragment.newInstance(mStartPoint, mEndPoint, RouteFragment.ROUTE_TYPE_WALK).show(getSupportFragmentManager(), "route");
                break;
            case R.id.fab:
                if(AVUser.getCurrentUser() == null){
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    break;
                }
                Intent intent = new Intent(this, PostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(MyApplication.FIELD_GEO, mEndPoint);
                bundle.putString(MyApplication.FIELD_NAME, mName);
                bundle.putString(MyApplication.FIELD_ID, mFieldID);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
