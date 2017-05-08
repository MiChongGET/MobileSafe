package cn.buildworld.com.mobilesafe.SpeedMode;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;

/**
 * 作者：MiChong on 2017/4/11 0011 21:28
 * 邮箱：1564666023@qq.com
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
        // 设置你申请的应用appid
        SpeechUtility.createUtility(this, "appid=58c7e3fe");
    }
}
