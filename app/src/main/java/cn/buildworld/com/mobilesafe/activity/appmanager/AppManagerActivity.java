package cn.buildworld.com.mobilesafe.activity.appmanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.DensityUtil;
import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.adapter.AppManagerAdapter;
import cn.buildworld.com.mobilesafe.bean.AppInfo;
import cn.buildworld.com.mobilesafe.dao.ApplockDao;
import cn.buildworld.com.mobilesafe.engine.AppInfoProvider;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private TextView tv_avail_rom;
    private TextView tv_avail_sd;
    private List<AppInfo> appInfos;
    private LinearLayout ll_loading;
    private ListView lv_app_manager;
    private AppManagerAdapter adapter;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    private TextView showAppNum;
    private PopupWindow popupWindow ;
    private void dismissPopupWindow() {
        // 把旧的弹出窗体关闭掉。
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    //定义弹窗功能
    private LinearLayout ll_start;
    private LinearLayout ll_share;
    private LinearLayout ll_uninstall;

    //item条目
    private AppInfo appInfo;

    private ApplockDao applockDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);
        toolbar.setTitle("软件管理器");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        long sdsize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        long romsize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());

        L.i("SD卡可用空间："+Formatter.formatFileSize(this,sdsize));

        tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
        tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
        tv_avail_sd.setText("SD卡可用空间："+ Formatter.formatFileSize(this,sdsize));
        tv_avail_rom.setText("内存可用："+Formatter.formatFileSize(this,romsize));

        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);

        showAppNum = (TextView) findViewById(R.id.showAppNum);

        fillDate();

        //list的滚动监听
        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**滚动的时候调用的方法
             *firstVisibleItem 第一个可见条目
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                dismissPopupWindow();
                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > userAppInfos.size()) {
                        showAppNum.setText("系统软件：" + systemAppInfos.size() + "个");
                    } else {
                        showAppNum.setText("用户软件：" + userAppInfos.size() + "个");
                    }
                }
            }
        });


        //list的条目监听
        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){
                    return;
                }

                else if (position == (userAppInfos.size()+1)){
                    return;
                }
                else if (position <= userAppInfos.size()){
                    //用户程序
                    int newposition = position - 1;
                    appInfo = userAppInfos.get(newposition);
                    L.i("我是用户程序："+newposition);
                }
                else{
                    //系统程序
                    int newposition = position -2 - userAppInfos.size();
                    appInfo = systemAppInfos.get(newposition);
                    L.i("我是系统程序："+newposition);
                }
                //关闭之前的popupwindow
                dismissPopupWindow();
                View contentview = View.inflate(getApplicationContext(),R.layout.pop_app_item,null);

                /**
                 * 功能的实现
                 */
                ll_start = (LinearLayout) contentview.findViewById(R.id.ll_start);
                ll_share = (LinearLayout) contentview.findViewById(R.id.ll_share);
                ll_uninstall = (LinearLayout) contentview.findViewById(R.id.ll_uninstall);


                ll_start.setOnClickListener(AppManagerActivity.this);
                ll_share.setOnClickListener(AppManagerActivity.this);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);

                popupWindow = new PopupWindow(contentview,-2,-2);

                //设置背景为透明,使得弹窗动画效果可以显示
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                int[] location = new int[2];
                view.getLocationInWindow(location);
                int dp = 100;
                int px = DensityUtil.dip2px(getApplicationContext(),dp);
                popupWindow.showAtLocation(parent, Gravity.LEFT|Gravity.TOP,px,location[1]-30);

                ScaleAnimation sa = new ScaleAnimation(0.3f,1.0f,0.3f,1.0f, Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0.5f);
                sa.setDuration(500);
                AlphaAnimation al = new AlphaAnimation(0.5f,1.0f);
                al.setDuration(500);
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(sa);
                set.addAnimation(al);

                contentview.setAnimation(set);
            }
        });

        //长按item监听，设置密码锁
        lv_app_manager.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    return true;
                }

                else if (position == (userAppInfos.size()+1)){
                    return true;
                }
                else if (position <= userAppInfos.size()){
                    //用户程序
                    int newposition = position - 1;
                    appInfo = userAppInfos.get(newposition);
                    L.i("我是用户程序："+newposition);
                }
                else{
                    //系统程序
                    int newposition = position -2 - userAppInfos.size();
                    appInfo = systemAppInfos.get(newposition);
                    L.i("我是系统程序："+newposition);
                }

                L.i("是否上锁："+applockDao.find(appInfo.getPackname()));
                if (applockDao.find(appInfo.getPackname())){
                    //说明已经上锁
                    applockDao.delete(appInfo.getPackname());

                    adapter.notifyDataSetChanged();
                }else {
                    //上锁
                    applockDao.add(appInfo.getPackname());
                    adapter.notifyDataSetChanged();
                }

                return true;
            }
        });
    }

    private void fillDate() {
        ll_loading.setVisibility(View.VISIBLE);

        new Thread(){
            @Override
            public void run() {
                //返回的是一个list表，包含app的名称，icon
                appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);

                //区分系统软件还是用户软件
                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo : appInfos){
                    if (appInfo.isUserApp()){
                        //用户软件信息集合
                        userAppInfos.add(appInfo);
                    }else {
                        //系统软件信息集合
                        systemAppInfos.add(appInfo);
                    }
                }

                //加载适配器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        applockDao = new ApplockDao(AppManagerActivity.this);
                        if (adapter == null) {
                            adapter = new AppManagerAdapter(appInfos, AppManagerActivity.this, userAppInfos, systemAppInfos,applockDao);
                            lv_app_manager.setAdapter(adapter);
                        }else {
                            adapter.notifyDataSetChanged();
                        }
                        ll_loading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();
    }

    /**
     * 获取某个目录的可用空间

     * @param path
     * @return
     */
    private long getAvailSpace(String path){
        StatFs statFs = new StatFs(path);
        statFs.getBlockCount();//获取分区的个数
        int size = statFs.getBlockSize();//获取分区的大小
        int count = statFs.getAvailableBlocks();//获取可用的区块的个数
        return size*count;
    }

    /**
     * 监听功能的实现
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_start:
                L.i("启动："+appInfo.getName());
                startApp();
                break;
            case R.id.ll_share:
                L.i("分享："+appInfo.getName());
                 shareApp();
                break;
            case R.id.ll_uninstall:
                if (appInfo.isUserApp()){
                L.i("卸载："+appInfo.getName());
                uninstallApp();
                }
                else {
                    Toast.makeText(this, "系统软件，无法卸载！！！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 分享app
     */
    private void shareApp() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件,名称叫："+appInfo.getName());
        startActivity(intent);
        dismissPopupWindow();
    }

    /**
     * 卸载一个app
     */
    private void uninstallApp() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + appInfo.getPackname()));
        startActivityForResult(intent, 0);
        dismissPopupWindow();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //刷新界面
        fillDate();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 开启一个应用程序
     */
    private void startApp() {
        //查询这个应用程序的入口activity
        PackageManager pm = getPackageManager();
//        Intent intent = new Intent();
//
//        intent.setAction("android.intent.action.MAIN");
//        intent.addCategory("android.intent.category.LAUNCHER");
//
//        //查询手机上面所有的有启动项的程序
//        List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);

        Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
        if (intent != null){
            startActivity(intent);
            dismissPopupWindow();
        }else {
            Toast.makeText(this, "系统软件，无法打开", Toast.LENGTH_SHORT).show();
        }

    }
}
