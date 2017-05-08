package cn.buildworld.com.mobilesafe.activity.callSmsSafe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;
import cn.buildworld.com.mobilesafe.adapter.CallSmsSafeAdapter;
import cn.buildworld.com.mobilesafe.bean.BlackNumInfo;
import cn.buildworld.com.mobilesafe.dao.BlackNumDao;

public class CallSmsSafeActivity extends AppCompatActivity {

    private ListView list_black_num;
    private CallSmsSafeAdapter adapter;
    private List<BlackNumInfo> allNum;
    private BlackNumDao numDao;

    private EditText et_add_number;
    private CheckBox call_checkBox;
    private CheckBox msg_checkBox;
    private Button cancel;
    private Button ok;
    private LinearLayout layout;
    private int offset = 0;
    private int maxNumber = 10;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.call_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        else if (item.getItemId() == R.id.add_call){
            addNumber();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);


        Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);
        toolbar.setTitle("黑名单拦截");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        list_black_num = (ListView) findViewById(R.id.list_black_num);
        layout = (LinearLayout) findViewById(R.id.loading);

        numDao = new BlackNumDao(this);

        fillDate();

        /**
         * listview的滚动事件监听器
         */
        list_black_num.setOnScrollListener(new AbsListView.OnScrollListener() {
            //当滚动状态发生变化时
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState){
                case  SCROLL_STATE_IDLE://空闲状态
                    L.i("空闲状态");
                    //判断当前listview滚动的位置
                    int lastVisiblePosition = list_black_num.getLastVisiblePosition();
                    if (lastVisiblePosition == (allNum.size() -1 )){
                        L.i("滑动到最后的位置！！！");
                        offset += maxNumber;
                        L.i("加载的数据长度："+numDao.findPart(offset,maxNumber).size());
                        if (numDao.findPart(offset,maxNumber).size() == 0){
                            Toast.makeText(CallSmsSafeActivity.this, "数据已经全部加载完毕！！！", Toast.LENGTH_SHORT).show();
                        }else {
                            fillDate();
                        }
                    }
                    break;
                case SCROLL_STATE_TOUCH_SCROLL://手指触摸滚动
                    break;
                case SCROLL_STATE_FLING://惯性滑行状态
                    break;
            }
            }

            //滚动的时候调用的方法
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

//        L.i("所有的号码信息"+allNum.toString());


    }

    private void fillDate() {
            layout.setVisibility(View.VISIBLE);

        new Thread(){
            @Override
            public void run() {
                if (allNum == null){
                    allNum = numDao.findPart(offset,maxNumber);
                }else {//已经加载过了数据
                    allNum.addAll(numDao.findPart(offset,maxNumber));
                }
                //查询所有的号码，放到list集合中去
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout.setVisibility(View.INVISIBLE);

                        if (adapter == null) {
                            adapter = new CallSmsSafeAdapter(CallSmsSafeActivity.this, allNum);
                            list_black_num.setAdapter(adapter);
                        }else {
                        adapter.notifyDataSetChanged();
                        }

                        adapter.setOnImageListener(new CallSmsSafeAdapter.CallBack() {
                            @Override
                            public void OnImageListener(View v, final int position) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
                                builder.setTitle("警告");
                                builder.setMessage("确认要删除此黑名单吗？");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        /**
                                         * 删除电话号码功能
                                         */
                                        BlackNumInfo info = allNum.get(position);
                                        String delete_num = info.getNum();
                                        L.i("要删除的号码："+delete_num);
                                        numDao.delete(delete_num);
                                        allNum.remove(position);
                                        adapter.notifyDataSetChanged();

                                    }
                                });

                                builder.setNegativeButton("取消",null);
                                builder.show();

                            }
                        });
                    }
                });

            }
        }.start();
    }


    /**
     * 添加拦截号码
     */
    public void addNumber(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View contentView = View.inflate(this, R.layout.dialog_add_black_num, null);
        et_add_number = (EditText) contentView.findViewById(R.id.et_add_number);
        call_checkBox = (CheckBox) contentView.findViewById(R.id.call_checkBox);
        msg_checkBox = (CheckBox) contentView.findViewById(R.id.msg_checkBox);
        cancel = (Button) contentView.findViewById(R.id.cancel);
        ok = (Button) contentView.findViewById(R.id.ok);
        dialog.setView(contentView, 0, 0, 0, 0);
        dialog.show();


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blacknumber = et_add_number.getText().toString().trim();
                if (TextUtils.isEmpty(blacknumber)) {
                    Toast.makeText(getApplicationContext(), "黑名单号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode;
                if (call_checkBox.isChecked() && msg_checkBox.isChecked()) {
                    //全部拦截
                    mode = "3";
                } else if (call_checkBox.isChecked()) {
                    //电话拦截
                    mode = "1";
                } else if (msg_checkBox.isChecked()) {
                    //短信拦截
                    mode = "2";
                } else {
                    Toast.makeText(getApplicationContext(), "请选择拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }

                //数据被加到数据库
                numDao.addBlackNum(blacknumber, mode);
                //更新listview集合里面的内容。
                BlackNumInfo info = new BlackNumInfo();
                info.setMode(mode);
                info.setNum(blacknumber);
                allNum.add(0, info);
                //通知listview数据适配器数据更新了。
                adapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });
    }

}
