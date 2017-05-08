package cn.buildworld.com.mobilesafe.activity.fangdao;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;

public class Setup2Activity extends BaseSetupActivity implements SimPreference.CallBackValue {

    private TelephonyManager tp ;
    private Fragment fm;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);
        toolbar.setTitle("2.手机卡绑定");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tp = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        sp = getSharedPreferences("config",MODE_PRIVATE);

        SimPreference sim = new SimPreference();
        getFragmentManager().beginTransaction().add(R.id.sim,sim).commit();


    }

    @Override
    public void showNext() {

        String sim = sp.getString("sim",null);
        if (TextUtils.isEmpty(sim)){
            Toast.makeText(this, "手机卡没有绑定，请绑定", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(Setup2Activity.this,Setup3Activity.class));
        overridePendingTransition(R.anim.tra_in,R.anim.tra_out);
        finish();
    }

    @Override
    public void showPre() {
        startActivity(new Intent(Setup2Activity.this,Setup1Activity.class));
        overridePendingTransition(R.anim.tra_pre_out,R.anim.tra_pre_in);
        finish();
    }


    //跳转到上一页
    public void pre(View view){
        showPre();
    }


    //跳转到下一页
    public void next(View view){
        showNext();
    }


    //主要是实时获取SIM卡绑定的状态
    @Override
    public void SentMesValue(boolean value) {
        SharedPreferences.Editor editor = sp.edit();

        if (value) {
            L.i("已完成绑定");

            String sim = tp.getSimSerialNumber();//SIM卡的序列号
            String deviceId = tp.getDeviceId();//设备id
            String num = tp.getLine1Number();
            L.i("手机卡号："+sim);
            L.i("手机ID:"+deviceId);
            L.i("手机号："+num);


            editor.putString("sim",sim+"1");
            editor.commit();

        }else {
            L.i("未完成绑定");

            editor.putString("sim",null);
            editor.commit();
        }
    }
}



class SimPreference extends PreferenceFragment{

    CallBackValue callBackValue ;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callBackValue = (CallBackValue) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sim);


        PreferenceManager manager = getPreferenceManager();
        SharedPreferences preferences = manager.getDefaultSharedPreferences(getActivity());

        boolean is_sim = preferences.getBoolean("is_sim",false);

    }

    //此处主要是处理preference状态的变化
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        SharedPreferences sharedPreferences = preference.getSharedPreferences();
        boolean is_sim = sharedPreferences.getBoolean("is_sim",false);

        callBackValue.SentMesValue(is_sim);

        L.i(is_sim+"");

        return true;
    }


    //使用回调使得状态可以及时地传递过去
    public interface CallBackValue{
        public void SentMesValue(boolean value);
    }
}
