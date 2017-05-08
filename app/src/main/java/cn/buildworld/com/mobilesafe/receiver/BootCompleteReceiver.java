package cn.buildworld.com.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.TelephonyManager;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;

/**
 * 作者：MiChong on 2017/4/9 0009 20:02
 * 邮箱：1564666023@qq.com
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    private SharedPreferences sp ;
    private TelephonyManager tm ;
    @Override
    public void onReceive(Context context, Intent intent) {

        //读取之前存储的SIM卡的信息
        sp = context.getSharedPreferences("config",context.MODE_PRIVATE);
        String oldSim = sp.getString("sim",null);

        //读取当前SIM卡的信息

        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String nowSim = tm.getSimSerialNumber();

        //比较SIM卡信息是否变更
        if (oldSim.equals(nowSim)){
            L.i("手机安全");
        }else {

            MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
            player.setLooping(false);
            player.setVolume(1.0f,1.0f);
            player.start();

            L.i("手机SIM卡已经变更！！！");
        }
    }
}
