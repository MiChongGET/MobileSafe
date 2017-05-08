package cn.buildworld.com.mobilesafe.activity.fangdao;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.receiver.AdminReceiver;

public class LostFindActivity extends AppCompatActivity{

    private SharedPreferences sp ;
    private TextView phone_num;
    private ImageView lock;
    private DevicePolicyManager dpm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //判断呢是否要设置向导
        sp = getSharedPreferences("config",MODE_PRIVATE);

        boolean configed = sp.getBoolean("configed",false);

        if (configed){
            //手机防盗页面
            setContentView(R.layout.activity_lost_find);

            Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);
            toolbar.setTitle("手机防盗");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            //设置显示绑定的手机号码
            phone_num = (TextView) findViewById(R.id.phone_num);
            String phoneNum = sp.getString("phoneNum", null);
            phone_num.setText(phoneNum);
            
            //设置显示是否上锁标志
            lock = (ImageView) findViewById(R.id.is_lock);
           SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            boolean is_fangdao = sp.getBoolean("is_fangdao", false);
            if (is_fangdao){
                lock.setImageResource(R.drawable.lock);
            }


            dpm  = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        }else {
            //跳转到设置界面
            startActivity(new Intent(LostFindActivity.this,Setup1Activity.class));
            finish();

        }

    }

    public void reEnterSetup(View view){
        startActivity(new Intent(LostFindActivity.this,Setup1Activity.class));
        finish();
    }


    //防盗功能实现

    public void GPS(View view){

        startActivity(new Intent(LostFindActivity.this,GpsActivity.class));
    }

    public void Alarm(View view){
        MediaPlayer player = MediaPlayer.create(this,R.raw.alarm);
        player.setLooping(false);
        player.setVolume(1.0f,1.0f);
        player.start();
        L.i("报警！！！");
    }

    public void WipeData(View view){

    }


    //锁屏实现
    public void LockScreen(View view){
        ComponentName   who = new ComponentName(this,AdminReceiver.class);
        if (dpm.isAdminActive(who)){
            lockscreen();
        }else openAdmin();


    }

    /**
     * 用代码去开启管理员
     * @param
     */
    public void openAdmin(){
        //创建一个Intent
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //我要激活谁
        ComponentName mDeviceAdminSample = new ComponentName(this,AdminReceiver.class);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        //劝说用户开启管理员权限
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "哥们开启我可以一键锁屏，你的按钮就不会经常失灵");
        startActivity(intent);
    }


    /**
     * 一键锁屏
     */

    public void lockscreen(){
        ComponentName   who = new ComponentName(this,AdminReceiver.class);
        if(dpm.isAdminActive(who)){
            dpm.lockNow();//锁屏
            dpm.resetPassword("", 0);//设置屏蔽密码

            //清除Sdcard上的数据
//			dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
            //恢复出厂设置
//			dpm.wipeData(0);
        }else{
            Toast.makeText(this, "还没有打开管理员权限", 1).show();
            return ;
        }


    }

}
