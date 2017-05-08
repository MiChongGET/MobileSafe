 package cn.buildworld.com.mobilesafe.activity.tools;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.Utils.NumAddressQueryUtils;

 public class NumAddressQueryActivity extends AppCompatActivity {

    private EditText ed_phone;
    private TextView result;
     private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num_address_query);

        Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);
        toolbar.setTitle("号码归属地查询");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ed_phone = (EditText) findViewById(R.id.ed_phone);
        result = (TextView) findViewById(R.id.result);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        ed_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * 当输入发生变化的时候
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s!=null && s.length()>3){
                    String number = NumAddressQueryUtils.getNumber(s.toString());
                    result.setText(number);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public void numberAddressQuery(View view){

        String phone = ed_phone.getText().toString().trim();
        L.i("输入的号码为："+phone);
        if (TextUtils.isEmpty(phone)){
            //输入框抖动
            Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
            ed_phone.startAnimation(shake);


            //手机震动
//            //当电话号码为空的时候，就去振动手机提醒用户
////			 vibrator.vibrate(2000);
//            long[] pattern = {200,200,300,300,1000,2000};
//            //-1不重复 0循环振动 1；
//            vibrator.vibrate(pattern, -1);

            Toast.makeText(this, "查询为空！！！", Toast.LENGTH_SHORT).show();


            return;
        }
        else {
            String number = NumAddressQueryUtils.getNumber(phone);

            if (number.equals("空")){
                //输入框抖动
                Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
                ed_phone.startAnimation(shake);
            }
            result.setText(number);
        }

    }
}
