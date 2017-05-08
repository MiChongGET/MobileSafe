package cn.buildworld.com.mobilesafe.Utils;

/**
 * 作者：MiChong on 2017/4/4 0004 21:14
 * 邮箱：1564666023@qq.com
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
public class StreamTools {
    /**
     * @param is 输入流
     * @return String 返回的字符串
     * @throws IOException
     */
    public static String readFromStream(InputStream is) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = is.read(buffer))!=-1){
            baos.write(buffer, 0, len);
        }
        is.close();
        String result = baos.toString();
        baos.close();
        return result;
    }
}
