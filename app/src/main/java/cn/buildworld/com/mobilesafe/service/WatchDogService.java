package cn.buildworld.com.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.activity.appmanager.EnterPwActivity;
import cn.buildworld.com.mobilesafe.dao.ApplockDao;

/**
 * 作者：MiChong on 2017/5/7 0007 10:46
 * 邮箱：1564666023@qq.com
 */
public class WatchDogService extends Service {
    private ActivityManager am;
    private boolean flag;
    private ApplockDao applockDao;
    private InnerReceiver innerReceiver;
    private String tempStopProtectName = "";//临时停止保护的包名
    private ScreenOffReceiver screenOffReceiver;

    private List<String> packnames;
    private DataChangeReceiver dataChangeReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //锁屏广播
    private class ScreenOffReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectName = null;
        }
    }

    //设置一个内部的广播
    private class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            L.i("接收到了临时停止广播事件！！！");
            tempStopProtectName = intent.getStringExtra("packname");
        }
    }

    //数据库变化广播
    private class DataChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            L.i("数据库更新！！！");
            packnames = applockDao.findAll();
        }
    }

    @Override
    public void onCreate() {
        screenOffReceiver = new ScreenOffReceiver();
        registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver,new IntentFilter("cn.buildworld.com.mobilesafe.tempstop"));

        dataChangeReceiver = new DataChangeReceiver();
        registerReceiver(dataChangeReceiver,new IntentFilter("updateDb"));

        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        flag = true;
        L.i("密码锁服务开启");
        applockDao = new ApplockDao(this);
        packnames = applockDao.findAll();
        L.i("数据库集合："+packnames.toString());

        new Thread(){
            @Override
            public void run() {
                while (flag){
                    //获取当前在任务栈顶端的程序，也就是当前运行的程序
                    List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
                    String packageName = infos.get(0).topActivity.getPackageName();
//                    L.i("当前运行的程序包名："+packageName);
//                    L.i("取消保护的包名："+tempStopProtectName);
                    //此处查询数据库使用时间比较多，影响性能发挥

                    if (packnames.contains(packageName)){
                        //如果包名在数据库中存在，跳转到输入密码的界面
//                        L.i("取消保护的包名："+tempStopProtectName);

                        //判断程序是否临时取消保护
                        if (packageName.equals(tempStopProtectName)){

                        }else
                        {
                            Intent intent = new Intent(getApplicationContext(), EnterPwActivity.class);
                            //服务是没有任务栈信息的，在开启activity时要指定一个任务栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packname", packageName);
                            startActivity(intent);
                        }
                    }

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        flag = false;
        unregisterReceiver(innerReceiver);
        innerReceiver = null;

        unregisterReceiver(screenOffReceiver);
        screenOffReceiver = null;

        unregisterReceiver(dataChangeReceiver);
        dataChangeReceiver = null;
        super.onDestroy();
    }
}
