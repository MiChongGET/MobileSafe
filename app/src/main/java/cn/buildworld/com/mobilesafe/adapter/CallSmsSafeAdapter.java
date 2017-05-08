package cn.buildworld.com.mobilesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.bean.BlackNumInfo;

/**
 * 作者：MiChong on 2017/4/22 0022 15:06
 * 邮箱：1564666023@qq.com
 */
public class CallSmsSafeAdapter extends BaseAdapter{

    private Context context;
    private List<BlackNumInfo> list;

    public CallSmsSafeAdapter(Context context, List<BlackNumInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        BlackNumInfo  numDao = list.get(position);

        ViewHolder viewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_call_sms_safe,null);
            viewHolder = new ViewHolder();

            viewHolder.call_num = (TextView) convertView.findViewById(R.id.call_num);
            viewHolder.mode = (TextView) convertView.findViewById(R.id.mode);
            viewHolder.isv_delete = (ImageView) convertView.findViewById(R.id.isv_delete);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.call_num.setText("拦截号码："+numDao.getNum());

        String mode = numDao.getMode();
        if("1".equals(mode)){
            viewHolder.mode.setText("电话拦截");
        }else if("2".equals(mode)){
            viewHolder.mode.setText("短信拦截");
        }else{
            viewHolder.mode.setText("全部拦截");
        }


        //删除号码监听
        viewHolder.isv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.OnImageListener(v,position);
            }
        });

        return convertView;
    }

    private class ViewHolder{
        private TextView call_num;
        private TextView mode;
        private ImageView isv_delete;
    }


    /**
     * 删除功能的方法回调
     */

    CallBack callBack;
    public void setOnImageListener(CallBack callBack){
        this.callBack = callBack;
    }

    public interface CallBack{
        void OnImageListener(View v,int position);
    }
}
