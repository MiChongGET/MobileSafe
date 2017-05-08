package cn.buildworld.com.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.bean.BlackNumInfo;

/**
 * 作者：MiChong on 2017/4/22 0022 14:08
 * 邮箱：1564666023@qq.com
 */
public class BlackNumDao {

    private BlackNumDBOpenHelper dbOpenHelper ;

    public BlackNumDao(Context context) {
        dbOpenHelper = new BlackNumDBOpenHelper(context);
//        L.i("数据库创建");
    }

    /**
     * 查找输入的电话号码是否存在
     * @param num 要查询的电话号码
     * @return
     */
    public boolean find(String num){
        boolean result = false;
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from blacknumber where number=?", new String[]{num});
        while (cursor.moveToNext()){
            result = true;
        }

        cursor.close();
        database.close();

        return result;
    }

    /**
     * 查找输入的电话号码的拦截模式
     * @param num 要查询的电话号码
     * @return
     */
    public String findMode(String num){
        String result = null;
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("select mode from blacknumber where number=?", new String[]{num});
        while (cursor.moveToNext()){
            result = cursor.getString(0);
        }

        cursor.close();
        database.close();

        return result;
    }


    /**
     * 查找输入的电话号码是否存在
     *
     * @return
     */
    public List<BlackNumInfo> findAll(){
        List<BlackNumInfo> list = new ArrayList<BlackNumInfo>();
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("select number,mode from blacknumber order by _id desc", null);
        while (cursor.moveToNext()){

            BlackNumInfo info = new BlackNumInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);

            info.setNum(number);
            info.setMode(mode);

            list.add(info);
        }

        cursor.close();
        database.close();

        return list;
    }

    /**
     * 查找部分号码是否存在
     * @param offset 从指定位置
     * @param maxNumber 一次最多获取多少条数据
     * @return
     */
    public List<BlackNumInfo> findPart(int offset,int maxNumber){
        List<BlackNumInfo> list = new ArrayList<BlackNumInfo>();
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("select number,mode from blacknumber order by _id desc LIMIT ? OFFSET ?",
                new String[]{String.valueOf(maxNumber), String.valueOf(offset)});
        while (cursor.moveToNext()){

            BlackNumInfo info = new BlackNumInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);

            info.setNum(number);
            info.setMode(mode);

            list.add(info);
        }

        cursor.close();
        database.close();

        return list;
    }

    /**
     * 添加黑名单
     * @param num 要添加的号码
     * @param mode 号码的模式
     */
    public void addBlackNum(String num,String mode){

        SQLiteDatabase database  = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number",num);
        values.put("mode",mode);

        database.insert("blacknumber",null,values);
        database.close();

        L.i("添加黑名单号码");
    }


    /**
     *更新号码拦截的模式
     * @param num 要更新的号码
     * @param newmode 要更改的模式
     */
    public void update(String num,String newmode){

        SQLiteDatabase database  = dbOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode",newmode);

        database.update("blacknumber",values,"number=?",new String[]{newmode});

        database.close();
    }

    /**
     * 删除指定的黑名单
     * @param num
     */
    public void delete(String num){
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();
        database.delete("blacknumber","number=?",new String[]{num});
        database.close();
    }
}
