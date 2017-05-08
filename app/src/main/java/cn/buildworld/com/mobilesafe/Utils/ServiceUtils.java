package cn.buildworld.com.mobilesafe.Utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 作者：MiChong on 2017/4/18 0018 21:36
 * 邮箱：1564666023@qq.com
 */
public class ServiceUtils {

    /**
     * 校检某个服务是否还是活着
     */

    public static boolean isServiceRunning(Context context,String serviceName){
        //校检服务是否还活着

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);

        for (ActivityManager.RunningServiceInfo info : infos){
            String name = info.service.getClassName();
            if (serviceName.equals(name)){
                return true;
            }
        }
        return false;
    }

}
