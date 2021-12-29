package com.truescend.gofit.views;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;

import com.j256.ormlite.stmt.query.In;
import com.truescend.gofit.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

/**
 * Created by Admin
 * Date 2021/12/28
 */
public class ShowLocalPermissActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int OPEN_LOCATION_CODE = 1001;

    private ConstraintLayout permissionBleLayout;
    private ConstraintLayout permissionGPSLayout;
    private ConstraintLayout permissionLocalLayout;
    private ConstraintLayout permissionCameraLayout;
    private ConstraintLayout permissionReadWriteLayout;
    private ConstraintLayout permissionPhoneStatusLayout;
    private ConstraintLayout permissionContactLayout;

    private BluetoothAdapter bluetoothAdapter;

    private ImageView permissBleStatusImg,permissGPSStatusImg,permissLocaStatusImg,
            permissCameraStatusImg,permissReadWriteStatusImg,permissPhoneImg,permissContactStatusImg;

    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            getPermissStatus();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_permission_layout);

        initViews();
        getPermissStatus();

        findViewById(R.id.showPermissionBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(0x00,intent);
                finish();
            }
        });

    }

    private void initViews() {
        permissionBleLayout = findViewById(R.id.permissionBleLayout);
        permissionGPSLayout = findViewById(R.id.permissionGPSLayout);
        permissionLocalLayout = findViewById(R.id.permissionLocalLayout);
        permissionCameraLayout = findViewById(R.id.permissionCameraLayout);
        permissionReadWriteLayout = findViewById(R.id.permissionReadWriteLayout);
        permissionPhoneStatusLayout = findViewById(R.id.permissionPhoneStatusLayout);
        permissionContactLayout = findViewById(R.id.permissionContactLayout);

        permissBleStatusImg = findViewById(R.id.permissBleStatusImg);
        permissGPSStatusImg = findViewById(R.id.permissGPSStatusImg);
        permissLocaStatusImg = findViewById(R.id.permissLocaStatusImg);
        permissCameraStatusImg = findViewById(R.id.permissCameraStatusImg);
        permissReadWriteStatusImg = findViewById(R.id.permissReadWriteStatusImg);
        permissPhoneImg = findViewById(R.id.permissPhoneImg);
        permissContactStatusImg = findViewById(R.id.permissContactStatusImg);




        permissionBleLayout.setOnClickListener(this);
        permissionGPSLayout.setOnClickListener(this);
        permissionLocalLayout.setOnClickListener(this);
        permissionCameraLayout.setOnClickListener(this);
        permissionReadWriteLayout.setOnClickListener(this);
        permissionPhoneStatusLayout.setOnClickListener(this);
        permissionContactLayout.setOnClickListener(this);


    }




    //获取权限状态
    private void getPermissStatus() {
        //是否打开了蓝牙
        permissBleStatusImg.setImageResource(isOpenBle() ? R.drawable.ic_permission_done : R.drawable.ic_item_menu_click);
        //是否打开了GPS
        boolean isOpenGPS = openGPSLocal(ShowLocalPermissActivity.this);
        permissGPSStatusImg.setImageResource(isOpenGPS ? R.drawable.ic_permission_done : R.drawable.ic_item_menu_click);

        //是否打开了位置权限
        boolean isLocalPermiss = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        permissLocaStatusImg.setImageResource(isLocalPermiss ?  R.drawable.ic_permission_done : R.drawable.ic_item_menu_click );
        //是否打开了相机权限
        boolean isOpenCamera = ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        permissCameraStatusImg.setImageResource(isOpenCamera ? R.drawable.ic_permission_done : R.drawable.ic_item_menu_click );
        //是否打开了文件读写权限
        boolean isOpenWrite = ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        permissReadWriteStatusImg.setImageResource(isOpenWrite ? R.drawable.ic_permission_done : R.drawable.ic_item_menu_click);
        //是否打开了电话权限
        boolean isOpenPhone =ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        permissPhoneImg.setImageResource(isOpenPhone ? R.drawable.ic_permission_done : R.drawable.ic_item_menu_click);
        //是否打开了通讯录权限
        boolean isContact = ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        permissContactStatusImg.setImageResource(isContact ? R.drawable.ic_permission_done : R.drawable.ic_item_menu_click);

    }






    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.permissionBleLayout:
                if(!isOpenBle()){
                    turnOnBlue(this,10 * 1000,0x01);
                }else{

                }
                break;
            case R.id.permissionGPSLayout:
                if(!openGPSLocal(this))
                    openGPS();
                break;
            case R.id.permissionLocalLayout:
                requestPermiss(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                break;
            case R.id.permissionCameraLayout:
                requestPermiss(new String[]{Manifest.permission.CAMERA});
                break;
            case R.id.permissionReadWriteLayout:
                requestPermiss(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE});
                break;
            case R.id.permissionPhoneStatusLayout:
                requestPermiss(new String[]{Manifest.permission.READ_PHONE_STATE});
                break;
            case R.id.permissionContactLayout:
                boolean isContact = ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
                if(isContact){
                    try {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    requestPermiss(new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.READ_CALL_LOG});
                }

                break;
        }
    }


    private void requestPermiss(String[] permiss){

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ANSWER_PHONE_CALLS},0x00);
        }
        ActivityCompat.requestPermissions(this,permiss,0x00);


    }


    private boolean isOpenBle(){
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager == null)
            return false;
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(bluetoothAdapter == null)
            return false;
        return bluetoothAdapter.isEnabled();
    }

    //打开GPS
    private void openGPS(){
        try {
            Intent intent =new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent,OPEN_LOCATION_CODE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void turnOnBlue(Activity activity, int visiableTime, int requestCode) {
        try {
            // 请求打开 Bluetooth
            Intent requestBluetoothOn = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
            requestBluetoothOn
                    .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            // 设置 Bluetooth 设备可见时间
            requestBluetoothOn.putExtra(
                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                    visiableTime);
            // 请求开启 Bluetooth
            activity.startActivityForResult(requestBluetoothOn,
                    requestCode);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //是否打开GPS开关
    public static boolean openGPSLocal(Context context){
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if(locationManager == null)
                return true;
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
