package com.huabao.ttsdkdemo.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.TimeUtils;
import com.huabao.ttsdk.api.TicConnectionState;
import com.huabao.ttsdk.api.TicManager;
import com.huabao.ttsdk.model.ScanRecord;
import com.huabao.ttsdk.model.TicDevice;
import com.huabao.ttsdkdemo.R;
import com.huabao.ttsdkdemo.TicEventImpl;

/**
 * Created by Negro
 * Date 2018/3/23
 * Email niulinguo@163.com
 */

public class TicTagDetailActivity extends AppCompatActivity {

    private TextView mTicTagStateTextView;
    private TextView mTicTagNameTextView;
    private TextView mTicTagAddressTextView;
    private TextView mTicTagHwVersionTextView;
    private TextView mTicTagFwVersionTextView;
    private TextView mTicTagAsyncTimeTextView;
    private TextView mTicTagPowerTextView;
    private TextView mUserAlertTextView;
    private TextView mLocationLongTextView;
    private TextView mLocationLatTextView;
    // 事件监听
    private final TicEventImpl mTicEvent = new TicEventImpl() {

        @Override
        public void onBondTicRemoved() {
            // TicTag 设备被移除
            super.onBondTicRemoved();
            Toast.makeText(TicTagDetailActivity.this, "TicTag 设备已移除", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onServiceUnbind() {
            // TTService 服务断开连接
            super.onServiceUnbind();
            Toast.makeText(TicTagDetailActivity.this, "TTService 已断开连接", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onUserAlertEvent(int count, boolean isBeacon) {
            // 监听到用户警报事件
            super.onUserAlertEvent(count, isBeacon);
            initUserAlert(count);
        }

        @Override
        public void onTicVersionChanged(@NonNull String hardwareVersion, @NonNull String firmwareVersion) {
            // TicTag 设备升级
            super.onTicVersionChanged(hardwareVersion, firmwareVersion);
            initTicTagVersionInfo(hardwareVersion, firmwareVersion);
        }

        @Override
        public void onLocationState(boolean open) {
            // 定位状态改变回调
            super.onLocationState(open);
            if (open) {
                initTicTagState();
            } else {
                clearInfo();
            }
        }

        @Override
        public void onScanBeacon(@NonNull ScanRecord scanRecord) {
            // 监听到 Beacon 信息
            super.onScanBeacon(scanRecord);
            initTicTagPowerInfo(scanRecord.getPower());
        }

        @Override
        public void onTicConnectStateChanged(int oldState, int newState) {
            // TicTag 连接蓝牙状态改变
            super.onTicConnectStateChanged(oldState, newState);
            initTicTagState();
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            // 监听到 Location 信息变化
            super.onLocationChanged(location);
            initLocationInfo(location);
        }
    };

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tag_detail);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 判断 TTService 是否已连接
        if (!TicManager.isServiceBond()) {
            Toast.makeText(this, "TTService 未连接", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 判断 TicTag 设备是否已绑定
        final TicDevice ticDevice = TicManager.api().getBondTic();
        if (ticDevice == null) {
            Toast.makeText(this, "未绑定 TicTag 设备", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mTicTagStateTextView = findViewById(R.id.tv_tictag_state);
        mTicTagNameTextView = findViewById(R.id.tv_tictag_name);
        mTicTagAddressTextView = findViewById(R.id.tv_tictag_address);
        mTicTagHwVersionTextView = findViewById(R.id.tv_tictag_hw_version);
        mTicTagFwVersionTextView = findViewById(R.id.tv_tictag_fw_version);
        mTicTagAsyncTimeTextView = findViewById(R.id.tv_tictag_async_time);
        mTicTagPowerTextView = findViewById(R.id.tv_tictag_power);
        mUserAlertTextView = findViewById(R.id.tv_alert_count);
        mLocationLongTextView = findViewById(R.id.tv_location_long);
        mLocationLatTextView = findViewById(R.id.tv_location_lat);

        // 添加事件监听
        TicManager.addTicListener(mTicEvent);

        initTicTagDeviceInfo(ticDevice);
        initTicTagState();
    }

    /**
     * 初始化 TicTag 设备信息
     */
    private void initTicTagDeviceInfo(@NonNull TicDevice ticDevice) {
        // TicTag 设备 名字
        final String ticDeviceName = ticDevice.getName();
        // TicTag 设备 Mac 地址
        final String ticDeviceAddress = ticDevice.getAddress();
        // TicTag 设备 硬件版本
        final String ticDeviceHwVersion = ticDevice.getHwVersion();
        // TicTag 设备 固件版本
        final String ticDeviceFwVersion = ticDevice.getFwVersion();
        // TicTag 设备 最后一次连接蓝牙时间
        final long ticDeviceLastConnected = ticDevice.getLastConnected();

        mTicTagNameTextView.setText(String.format("已绑定设备名字：%s", ticDeviceName));
        mTicTagAddressTextView.setText(String.format("已绑定设备地址：%s", ticDeviceAddress));
        initTicTagVersionInfo(ticDeviceHwVersion, ticDeviceFwVersion);
        mTicTagAsyncTimeTextView.setText(String.format("最后蓝牙同步时间：%s", TimeUtils.millis2String(ticDeviceLastConnected)));
    }

    /**
     * 初始化用户警报次数
     */
    private void initUserAlert(int count) {
        mUserAlertTextView.setText(String.format("用户警报次数：%s", count));
    }

    /**
     * 初始化 TicTag 版本信息
     */
    private void initTicTagVersionInfo(@NonNull String hwVersion, @NonNull String fwVersion) {
        mTicTagHwVersionTextView.setText(String.format("已绑定设备硬件版本：%s", hwVersion));
        mTicTagFwVersionTextView.setText(String.format("已绑定设备固件版本：%s", fwVersion));
    }

    /**
     * 初始化 TicTAg 电量信息
     */
    private void initTicTagPowerInfo(int power) {
        mTicTagPowerTextView.setText(String.format("设备电量：%s%%", power));
    }

    /**
     * 初始化定位信息
     */
    private void initLocationInfo(@NonNull Location location) {
        mLocationLongTextView.setText(String.format("定位信息经度：%s", location.getLongitude()));
        mLocationLatTextView.setText(String.format("定位信息纬度：%s", location.getLatitude()));
    }

    /**
     * 初始化 TicTag 当前状态
     */
    private void initTicTagState() {
        if (!TicManager.isServiceBond()) {
            Toast.makeText(this, "TTService 未连接", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final boolean locationState = TicManager.api().getLocationState();
        if (!locationState) {
            // 长时间没有连接过蓝牙并没有监听到 Beacon 信息，就会停止定位
            clearInfo();
            return;
        }

        final int ticConnectState = TicManager.api().getTicConnectState();
        switch (ticConnectState) {
            case TicConnectionState.STATE_CONNECTING: {
                mTicTagStateTextView.setText(R.string.state_tic_tag_connecting);
                break;
            }
            case TicConnectionState.STATE_CONNECTED: {
                mTicTagStateTextView.setText(R.string.state_tic_tag_connected);
                break;
            }
            case TicConnectionState.STATE_DISCONNECTED: {
                mTicTagStateTextView.setText(R.string.state_tic_tag_disconnected);
                break;
            }
        }
    }

    /**
     * 清除所有信息
     */
    private void clearInfo() {
        mTicTagStateTextView.setText(R.string.state_tic_tag_state_loss);
        mTicTagPowerTextView.setText(String.format("设备电量：%s", "--"));
        mUserAlertTextView.setText(String.format("用户警报次数：%s", 0));
        mLocationLongTextView.setText(String.format("定位信息经度：%s", "--"));
        mLocationLatTextView.setText(String.format("定位信息纬度：%s", "--"));
    }
}
