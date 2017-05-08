package cn.buildworld.com.mobilesafe.activity.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.Utils.ServiceUtils;
import cn.buildworld.com.mobilesafe.fragment.SettingsFragment;
import cn.buildworld.com.mobilesafe.service.AddressService;
import cn.buildworld.com.mobilesafe.service.CallSmsSafeService;
import cn.buildworld.com.mobilesafe.service.WatchDogService;


/**
 * 作者：MiChong on 2017/4/6 0006 10:50
 * 邮箱：1564666023@qq.com
 */
public class SettingsActivity extends AppCompatActivity
        implements SettingsFragment.CallBackValue{

    private Intent showAddress;
    private Button addresBackgroud;
    private Intent callSmsSafeIntent;
    private Intent watchdogIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar3);

        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addresBackgroud = (Button) findViewById(R.id.addres_backgroud);

        //此时主要是设置preference的fragment文件

        SettingsFragment sf = new SettingsFragment();
        getFragmentManager().beginTransaction().add(R.id.preferences,sf).commit();
        showAddress = new Intent(this, AddressService.class);

        String myPackageName = getPackageName();
        callSmsSafeIntent = new Intent(this, CallSmsSafeService.class);

        boolean isRunning = ServiceUtils.isServiceRunning(this, "cn.buildworld.com.mobilesafe.service.AddressService");

        if (isRunning){
            addresBackgroud.setVisibility(View.VISIBLE);
        }else addresBackgroud.setVisibility(View.INVISIBLE);


    }

    public void setSpeed(View view){
        startActivity(new Intent(SettingsActivity.this,SetSpeed.class));
    }

    @Override
    public void SentValue(boolean value) {
        if (value){
            startService(showAddress);
            addresBackgroud.setVisibility(View.VISIBLE);
        }else {
            stopService(showAddress);
            addresBackgroud.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 黑名单状态监听
     * @param v
     */
    @Override
    public void isOpenBlackNum(boolean v) {

        L.i("黑名单是否开启："+v);
        if (v){
            startService(callSmsSafeIntent);
        }else{
            stopService(callSmsSafeIntent);
        }


    }


    /**
     * 设置密码锁服务是否开启
     * @param value
     */
    @Override
    public void WatchDog(boolean value) {
        L.i("密码锁服务是否开启："+value);
        watchdogIntent = new Intent(this, WatchDogService.class);
        if (value){
            startService(watchdogIntent);
        }else {
            stopService(watchdogIntent);
        }

    }

    public void AddresBackgroud(View view){
        String[] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("归属地提示框风格");
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("which",which);
                editor.commit();

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("cancel",null);
        builder.show();
    }
}
