package cn.buildworld.com.mobilesafe.Utils;

/**
 * 作者：MiChong on 2017/4/24 0024 17:04
 * 邮箱：1564666023@qq.com
 */

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 短信的工具类
 */
public class SmsUtils {


    /**
     * 备份短信的回调接口
     */
    public  interface BackUpCallBack{

        /**
         * 开始备份时，设置进度的最大值
         * @param max 总进度
         */
        public void beforeBackup(int max);

        /**
         * 备份过程中，增加进度
         * @param progress 当前进度
         */
        public void onSmsBackup(int progress);
    }

    /**
     *
     * @param context 上下文
     * @throws IOException
     * @throws InterruptedException
     */

    public static void backupSms(Context context, BackUpCallBack callBack) throws IOException, InterruptedException {

        ContentResolver resolver = context.getContentResolver();

        File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");

        FileOutputStream fp = new FileOutputStream(file);

        //把用户短信一条条读出来
        XmlSerializer serializer = Xml.newSerializer();
        //初始化生成器
        serializer.setOutput(fp,"utf-8");
        serializer.startDocument("utf-8",true);
        serializer.startTag(null,"smss");

        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"body", "address", "type", "date"}, null, null, null);
        int max = cursor.getCount();
//        pd.setMax(max);
        callBack.beforeBackup(max);
        serializer.attribute(null,"max", String.valueOf(max));
        int process = 0;

        while (cursor.moveToNext()){

            Thread.sleep(500);
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);

            serializer.startTag(null,"sms");
            serializer.startTag(null,"body");
            serializer.text(body);
            serializer.endTag(null,"body");

            serializer.startTag(null,"address");
            serializer.text(address);
            serializer.endTag(null,"address");

            serializer.startTag(null,"type");
            serializer.text(type);
            serializer.endTag(null,"type");

            serializer.startTag(null,"date");
            serializer.text(date);
            serializer.endTag(null,"date");

            serializer.endTag(null,"sms");

            //备份过程中增加进度
            process ++;
//            pd.setProgress(process);

            callBack.onSmsBackup(process);
        }
        cursor.close();

        serializer.endTag(null,"smss");
        serializer.endDocument();
        fp.close();
    }


    /**
     * 短信恢复功能实现
     * @param context
     * @param flag 是否清理原来的短信
     */
    public static void restoreSms(Context context,boolean flag){

        //1、读取sd卡上的xml文件
        //2、d读取max
        //3、读取每一条短信信息
        //4、把短信插入到系统短信应用

        Uri uri = Uri.parse("content://sms/");
        if (flag) {
            context.getContentResolver().delete(uri, null, null);
        }

        ContentValues values = new ContentValues();
        values.put("body","我是短信的内容");
        values.put("date","1493116111111");
        values.put("type","1");
        values.put("address","18130104479");
        String s = values.toString();
        L.i("短信内容："+s);
        context.getContentResolver().insert(uri,values);

    }
}
