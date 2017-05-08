package cn.buildworld.com.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：MiChong on 2017/5/6 0006 16:07
 * 邮箱：1564666023@qq.com
 */
public class ApplockDao {
    private AppLockDBOpenHelper helper;
    private Context context;

    /**
     * 构造方法
     * @param context 上下文
     */
    public ApplockDao(Context context) {
        helper = new AppLockDBOpenHelper(context);
        this.context = context;
    }

    public void add(String packname){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname",packname);
        db.insert("applock",null,values);
        db.close();
        Intent intent = new Intent();
        intent.setAction("updateDb");
        context.sendBroadcast(intent);

    }

    public void delete(String packname){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("applock","packname=?",new String[]{packname});
        db.close();
        Intent intent = new Intent();
        intent.setAction("updateDb");
        context.sendBroadcast(intent);
    }


    /**
     * 查询包名是否存在
     * @param packname
     * @return
     */
    public boolean find(String packname){
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
        if (cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }


    /**
     * 查询所有的包名
     * @return
     */
    public List<String> findAll(){
        List<String> packnames = new ArrayList<String>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packname"},null,null, null, null, null);
        while (cursor.moveToNext()){
            packnames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packnames;
    }
}
