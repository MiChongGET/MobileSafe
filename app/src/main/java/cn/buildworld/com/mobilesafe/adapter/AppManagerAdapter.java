package cn.buildworld.com.mobilesafe.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.bean.AppInfo;
import cn.buildworld.com.mobilesafe.dao.ApplockDao;

/**
 * 作者：MiChong on 2017/4/29 0029 18:18
 * 邮箱：1564666023@qq.com
 */
public class AppManagerAdapter extends BaseAdapter {

    private List<AppInfo> list ;
    private Context context;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    private ApplockDao applockDao;

    public AppManagerAdapter(List<AppInfo> list, Context context, List<AppInfo> userAppInfos, List<AppInfo> systemAppInfos, ApplockDao applockDao) {
        this.list = list;
        this.context = context;
        this.userAppInfos = userAppInfos;
        this.systemAppInfos = systemAppInfos;
        this.applockDao = applockDao;
    }

    @Override
    public int getCount() {
        return userAppInfos.size()+systemAppInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppInfo appInfo ;
        //用户软件提示
        if (position == 0 ){
            TextView t = new TextView(context);
            t.setTextColor(Color.WHITE);
            t.setBackgroundColor(Color.GRAY);
            t.setText("用户软件："+userAppInfos.size()+"个");
            return  t;
        }else if (position == (userAppInfos.size()+1)){
            TextView t = new TextView(context);
            t.setTextColor(Color.WHITE);
            t.setBackgroundColor(Color.GRAY);
            t.setText("系统软件："+systemAppInfos.size()+"个");
            return  t;
        }else if (position <= userAppInfos.size()){
            int newposition = position - 1;
            appInfo = userAppInfos.get(newposition);
        }else {
            int newposition = position - 2 - userAppInfos.size();
            appInfo = systemAppInfos.get(newposition);
        }


        View view;
        ViewHolder viewHolder;


        //convertView instanceof RelativeLayout 主要是为了消除listview缓存带来的影响
        if (convertView != null && convertView instanceof RelativeLayout){

            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        else {
            viewHolder = new ViewHolder();
            view = View.inflate(context, R.layout.item_app_info,null);
            viewHolder.icon = (ImageView) view.findViewById(R.id.iv_app_icon);
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
            viewHolder.tv_location = (TextView) view.findViewById(R.id.tv_app_location);
            viewHolder.iv_status = (ImageView) view.findViewById(R.id.iv_status);

            view.setTag(viewHolder);
        }


        viewHolder.icon.setImageDrawable(appInfo.getIcon());
        viewHolder.tv_name.setText(appInfo.getName());
        if (appInfo.isInRom()){
            viewHolder.tv_location.setText("手机内存");
        }else {
            viewHolder.tv_location.setText("外部存储");
        }

        //设置上锁的图标
        if (applockDao.find(appInfo.getPackname())){
            viewHolder.iv_status.setImageResource(R.drawable.lock);
        }else viewHolder.iv_status.setImageResource(R.drawable.unlock);

        return view;
    }

        class ViewHolder{
        private ImageView icon;
        private TextView tv_name;
        private TextView tv_location;
        private ImageView iv_status;
    }
}
