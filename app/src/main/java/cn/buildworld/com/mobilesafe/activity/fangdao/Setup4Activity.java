package cn.buildworld.com.mobilesafe.activity.fangdao;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.renderscript.Short4;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;

public class Setup4Activity extends BaseSetupActivity implements FangdaoPreference.CallBackValue {

    private SharedPreferences sp;
    private boolean is_fangdao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);


        Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);
        toolbar.setTitle("4.设置完成");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sp  = getSharedPreferences("config",MODE_PRIVATE);


        //设置保存手机防盗状态

        FangdaoPreference fp = new FangdaoPreference();
        getFragmentManager().beginTransaction().add(R.id.fangdao,fp).commit();


    }

    @Override
    public void showNext() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean fangdao = preferences.getBoolean("is_fangdao",false);
        if (is_fangdao){
            startActivity(new Intent(Setup4Activity.this,LostFindActivity.class));
            finish();
            return;
        }
        if (fangdao){
            startActivity(new Intent(Setup4Activity.this,LostFindActivity.class));
            finish();
            return;
        }
        if (!fangdao){
            Toast.makeText(this, "手机防盗未开启！！！", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void showPre() {
        startActivity(new Intent(Setup4Activity.this,Setup3Activity.class));
        overridePendingTransition(R.anim.tra_pre_out,R.anim.tra_pre_in);
        finish();
    }

    //跳转到上一页
    public void pre(View view){
        showPre();
    }


    public void next(View view){
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("configed",true);
        editor.commit();

        showNext();
    }

    @Override
    public void SentMesValue(boolean value) {
        is_fangdao = value;

        L.i("手机防盗是否开启"+value);
    }
}

class FangdaoPreference extends PreferenceFragment {

    CallBackValue callBackValue ;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callBackValue = (CallBackValue) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fangdao);


        PreferenceManager manager = getPreferenceManager();
        SharedPreferences preferences = manager.getDefaultSharedPreferences(getActivity());

        boolean is_fangdao = preferences.getBoolean("is_fangdao",false);

    }

    //此处主要是处理preference状态的变化
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        SharedPreferences sharedPreferences = preference.getSharedPreferences();
        boolean is_fangdao = sharedPreferences.getBoolean("is_fangdao",false);

        callBackValue.SentMesValue(is_fangdao);

        L.i(is_fangdao+"");

        return true;
    }


    //使用回调使得状态可以及时地传递过去
    public interface CallBackValue{
        public void SentMesValue(boolean value);
    }
}

