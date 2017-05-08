package cn.buildworld.com.mobilesafe.Utils;

import android.util.Log;

/**
 * 作者：MiChong on 2017/4/4 0004 19:08
 * 邮箱：1564666023@qq.com
 */
public class L {
    private static boolean isopen = true;
    private static String log = "michong:";

    public static void i(String s){
        if (isopen){
            Log.i(log,s);
        }

    }
}
