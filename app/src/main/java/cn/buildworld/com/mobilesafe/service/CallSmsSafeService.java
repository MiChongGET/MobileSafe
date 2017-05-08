package cn.buildworld.com.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.dao.BlackNumDao;

public class CallSmsSafeService extends Service {

	private InnerSmsReceiver receiver;
	private BlackNumDao dao;
	private TelephonyManager tm;
	private Mylistener listener;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class InnerSmsReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {

			L.i("内部广播接收者，短信到来了");

			//检查发件人是否是黑名单号码，设置短信拦截
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");
			for (Object obj : pdus){

				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				//得到短信发件人
				String sender = smsMessage.getOriginatingAddress();
				String result = dao.findMode(sender);

				//短信拦截
				if (result.equals("2") || result.equals("3")){
					L.i("拦截短信！！！");
					abortBroadcast();
				}



			}
		}
	}

	@Override
	public void onCreate() {

		dao = new BlackNumDao(this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new Mylistener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		receiver = new InnerSmsReceiver();
		registerReceiver(receiver,new IntentFilter("Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT"));
		super.onCreate();
	}

	@Override
	public void onDestroy() {

		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener,PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}

	private class  Mylistener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {


			switch (state) {
				case TelephonyManager.CALL_STATE_RINGING://零响状态。
					String result = dao.findMode(incomingNumber);
					L.i("电话监听，拦截模式："+result);

					if("1".equals(result)||"3".equals(result)){
						L.i("挂断电话。。。。");
					}
					break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}
}
