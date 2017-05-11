package com.example.lee.gpsdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private TextView loTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loTextView = (TextView) findViewById(R.id.tv_get_location);
        callPhone(loTextView);
//        chackLocation();
    }

    public void callPhone(View v) {
        MPermissionUtils.requestPermissionsResult(this, 1, new String[]{Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_FINE_LOCATION}
                , new MPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Toast.makeText(MainActivity.this, "授权成功,执行拨打电话操作!", Toast.LENGTH_SHORT).show();
                        chackLocation();
                    }

                    @Override
                    public void onPermissionDenied() {
                        MPermissionUtils.showTipsDialog(MainActivity.this);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void chackLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //接着需要选择一个位置提供器来确定设备当前的位置，一个有三种方式。
        //获取有哪些位置提供器可用
        List<String> providers = locationManager.getProviders(true);//传入 true 就表示只有启用的位置提供器才会被返回。
        System.out.println(providers);//输出开启了哪些位置提供器
        String provider = null;
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_LONG).show();
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);//通过provider获取当前位置
        if (location != null) {
            showLocation(location);
        }
        //监听器：监听位置的变化
        //每隔5秒检测一次移动距离，当移动距离大于1米就调用监听器的onLocationChanged方法
        locationManager.requestLocationUpdates(provider, 5000, 1, listener);
    }

    //显示经纬度的函数
    private void showLocation(Location location) {
        String currentPosition = "纬度：" + location.getLatitude() + "\n" + "经度：" + location.getLongitude();//获取经纬度
        loTextView.setText(currentPosition);//为文本视图设置当前的位置
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(listener);//关闭程序时将监听器移除
        }
    }

    LocationListener listener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            System.out.println("状态改变");
        }

        @Override
        public void onProviderEnabled(String provider) {
            System.out.println("提供器启动");
        }

        @Override
        public void onProviderDisabled(String provider) {
            System.out.println("提供器关闭");
        }

        @Override
        public void onLocationChanged(Location location) {
            System.out.println("位置改变");
            showLocation(location);
        }
    };
}
