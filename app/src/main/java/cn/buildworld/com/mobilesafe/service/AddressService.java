package cn.buildworld.com.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.Utils.NumAddressQueryUtils;

public class AddressService extends Service {
	
	/**
	 * 窗体管理者
	 */
	private WindowManager wm;
	private View view;

	/**
	 * 电话服务
	 */

	private TelephonyManager tm;
	private MyListenerPhone listenerPhone;
	
	private OutCallReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


    /**
     * 拨打电话
     */
	// 服务里面的内部类
	//广播接收者的生命周期和服务一样
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 这就是我们拿到的播出去的电话号码
			String phone = getResultData();
			// 查询数据库
			String address = NumAddressQueryUtils.getNumber(phone);
			
//			Toast.makeText(context, address, 1).show();
			myToast(address);
		}

	}

	private class MyListenerPhone extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// state：状态，incomingNumber：来电号码
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 来电铃声响起
				// 查询数据库的操作
				String address = NumAddressQueryUtils
						.getNumber(incomingNumber);
				
//				Toast.makeText(getApplicationContext(), address, 1).show();
				myToast(address);

				L.i("电话响起,来电号码："+incomingNumber);
				break;
				
			case TelephonyManager.CALL_STATE_IDLE://电话的空闲状态：挂电话、来电拒绝
				//把这个View移除
				if(view != null ){
					wm.removeView(view);
				}
			
				
				break;

			default:
				break;
			}
		}

	}

	private long[] mHits = new long[3];
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		// 监听来电
		listenerPhone = new MyListenerPhone();
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);
		
		//用代码去注册广播接收者
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
		//实例化窗体
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

	/**
	 * 自定义土司
	 * @param
	 */
	private WindowManager.LayoutParams params;
	private SharedPreferences sp;

	public void myToast(String address) {
	     view =   View.inflate(this, R.layout.address_show, null);
	    TextView textview  = (TextView) view.findViewById(R.id.tv_address);

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis()-500)) {
					params.x = wm.getDefaultDisplay().getWidth()/2 - view.getWidth()/2;
					wm.updateViewLayout(view,params);
					sp = getSharedPreferences("config",MODE_PRIVATE);
					SharedPreferences.Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.putInt("lasty", params.y);
					editor.commit();
				}
			}

		});

        //给view对象设置一个触摸的监听器
        view.setOnTouchListener(new View.OnTouchListener() {

            //定义手指的初始化位置
            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN://手指按下屏幕
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
//                        L.i("开始位置："+startX+","+startY);
                        break;
                    case MotionEvent.ACTION_MOVE://手指在屏幕上面移动
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();
//                        L.i("新的位置："+newX+","+newY);

                        int dx = newX-startX;
                        int dy = newY-startY;
//                        L.i("更新imageview在窗口上的位置，偏移量为："+dx+","+dy);

						params.x+= dx;
						params.y+= dy;

						//考虑边界问题
						if (params.x<0){
							params.x = 0;
						}
						if (params.y<0){
							params.y = 0;
						}
						if (params.x>(wm.getDefaultDisplay().getWidth()-view.getWidth())){
						params.x=wm.getDefaultDisplay().getWidth()-view.getWidth();
						}
						if (params.y>(wm.getDefaultDisplay().getHeight()-view.getHeight())){
							params.y=wm.getDefaultDisplay().getHeight()-view.getHeight();
						}


						wm.updateViewLayout(view,params);

                        //重新初始化手指的开始结束位置
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP://手指离开屏幕
//						L.i("手指离开控件");
						     sp = getSharedPreferences("config",MODE_PRIVATE);
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt("lastx", params.x);
						editor.putInt("lasty", params.y);
						editor.commit();
                        break;
                }
                return false;
            }
        });


	    //"半透明","活力橙","卫士蓝","金属灰","苹果绿"
		int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_blue
				,R.drawable.call_locate_gray,R.drawable.call_locate_green};
	    SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
	    view.setBackgroundResource(ids[sp.getInt("which", 2)]);
	    textview.setText(address);
		//窗体的参数就设置好了
		 params = new WindowManager.LayoutParams();

         params.height = WindowManager.LayoutParams.WRAP_CONTENT;
         params.width = WindowManager.LayoutParams.WRAP_CONTENT;

		//初始的Toast位置在左上角
		 params.gravity = Gravity.TOP + Gravity.LEFT;
		 //窗体的位置是上次最后处于的位置
		 params.x = sp.getInt("lastx", 0);
		 params.y = sp.getInt("lasty", 0);

         params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                 | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
         params.format = PixelFormat.TRANSLUCENT;

		//电话优先级的一种窗体类型，记得添加权限
         params.type = WindowManager.LayoutParams.TYPE_TOAST;
		wm.addView(view, params);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 取消监听来电
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone = null;
		
		//用代码取消注册广播接收者
		unregisterReceiver(receiver);
		receiver = null;

	}

}
