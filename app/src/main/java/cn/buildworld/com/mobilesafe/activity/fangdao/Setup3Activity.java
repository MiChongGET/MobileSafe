package cn.buildworld.com.mobilesafe.activity.fangdao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cn.buildworld.com.mobilesafe.R;

public class Setup3Activity extends BaseSetupActivity {

    private EditText setup_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);
        toolbar.setTitle("3.设置安全号码");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //设置获取的手机号
        setup_phone = (EditText) findViewById(R.id.setup_phone);

        String phoneNum = sp.getString("phoneNum", null);
        setup_phone.setText(phoneNum);
    }

    @Override
    public void showNext() {

        String phone = setup_phone.getText().toString().trim();

        if (TextUtils.isEmpty(phone)){

            Toast.makeText(this, "号码为空！！！", Toast.LENGTH_SHORT).show();
            return;
        }

        //保存设置的号码
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("phoneNum",phone);
        editor.commit();


        startActivity(new Intent(Setup3Activity.this,Setup4Activity.class));
        overridePendingTransition(R.anim.tra_in,R.anim.tra_out);
        finish();
    }

    @Override
    public void showPre() {
        startActivity(new Intent(Setup3Activity.this,Setup2Activity.class));
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

    public void selectContact(View veiw){

        Intent intent = new Intent(this,SelectContactActivity.class);
        startActivityForResult(intent,0);
    }


    //获取手机联系人传过来的值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null)
            return;

        String phone = data.getStringExtra("phone").replace("-", "");

        setup_phone.setText(phone);

    }
}
