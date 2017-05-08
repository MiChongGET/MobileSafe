package cn.buildworld.com.mobilesafe.activity.appmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;

public class EnterPwActivity extends AppCompatActivity {

    private EditText login_app_pw;
    private String packname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pw);

        Intent intent = getIntent();
        packname = intent.getStringExtra("packname");
        L.i("取消保护的包名；"+packname);
        login_app_pw = (EditText) findViewById(R.id.login_app_pw);

    }


    @Override
    public void onBackPressed() {
        //回桌面。
//        <action android:name="android.intent.action.MAIN" />
//        <category android:name="android.intent.category.HOME" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <category android:name="android.intent.category.MONKEY"/>
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        //所有的activity最小化 不会执行ondestory 只执行 onstop方法。

    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    public void LoginApp(View v){

        String passwd = login_app_pw.getText().toString().trim();
        if (TextUtils.isEmpty(passwd)){
            Toast.makeText(this, "输入的密码为空！！！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwd.equals("123")){
            //发送一个自定义的广播，通知临时停止保护
            Intent intent = new Intent();
            intent.setAction("cn.buildworld.com.mobilesafe.tempstop");
            intent.putExtra("packname",packname);
            sendBroadcast(intent);
            finish();
        }

    }
}
