package cn.buildworld.com.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.Utils.MD5Utils;
import cn.buildworld.com.mobilesafe.activity.Settings.SettingsActivity;
import cn.buildworld.com.mobilesafe.activity.appmanager.AppManagerActivity;
import cn.buildworld.com.mobilesafe.activity.callSmsSafe.CallSmsSafeActivity;
import cn.buildworld.com.mobilesafe.activity.fangdao.LostFindActivity;
import cn.buildworld.com.mobilesafe.service.AddressService;
import cn.buildworld.com.mobilesafe.activity.tools.AtoolsActivity;
import cn.buildworld.com.mobilesafe.service.WatchDogService;

public class HomeActivity extends AppCompatActivity {

    private GridView list_home;
    private static String[] names = {
            "手机防盗",
            "通讯卫士",
            "软件管理",
            "进程管理",
            "流量管理",
            "手机杀毒",
            "缓存清理",
            "高级工具",
            "设置中心"
    };

    private static int pic_id[] = {
            R.drawable.safe,
            R.drawable.callmsgsafe,
            R.drawable.app,
            R.drawable.taskmanager,
            R.drawable.netmanager,
            R.drawable.trojan,
            R.drawable.sysoptimize,
            R.drawable.atools,
            R.drawable.settings

    };
    private GridviewAdapter adapter = new GridviewAdapter();
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        list_home = (GridView) findViewById(R.id.list_home);
        list_home.setAdapter(adapter);

        //获取密码
        sp = getSharedPreferences("config",MODE_PRIVATE);


        list_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 7://进入高级工具
                        startActivity(new Intent(HomeActivity.this, AtoolsActivity.class));
                        L.i("进入高级工具");
                        break;
                    case 8:
                        startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
                        break;
                    case 0:
                        showLostFindDialog();
                        L.i("手机防盗");
                        break;
                    case 1:
                        startActivity(new Intent(HomeActivity.this, CallSmsSafeActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
                        break;
                }
            }
        });


        //手机报警
        //首先判断防盗是否开启
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean is_fangdao = sp.getBoolean("is_fangdao", false);
        //如果开启了，则去判断SIM卡
        if (is_fangdao) {
            SimisChange();
        }



        /**
         * 判断归属地功能是否开启
         */

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showAddress = sharedPreferences.getBoolean("showAddress",false);
        Intent showAdd = new Intent(this, AddressService.class);

        if (showAddress){
            startService(showAdd);
        }else {
            stopService(showAdd);
        }


        /**
         * 判断密码锁功能是否开启
         */
        SharedPreferences sps = PreferenceManager.getDefaultSharedPreferences(this);
        boolean watchdog = sps.getBoolean("watchdog",false);
        Intent watchdogIntent = new Intent(this, WatchDogService.class);
        if (watchdog){
            startService(watchdogIntent);
        }else {
            stopService(watchdogIntent);
        }


    }

    private void showLostFindDialog() {
        //判断手机是否设置密码
        if (isSetuppwd()){
            //已经设置密码了，弹出对话框
            showEnterDialog();
        }else {
            //没有设置密码，弹出的是设置密码对话框
            showSetupPwDialog();
        }

    }

    private EditText et_setup_pwd;
    private EditText et_setup_confirm;
    private Button ok;
    private Button cancel;
    private AlertDialog dialog;

    //设置密码对话框
    private void showSetupPwDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_passwd, null);
        et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
        ok = (Button) view.findViewById(R.id.ok);
        cancel= (Button) view.findViewById(R.id.cancel);


        builder.setView(view);
        dialog = builder.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwd = et_setup_pwd.getText().toString().trim();
                String confirm = et_setup_confirm.getText().toString().trim();

                if (TextUtils.isEmpty(passwd) || TextUtils.isEmpty(confirm)){
                    Toast.makeText(HomeActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断是否一致
                if (passwd.equals(confirm)){
                    //一致的话，就保存密码，取消对话框，进入手机防盗页面
                    startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("passwd", MD5Utils.md5Password(passwd));
                    editor.commit();
                    dialog.dismiss();

                }else {
                    Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            dialog.dismiss();
            }
        });
    }

    //输入密码对话框
    private void showEnterDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_passwd, null);
        et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
        ok = (Button) view.findViewById(R.id.ok);
        cancel= (Button) view.findViewById(R.id.cancel);


        builder.setView(view);
        dialog = builder.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwd = et_setup_pwd.getText().toString().trim();
                String savepasswd = sp.getString("passwd", null);
                if (TextUtils.isEmpty(passwd)){
                    Toast.makeText(HomeActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (MD5Utils.md5Password(passwd).equals(savepasswd)){
                    //消掉对话框，进入主界面
                    dialog.dismiss();
                    startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                    L.i("进入手机防盗主界面");
                }else {
                    Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    et_setup_pwd.setText("");
                    return;
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //判断是否设置过密码
    private boolean isSetuppwd() {
        String passwd = sp.getString("passwd",null);

        L.i("是否设置过密码："+!TextUtils.isEmpty(passwd));
        return !TextUtils.isEmpty(passwd);
    }


    private class GridviewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this,R.layout.list_home,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            TextView textView = (TextView) view.findViewById(R.id.name);

            textView.setText(names[position]);
            imageView.setImageResource(pic_id[position]);

            return view;
        }
    }

    //监听手机SIM卡是否改变
    private TelephonyManager tm ;

    public void SimisChange(){
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String number = tm.getSimSerialNumber();
        L.i("SimSerialNumber:"+number);

        sp = getSharedPreferences("config",MODE_PRIVATE);
        String sim = sp.getString("sim", null);

        L.i("sim:"+sim);
        if (sim.equals(number+"1")){
            L.i("手机安全！！！");
        }else {

            MediaPlayer player = MediaPlayer.create(this,R.raw.alarm);
            player.setLooping(false);
            player.setVolume(1.0f,1.0f);
            player.start();
            L.i("报警！！！");
        }

    }
}
