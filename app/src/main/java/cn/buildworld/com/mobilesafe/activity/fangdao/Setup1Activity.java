package cn.buildworld.com.mobilesafe.activity.fangdao;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.View;

import cn.buildworld.com.mobilesafe.R;

public class Setup1Activity extends BaseSetupActivity {

    //1、定义一个手势识别器
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);
        toolbar.setTitle("1.欢迎使用手机防盗");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void showNext() {
        Intent intent = new Intent(this,Setup2Activity.class);
        startActivity(intent);
        finish();
        //要求在finish()或者startActivity(intent);后面执行；
        overridePendingTransition(R.anim.tra_in, R.anim.tra_out);
    }

    public void next(View view){

        showNext();
    }

    @Override
    public void showPre() {

    }

}
