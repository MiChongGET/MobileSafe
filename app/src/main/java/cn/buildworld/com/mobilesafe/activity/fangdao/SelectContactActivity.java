package cn.buildworld.com.mobilesafe.activity.fangdao;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.buildworld.com.mobilesafe.R;

public class SelectContactActivity extends AppCompatActivity {

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);
        toolbar.setTitle("选择手机联系人");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        listView = (ListView) findViewById(R.id.contact_lv);

        final List<Map<String,String>> data = getContactInfo();
        //设置联系人界面
        listView.setAdapter(new SimpleAdapter(this,data,R.layout.contact_item_view,new String[]{"name","phone"},new int[]{R.id.tv_name,R.id.tv_phone}));

        //设置监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = data.get(position).get("phone");

                Intent intent = new Intent();
                intent.putExtra("phone",phone);

                setResult(0,intent);
                finish();
            }
        });
    }

    private List<Map<String,String>> getContactInfo() {

        List<Map<String,String>> list = new ArrayList<Map<String, String>>();


        //得到一个内容解释器
        ContentResolver contentResolver = getContentResolver();

        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");

        Cursor cursor = contentResolver.query(uri, new String[]{"contact_id"}, null, null, null);

        while (cursor.moveToNext()){

            Map<String,String> map = new HashMap<String, String>();

            String cursor_id = cursor.getString(0);
            if (cursor_id != null){

                Cursor datacursor = contentResolver.query(
                                uriData,
                                new String[]{"data1","mimetype"},
                                "contact_id=?",
                                new String[]{cursor_id},
                                null);

                while (datacursor.moveToNext()){
                    String data1 = datacursor.getString(0);
                    String mimetype = datacursor.getString(1);

                    if("vnd.android.cursor.item/name".equals(mimetype)){
                        //联系人的姓名
                        map.put("name", data1);
                    }else if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
                        //联系人的电话号码
                        map.put("phone", data1);
                    }
                }
                datacursor.close();
                list.add(map);
            }

        }

        cursor.close();

        return list;
    }
}
