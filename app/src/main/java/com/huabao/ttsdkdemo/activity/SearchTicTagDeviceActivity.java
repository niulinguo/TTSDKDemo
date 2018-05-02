package com.huabao.ttsdkdemo.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.huabao.ttsdk.api.TicManager;
import com.huabao.ttsdk.model.ScanRecord;
import com.huabao.ttsdkdemo.R;
import com.huabao.ttsdkdemo.adapter.DeviceInfoAdapter;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

import timber.log.Timber;

/**
 * Created by Negro
 * Date 2018/3/23
 * Email niulinguo@163.com
 * <p>
 * 搜索蓝牙设备页面
 * 1、判断是否是 TicTag 设备方法
 * 2、请求绑定 TicTag 设备
 */

public class SearchTicTagDeviceActivity extends AppCompatActivity {

    private final static int TIC_PERMISSION_ACCESS_FINE_LOCATION = 1;

    private DeviceInfoAdapter mDeviceInfoAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            // 判断是否是 TicTag 设备
            final ScanRecord scanRecordModel = new ScanRecord();
            if (scanRecordModel.parse(scanRecord) && scanRecordModel.isTicDevice()) {
                if (mDeviceInfoAdapter.addWithCheck(device)) {
                    mDeviceInfoAdapter.notifyDataSetChanged();
                }
            }
            Timber.tag("bleSearch").i(scanRecordModel.toString());
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tic_tag_device);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (!BluetoothUtils.openBluetooth()) {
            Toast.makeText(this, "请打开蓝牙", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final ListView deviceListView = findViewById(R.id.ll_device_list);
        mDeviceInfoAdapter = new DeviceInfoAdapter(this);
        deviceListView.setAdapter(mDeviceInfoAdapter);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemDeviceClicked(mDeviceInfoAdapter.getBluetoothDevice(position));
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkLocationPermission()) {
            scanBluetoothDevice();
        }
    }

    private void scanBluetoothDevice() {
        BluetoothUtils.getBluetoothAdapter().startLeScan(mLeScanCallback);
    }

    private void stopBluetoothScan() {
        BluetoothUtils.getBluetoothAdapter().stopLeScan(mLeScanCallback);
    }

    private void onItemDeviceClicked(BluetoothDevice bluetoothDevice) {
        stopBluetoothScan();
        if (TicManager.isServiceBond()) {
            TicManager.api().requestBondTic(bluetoothDevice.getName(), bluetoothDevice.getAddress());
        } else {
            Toast.makeText(this, "TTService 已断开", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @RequiresApi(api = 23)
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, TIC_PERMISSION_ACCESS_FINE_LOCATION);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == TIC_PERMISSION_ACCESS_FINE_LOCATION) {
            final boolean success = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (success) {
                scanBluetoothDevice();
            } else {
                Toast.makeText(this, "无法获取权限", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
