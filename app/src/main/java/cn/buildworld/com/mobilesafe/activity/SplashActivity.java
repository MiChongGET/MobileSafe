package cn.buildworld.com.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.Utils.StreamTools;
import cn.buildworld.com.mobilesafe.activity.Settings.SetSpeed;

public class SplashActivity extends Activity {

    private static final int ENTER_HOME =0 ;
    private static final int SHOWDIALOG =1;
    private static final int JSON_ERROE =2;
    private static final int NET_ERROR = 3;
    private static final int URL_ERROR = 4;
    private static final int TIME_OUT = 5;
    private static final String TAG = "初始界面";
    private TextView verison;
    private String version;
    private AlertDialog.Builder alertDialog;
    private  String description;
    private ProgressBar progressBar;
    private RelativeLayout rl_splash;
    private HttpURLConnection conn;

    /**
     * 语音识别模块
     */
    private RecognizerDialog dialog;
    private SpeakerVerifier mVerifier;
    private Toast toast;
    private static final int PWD_TYPE_TEXT = 1;
    // 自由说由于效果问题，暂不开放
//	private static final int PWD_TYPE_FREE = 2;
    private static final int PWD_TYPE_NUM = 3;
    // 当前声纹密码类型，1、2、3分别为文本、自由说和数字密码
    private int mPwdType = PWD_TYPE_TEXT;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    goHome();
                    Toast.makeText(SplashActivity.this, "进入主界面！！！", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    showDialog();
                    break;
                case 2:
                    goHome();
                    Toast.makeText(SplashActivity.this, "json解析出错！！！", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    goHome();
                    Toast.makeText(SplashActivity.this, "网络错误！！！", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    goHome();
                    Toast.makeText(SplashActivity.this, "网络地址错误！！！", Toast.LENGTH_SHORT).show();
                case 5:
                    goHome();
                    Toast.makeText(SplashActivity.this, "网络连接超时！！！", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        verison = (TextView) findViewById(R.id.tv_splash_version);
        verison.setText("版本:"+getVersion());


        //获取preference设置的值
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isUpdate = preferences.getBoolean("isUpdate",true);
        L.i("是否升级："+isUpdate);

//        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f,1.0f);
//        alphaAnimation.setDuration(500);
//        rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
//        rl_splash.setAnimation(alphaAnimation);


        /**
         * 语音UI模块
         */
        toast = Toast.makeText(SplashActivity.this, "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);
        dialog = new RecognizerDialog(this,mInitListener);

        if (isSpeedOpen()){
           // dialog.show();
            useSpeed();

        }else if (isUpdate){
            checkupdate();
        }else  {
            //延迟两秒钟进入
//            Timer timer = new Timer();
//            TimerTask timerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    goHome();
//                }
//            };
//            timer.schedule(timerTask,1000);
            goHome();
        }



        /**
         * 手机归属地查询数据库资料拷贝
         */
        copyDB();

    }

    private void copyDB() {

        try {
            File file = new File(getFilesDir(),"address.db");
            //首先判断文件是否存在，如果存在就不去拷贝文件
            if (file.exists() && file.length()>0) {
                L.i("数据库已经存在！！！");
            }else {
                InputStream open = getAssets().open("address.db");

                FileOutputStream fp = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = open.read(bytes)) != -1) {
                    fp.write(bytes, 0, len);
                }
                fp.close();
                open.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //检查更新
    private void checkupdate() {


        new Thread(){
            @Override
            public void run() {
                super.run();
                Message msg = null;

                long starTime = 0;
                try {
                    msg= Message.obtain();
                    starTime = System.currentTimeMillis();

                    URL url = null;
                    url = new URL(getString(R.string.versionurl));

                    conn= (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    //设置网络连接超时
                    conn.setConnectTimeout(5000);
                    int code = conn.getResponseCode();
                    if (code==200){
                        InputStream inputStream = conn.getInputStream();
                        String s = StreamTools.readFromStream(inputStream);

                        JSONObject object = new JSONObject(s);
                        version = object.getString("version");
                        description = object.getString("description");

                        if (version.equals(getVersion())){

                            msg.what=ENTER_HOME;
                        }else {
                            msg.what=SHOWDIALOG;
                        }

                    }

                }
                catch (ConnectTimeoutException e){
                    msg.what = TIME_OUT;
                    e.printStackTrace();
                }
                catch (MalformedURLException e) {
                    msg.what = URL_ERROR;
                    e.printStackTrace();
                }
                catch (JSONException e) {
                    msg.what=JSON_ERROE;
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what=NET_ERROR;
                    e.printStackTrace();
                } finally {

                    conn.disconnect();
                    long endTime = System.currentTimeMillis();
                    long totalTime = endTime-starTime;
                    if(totalTime<2000){
                        try {
                            Thread.sleep(2000-totalTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    handler.sendMessage(msg);
                }
            }
        }.start();

    }

    //获取版本号
    private String getVersion(){

        PackageManager pm = getPackageManager();

        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);

            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "版本号获取错误";
        }

    }

    /**
     * 设置更新提示dialog
     */
    public void showDialog(){
        alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("版本更新")
                .setIcon(R.mipmap.ic_launcher_round)
                .setMessage(description)
                .setCancelable(false)//强制选择，无法按返回键
                .setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //判断SD卡是否存在
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        }else{
                        }

                        //进入主界面，并且使得dialog消失
                        goHome();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("下次更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goHome();
                    }
                });
        alertDialog.show();

    }


    /**
     * 语音识别是否开启
     */
    public boolean isSpeedOpen(){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isOpenSpeed = sp.getBoolean("is_open_speed", false);
        return isOpenSpeed;
    }

    /**
     * 识别功能设置
     */
    public void useSpeed(){
        L.i("语音验证！");

        //初始化
        mVerifier = SpeakerVerifier.createVerifier(SplashActivity.this, new InitListener() {
            @Override
            public void onInit(int i) {

                if (ErrorCode.SUCCESS == i){
                    L.i("初始话引擎成功！！！");
                }
                else L.i("初始化引擎失败，错误码："+i);
            }
        });

        SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
        String sim = sp.getString("sim", null);

        if( !checkInstance() ){
            return;
        }
        // 清空参数
        mVerifier.setParameter(SpeechConstant.PARAMS, null);
        mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
        mVerifier = SpeakerVerifier.getVerifier();
        // 设置业务类型为验证
        mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");
        // 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
//			mVerify.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);


        mVerifier.setParameter(SpeechConstant.ISV_PWD, "芝麻开门");


        // 设置auth_id，不能设置为空
        mVerifier.setParameter(SpeechConstant.AUTH_ID, sim);
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
        // 开始验证
        mVerifier.startListening(mVerifyListener);

    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(SplashActivity.this, "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
            }
        }
    };

//    /**
//     * 听写UI监听器
//     */
//
//
//    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
//        public void onResult(RecognizerResult results, boolean isLast) {
//            L.i("onResult");
//
//        }
//
//        /**
//         * 识别回调错误.
//         */
//        public void onError(SpeechError error) {
//            Toast.makeText(SplashActivity.this, error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
//        }
//
//    };


    private VerifierListener mVerifyListener = new VerifierListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onResult(VerifierResult result) {

            if (result.ret == 0) {
                // 验证通过
                toast.setText("验证通过");
                toast.show();

                //当验证通过进入主界面
                goHome();
            }
            else{
                // 验证不通过
                switch (result.err) {
                    case VerifierResult.MSS_ERROR_IVP_GENERAL:
                        toast.setText("内核异常");
                        toast.show();
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
                        toast.setText("出现截幅");
                        toast.show();
                        break;
                    case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
                        toast.setText("太多噪音");
                        toast.show();
                        break;
                    case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
                        toast.setText("录音太短");
                        toast.show();
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
                        toast.setText("验证不通过，您所读的文本不一致");
                        toast.show();
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
                        toast.setText("音量太低");
                        toast.show();
                        break;
                    case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
                        toast.setText("音频长达不到自由说的要求");
                        toast.show();
                        break;
                    default:
                        toast.setText("验证不通过");
                        toast.show();
                        break;
                }
            }
        }
        // 保留方法，暂不用
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }

        @Override
        public void onError(SpeechError error) {
//            setRadioClickable(true);

            switch (error.getErrorCode()) {
                case ErrorCode.MSP_ERROR_NOT_FOUND:
                   // mShowMsgTextView.setText("模型不存在，请先注册");
                    break;

                default:
                    showTip("onError Code："	+ error.getPlainDescription(true));
                    break;
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }
    };

    private void showTip(final String str) {
        toast.setText(str);
        toast.show();
    }

    private boolean checkInstance(){
        if( null == mVerifier ){
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip( "创建对象失败，请确认 libmsc.so 放置正确，\n 且有调用 createUtility 进行初始化" );
            return false;
        }else{
            return true;
        }
    }

    /**
     * 直接进入到主界面
     */
    public void goHome(){
        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        finish();
    }

}
