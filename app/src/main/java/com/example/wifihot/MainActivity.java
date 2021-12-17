package com.example.wifihot;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.sdk.wifihot.WIfiSDK;
import com.sdk.wifihot.sdk.RequestData;
import com.sdk.wifihot.sdk.WIfiHot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            XXPermissions.with(MainActivity.this)
                    .permission(Permission.ACCESS_COARSE_LOCATION,Permission.ACCESS_FINE_LOCATION)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all ) {
                                Log.e("TAG","onGranted");
                                req();
                            }
                        }
                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            if (never) {
                                Log.e("TAG","onDenied1");
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                            } else {
                                Log.e("TAG","onDenied");
                            }
                        }
                    });
        }else{
            req();
        }


    }
    private void req(){
//        WIfiSDK.instance(this,"hsl-ap3-001","12345678","20F989A74F7FCE58D284519D677A0610","AOC",
//                "6.1.21111804","HABCDEFG123456","1637638088683","FBL01.02");
        WIfiHot.init(this);
        WIfiHot.start(new RequestData.Builder()
                        .ssid("hsl-ap3-001")
                        .password("12345678")
                        .build());
    }
}
