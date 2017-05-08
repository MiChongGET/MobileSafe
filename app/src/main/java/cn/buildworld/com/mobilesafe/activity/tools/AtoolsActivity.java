package cn.buildworld.com.mobilesafe.activity.tools;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.SmsUtils;

public class AtoolsActivity extends AppCompatActivity {
    private ProgressDialog pd ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);

        Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);
        toolbar.setTitle("高级工具");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
    public void numberQuery(View view){
        startActivity(new Intent(AtoolsActivity.this,NumAddressQueryActivity.class));
    }

    public void smsBackup(View view){
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在备份");
        pd.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    SmsUtils.backupSms(getApplicationContext(), new SmsUtils.BackUpCallBack() {

                        //接口回调
                        @Override
                        public void beforeBackup(int max) {
                            pd.setMax(max);
                        }

                        @Override
                        public void onSmsBackup(int progress) {
                            pd.setProgress(progress);
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    pd.dismiss();
                }
            }
        }.start();
    }

    /**
     * 短信
     * @param view
     */
    public void smsRestore(View view){

        SmsUtils.restoreSms(this,true);
        Toast.makeText(this, "短信恢复成功！！！", Toast.LENGTH_SHORT).show();

    }
}
