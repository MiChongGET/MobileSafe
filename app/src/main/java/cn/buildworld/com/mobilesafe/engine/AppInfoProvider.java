package cn.buildworld.com.mobilesafe.engine;

/**
 * 作者：MiChong on 2017/4/27 0027 15:34
 * 邮箱：1564666023@qq.com
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import cn.buildworld.com.mobilesafe.bean.AppInfo;

/**
 * 业务方法，提供手机里面安装的所有的应用程序信息
 */
public class AppInfoProvider {
    /**
     * 获取所有的安装的应用程序信息
     */
    public static List<AppInfo> getAppInfos(Context context){
        PackageManager manager = context.getPackageManager();
        //所有的安装在系统上的应用程序包信息
        List<PackageInfo> infoList = manager.getInstalledPackages(0);

        List<AppInfo> appInfos = new ArrayList<>();
        for (PackageInfo packageInfo :infoList){
            //获取app的包名称
            String packageName = packageInfo.packageName;
            //获取app的图标
            Drawable icon = packageInfo.applicationInfo.loadIcon(manager);
            String name = packageInfo.applicationInfo.loadLabel(manager).toString();

            AppInfo appInfo = new AppInfo();
            //应用程序信息标记
            int flags = packageInfo.applicationInfo.flags;
            if( (flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                //代表是用户程序
                appInfo.setUserApp(true);
            }else {
                //代表系统程序
                appInfo.setUserApp(false);
            }

            if( (flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){
                //代表是内存
                appInfo.setInRom(true);
            }else {
                //代表外存
                appInfo.setInRom(false);
            }

            appInfo.setIcon(icon);
            appInfo.setName(name);
            appInfo.setPackname(packageName);
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
