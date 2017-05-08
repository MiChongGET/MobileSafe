package cn.buildworld.com.mobilesafe.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.buildworld.com.mobilesafe.Utils.L;

/**
 * 作者：MiChong on 2017/4/22 0022 13:52
 * 邮箱：1564666023@qq.com
 */
public class BlackNumDBOpenHelper extends SQLiteOpenHelper {
    public BlackNumDBOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table blacknumber (_id integer primary key autoincrement,number varchar(20),mode varchar(2))";

        L.i("表的创建");
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
