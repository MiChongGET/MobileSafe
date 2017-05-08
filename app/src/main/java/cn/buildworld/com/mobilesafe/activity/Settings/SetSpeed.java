package cn.buildworld.com.mobilesafe.activity.Settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;

import org.json.JSONException;
import org.json.JSONObject;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;

public class SetSpeed extends AppCompatActivity implements SpeedFragment.CallBackValue {

    private static final String TAG = "讯飞语音识别：";
    private SharedPreferences preferences;
    private Button isv_register;
    private Button isv_verify;
    private Button isv_delete;

    private TextView mShowPwdTextView;
    private TextView mShowMsgTextView;
    private TextView mShowRegFbkTextView;
    private TextView mRecordTimeTextView;

    private SpeakerVerifier mVerifier;
    private static final int PWD_TYPE_TEXT = 1;
    // 自由说由于效果问题，暂不开放
//	private static final int PWD_TYPE_FREE = 2;
    private static final int PWD_TYPE_NUM = 3;
    // 当前声纹密码类型，1、2、3分别为文本、自由说和数字密码
    private int mPwdType = PWD_TYPE_TEXT;
    private Toast mToast;

    private String mAuthId = "";
    // 文本声纹密码
    private String mTextPwd = "芝麻开门";
    // 数字声纹密码
    private String mNumPwd = "";
    // 数字声纹密码段，默认有5段
    private String[] mNumPwdSegs;
    private LinearLayout showText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_speed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);

        toolbar.setTitle("语音设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        SpeedFragment sf = new SpeedFragment();
        getFragmentManager().beginTransaction().add(R.id.control_speed,sf).commit();

        //语音识别模块实现
        isv_register = (Button) findViewById(R.id.isv_register1);
        isv_verify  = (Button) findViewById(R.id.isv_verify1);
        isv_delete = (Button) findViewById(R.id.isv_delete);
        showText = (LinearLayout) findViewById(R.id.showText);

        //获取语音识别模块是否开启
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean is_open_speed = preferences.getBoolean("is_open_speed",false);
        /**
         * 设置按钮是否隐藏
         */
        if (is_open_speed){
            isv_verify.setVisibility(View.VISIBLE);
            isv_register.setVisibility(View.VISIBLE);
            isv_delete.setVisibility(View.VISIBLE);
            showText.setVisibility(View.VISIBLE);

            mVerifier = SpeakerVerifier.createVerifier(SetSpeed.this, new InitListener() {
                @Override
                public void onInit(int i) {

                    if (ErrorCode.SUCCESS == i){
                        L.i("初始话引擎成功！！！");
                    }
                    else L.i("初始化引擎失败，错误码："+i);
                }
            });

        }else {
            isv_register.setVisibility(View.INVISIBLE);
            isv_verify.setVisibility(View.INVISIBLE);
            isv_delete.setVisibility(View.INVISIBLE);
            showText.setVisibility(View.INVISIBLE);
        }

        //获取SIM卡的序列号
        SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
        String simNum = sp.getString("sim", null);
        mAuthId = simNum;
        L.i("获取到的参数："+mAuthId);


        //显示界面
        mShowPwdTextView = (TextView) findViewById(R.id.showPwd);
        mShowMsgTextView = (TextView) findViewById(R.id.showMsg);
        mShowRegFbkTextView = (TextView) findViewById(R.id.showRegFbk);
        mRecordTimeTextView = (TextView) findViewById(R.id.recordTime);

        //吐司
        mToast = Toast.makeText(SetSpeed.this, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    //三大按钮处理模块

    /**
     * 语音注册模块
     * @param view
     */
    public void register(View view){
        L.i("语音注册！");
        if( !checkInstance() ){
            return;
        }
        mVerifier.setParameter(SpeechConstant.PARAMS, null);
        mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test.pcm");
        // 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
//			mVerify.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);
        if (mPwdType == PWD_TYPE_TEXT) {
            // 文本密码注册需要传入密码
//            if (TextUtils.isEmpty(mTextPwd)) {
//                showTip("请获取密码后进行操作");
//                return;
//            }
            mVerifier.setParameter(SpeechConstant.ISV_PWD, mTextPwd);
            mShowPwdTextView.setText("请读出：" + mTextPwd);
            mShowMsgTextView.setText("训练 第" + 1 + "遍，剩余4遍");
        } else if (mPwdType == PWD_TYPE_NUM) {
            // 数字密码注册需要传入密码
            if (TextUtils.isEmpty(mNumPwd)) {
                showTip("请获取密码后进行操作");
                return;
            }
            mVerifier.setParameter(SpeechConstant.ISV_PWD, mNumPwd);
            ((TextView) findViewById(R.id.showPwd)).setText("请读出："
                    + mNumPwd.substring(0, 8));
            mShowMsgTextView.setText("训练 第" + 1 + "遍，剩余4遍");
        }

//                setRadioClickable(false);
        // 设置auth_id，不能设置为空
        mVerifier.setParameter(SpeechConstant.AUTH_ID, mAuthId);
        // 设置业务类型为注册
        mVerifier.setParameter(SpeechConstant.ISV_SST, "train");
        // 设置声纹密码类型
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
        // 开始注册
        mVerifier.startListening(mRegisterListener);
    }


    /**
     * 语音验证模块
     * @param view
     */
    public void verify(View view){

        L.i("语音验证！");
        if( !checkInstance() ){
            return;
        }
        ((TextView) findViewById(R.id.showMsg)).setText("");
        // 清空参数
        mVerifier.setParameter(SpeechConstant.PARAMS, null);
        mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
        mVerifier = SpeakerVerifier.getVerifier();
        // 设置业务类型为验证
        mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");
        // 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
//			mVerify.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);

        if (mPwdType == PWD_TYPE_TEXT) {
            // 文本密码注册需要传入密码
//            if (TextUtils.isEmpty(mTextPwd)) {
//                showTip("请获取密码后进行操作");
//                return;
//            }
            mVerifier.setParameter(SpeechConstant.ISV_PWD, mTextPwd);
            ((TextView) findViewById(R.id.showPwd)).setText("请读出："
                    + mTextPwd);
        } else if (mPwdType == PWD_TYPE_NUM) {
            // 数字密码注册需要传入密码
            String verifyPwd = mVerifier.generatePassword(8);
            mVerifier.setParameter(SpeechConstant.ISV_PWD, verifyPwd);
            ((TextView) findViewById(R.id.showPwd)).setText("请读出："
                    + verifyPwd);
        }
//                setRadioClickable(false);
        // 设置auth_id，不能设置为空
        mVerifier.setParameter(SpeechConstant.AUTH_ID, mAuthId);
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);
        // 开始验证
        mVerifier.startListening(mVerifyListener);

    }

    /**
     *语音模型删除
     * @param view
     */
    public void delete(View view){
        performModelOperation("del", mModelOperationListener);
    }



    //讯飞功能处理模块
    /**
     * 执行模型操作
     *
     * @param operation 操作命令
     * @param listener  操作结果回调对象
     */
    private void performModelOperation(String operation, SpeechListener listener) {
        // 清空参数
        mVerifier.setParameter(SpeechConstant.PARAMS, null);
        mVerifier.setParameter(SpeechConstant.ISV_PWDT, "" + mPwdType);

        if (mPwdType == PWD_TYPE_TEXT) {
            // 文本密码删除需要传入密码
            if (TextUtils.isEmpty(mTextPwd)) {
                showTip("请获取密码后进行操作");
                return;
            }
            mVerifier.setParameter(SpeechConstant.ISV_PWD, mTextPwd);
        } else if (mPwdType == PWD_TYPE_NUM) {

        }

        // 设置auth_id，不能设置为空
        mVerifier.sendRequest(operation, mAuthId, listener);
    }




    private VerifierListener mVerifyListener = new VerifierListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onResult(VerifierResult result) {
//            setRadioClickable(true);
            mShowMsgTextView.setText(result.source);

            if (result.ret == 0) {
                // 验证通过
                mShowMsgTextView.setText("验证通过");
            }
            else{
                // 验证不通过
                switch (result.err) {
                    case VerifierResult.MSS_ERROR_IVP_GENERAL:
                        mShowMsgTextView.setText("内核异常");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
                        mShowMsgTextView.setText("出现截幅");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
                        mShowMsgTextView.setText("太多噪音");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
                        mShowMsgTextView.setText("录音太短");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
                        mShowMsgTextView.setText("验证不通过，您所读的文本不一致");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
                        mShowMsgTextView.setText("音量太低");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
                        mShowMsgTextView.setText("音频长达不到自由说的要求");
                        break;
                    default:
                        mShowMsgTextView.setText("验证不通过");
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
                    mShowMsgTextView.setText("模型不存在，请先注册");
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

    private SpeechListener mModelOperationListener = new SpeechListener() {

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
//            setRadioClickable(true);

            String result = new String(buffer);
            try {
                JSONObject object = new JSONObject(result);
                String cmd = object.getString("cmd");
                int ret = object.getInt("ret");

                if ("del".equals(cmd)) {
                    if (ret == ErrorCode.SUCCESS) {
                        showTip("删除成功");
//                        mResultEditText.setText("");
                    } else if (ret == ErrorCode.MSP_ERROR_FAIL) {
                        showTip("删除失败，模型不存在");
                    }
                } else if ("que".equals(cmd)) {
                    if (ret == ErrorCode.SUCCESS) {
                        showTip("模型存在");
                    } else if (ret == ErrorCode.MSP_ERROR_FAIL) {
                        showTip("模型不存在");
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        @Override
        public void onCompleted(SpeechError error) {
//            setRadioClickable(true);

            if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
                showTip("操作失败：" + error.getPlainDescription(true));
            }
        }
    };

    @Override
    public void SentMesValue(boolean value) {
        L.i("语音是否开启："+value);
        if (!value){
            isv_register.setVisibility(View.INVISIBLE);
            isv_verify.setVisibility(View.INVISIBLE);
            isv_delete.setVisibility(View.INVISIBLE);
            showText.setVisibility(View.INVISIBLE);
        }else {
            isv_verify.setVisibility(View.VISIBLE);
            isv_delete.setVisibility(View.VISIBLE);
            isv_register.setVisibility(View.VISIBLE);
            showText.setVisibility(View.VISIBLE);

            //打开语音开关就初始化一下
            mVerifier = SpeakerVerifier.createVerifier(SetSpeed.this, new InitListener() {
                @Override
                public void onInit(int i) {

                    if (ErrorCode.SUCCESS == i){
                        L.i("初始话引擎成功！！！");
                    }
                    else L.i("初始化引擎失败，错误码："+i);
                }
            });
        }
    }



    private VerifierListener mRegisterListener = new VerifierListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onResult(VerifierResult result) {
            ((TextView)findViewById(R.id.showMsg)).setText(result.source);

            if (result.ret == ErrorCode.SUCCESS) {
                switch (result.err) {
                    case VerifierResult.MSS_ERROR_IVP_GENERAL:
                        mShowMsgTextView.setText("内核异常");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_EXTRA_RGN_SOPPORT:
                        mShowRegFbkTextView.setText("训练达到最大次数");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
                        mShowRegFbkTextView.setText("出现截幅");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
                        mShowRegFbkTextView.setText("太多噪音");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
                        mShowRegFbkTextView.setText("录音太短");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
                        mShowRegFbkTextView.setText("训练失败，您所读的文本不一致");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
                        mShowRegFbkTextView.setText("音量太低");
                        break;
                    case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
                        mShowMsgTextView.setText("音频长达不到自由说的要求");
                    default:
                        mShowRegFbkTextView.setText("");
                        break;
                }

                if (result.suc == result.rgn) {
//                    setRadioClickable(true);
                    mShowMsgTextView.setText("注册成功");

                    if (PWD_TYPE_TEXT == mPwdType) {
                        Toast.makeText(SetSpeed.this, "您的文本密码声纹ID：\n" + result.vid, Toast.LENGTH_SHORT).show();
                    } else if (PWD_TYPE_NUM == mPwdType) {
//                        mResultEditText.setText("您的数字密码声纹ID：\n" + result.vid);
                    }

                } else {
                    int nowTimes = result.suc + 1;
                    int leftTimes = result.rgn - nowTimes;

                    if (PWD_TYPE_TEXT == mPwdType) {
                        mShowPwdTextView.setText("请读出：" + mTextPwd);
                    } else if (PWD_TYPE_NUM == mPwdType) {
                        mShowPwdTextView.setText("请读出：" + mNumPwdSegs[nowTimes - 1]);
                    }

                    mShowMsgTextView.setText("训练 第" + nowTimes + "遍，剩余" + leftTimes + "遍");
                }

            }else {
//                setRadioClickable(true);

                mShowMsgTextView.setText("注册失败，请重新开始。");
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

            if (error.getErrorCode() == ErrorCode.MSP_ERROR_ALREADY_EXIST) {
                showTip("模型已存在，如需重新注册，请先删除");
            } else {
                showTip("onError Code：" + error.getPlainDescription(true));
            }
        }

        @Override
        public void onEndOfSpeech() {
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
        }
    };


    /**
     * 初始化TextView和密码文本
     */
    private void initTextView(){
        mTextPwd = null;
        mNumPwd = null;
        mShowPwdTextView.setText("");
        mShowMsgTextView.setText("");
        mShowRegFbkTextView.setText("");
        mRecordTimeTextView.setText("");
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
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
}


/**
 * 语音开关模块
 */


class SpeedFragment extends PreferenceFragment {

    CallBackValue callBackValue ;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callBackValue = (CallBackValue) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.is_open_speed);


        PreferenceManager manager = getPreferenceManager();
        SharedPreferences preferences = manager.getDefaultSharedPreferences(getActivity());
        boolean is_open_speed = preferences.getBoolean("is_open_speed",false);
        L.i(is_open_speed+"");
    }

    //此处主要是处理preference状态的变化
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        SharedPreferences sharedPreferences = preference.getSharedPreferences();
        boolean is_open_speed = sharedPreferences.getBoolean("is_open_speed",false);

        L.i("监听语音识别："+is_open_speed);

        callBackValue.SentMesValue(is_open_speed);

        return true;
    }


    //使用回调使得状态可以及时地传递过去
    public interface CallBackValue{
        public void SentMesValue(boolean value);
    }
}
