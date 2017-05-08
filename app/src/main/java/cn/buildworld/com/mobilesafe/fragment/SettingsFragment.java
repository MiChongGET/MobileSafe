package cn.buildworld.com.mobilesafe.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;

public class SettingsFragment extends PreferenceFragment {


    private CallBackValue callBackValue;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callBackValue = (CallBackValue) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);



        PreferenceManager manager = getPreferenceManager();
        SharedPreferences preferences = manager.getDefaultSharedPreferences(getActivity());

    }

    /**
     * 实时监听设置的变化
     * @param preferenceScreen
     * @param preference
     * @return
     */
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        SharedPreferences sharedPreferences = preference.getSharedPreferences();
        boolean isUpdate = sharedPreferences.getBoolean("isUpdate",true);
        boolean showAddress = sharedPreferences.getBoolean("showAddress",false);
        boolean isOpenBlackNum = sharedPreferences.getBoolean("isOpenBlackNum",false);
        boolean watchdog = sharedPreferences.getBoolean("watchdog",false);
//
//        L.i("是否升级："+isUpdate);
//        L.i("是否显示归属地："+showAddress);
//        L.i("是否开启黑名单："+isOpenBlackNum);
//        L.i("是否开启密码锁："+watchdog);

        callBackValue.SentValue(showAddress);
        callBackValue.isOpenBlackNum(isOpenBlackNum);
        callBackValue.WatchDog(watchdog);
        return true;
    }


    public interface CallBackValue{
        void SentValue(boolean value);
        void isOpenBlackNum(boolean v);
        void WatchDog(boolean value);
    }
}
