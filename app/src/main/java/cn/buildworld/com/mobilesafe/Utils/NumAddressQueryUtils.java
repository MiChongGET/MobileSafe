package cn.buildworld.com.mobilesafe.Utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 作者：MiChong on 2017/4/17 0017 17:16
 * 邮箱：1564666023@qq.com
 */
public class NumAddressQueryUtils  {

    public static String url = "data/data/cn.buildworld.com.mobilesafe/files/address.db";


    public static String getNumber(String num){

        String location = null;

        if (num.matches("^1[3,4,5,6,7,8]\\d{9}$")){
            SQLiteDatabase database = SQLiteDatabase.openDatabase(url, null, SQLiteDatabase.OPEN_READONLY);

            Cursor cursor = database.rawQuery("SELECT location FROM data2 WHERE id " +
                            "= (select outkey from data1 where id=?)",
                    new String[]{num.substring(0, 7)});

            while (cursor.moveToNext()){
                location = cursor.getString(0);
            }

            cursor.close();
            return location;
        }else if (num.equals("110")){
            return "淮南市公安局";
        }
        else {
            return "空";
        }


    }
}
