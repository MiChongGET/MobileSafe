package cn.buildworld.com.mobilesafe.activity.fangdao;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import cn.buildworld.com.mobilesafe.R;
import cn.buildworld.com.mobilesafe.Utils.L;

public class GpsActivity extends AppCompatActivity {

    private LocationManager lm;
    private MyLocation myLocation;
    private TextView gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.total_toolbar);
        toolbar.setTitle("GPS定位");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        gps = (TextView) findViewById(R.id.gps);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

//        List<String> provider = lm.getAllProviders();
//        for (String s :provider){
//            L.i(s);
//        }

        myLocation = new MyLocation();
        //注册位置监听服务
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            lm.requestLocationUpdates("gps", 0, 0, myLocation);
            return;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(myLocation);
        myLocation = null;
    }


    class MyLocation implements LocationListener{


        //当前位置信息
        @Override
        public void onLocationChanged(Location location) {

            String longitude = "经度："+location.getLongitude();
            String latitude = "纬度："+location.getLatitude();
            String accuracy ="精确度："+location.getAccuracy();

            gps.setText(longitude+"\n"+latitude+"\n"+accuracy);


            L.i(longitude+latitude+accuracy);
        }

        //当状态发生改变的时候回调 开启--关闭
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        //某一个位置提供者可以使用了
        @Override
        public void onProviderEnabled(String provider) {

        }

        //某一个位置提供者不可以使用了
        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
