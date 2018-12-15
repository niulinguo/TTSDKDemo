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
import com.huabao.ttsdkdemo.R;
import com.huabao.ttsdkdemo.TicEventImpl;

import net.useiov.nepenthes_sdk.api.TicConnectionState;
import net.useiov.nepenthes_sdk.api.TicManager;
import net.useiov.nepenthes_sdk.model.ScanRecord;
import net.useiov.nepenthes_sdk.model.TicDevice;

/**
 * Created by Niles
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
    private final TicEventImpl mTicEvent = new TicEventImpl() {

        @Override
        public void onBondTicRemoved() {
            super.onBondTicRemoved();
            Toast.makeText(TicTagDetailActivity.this, "Device Removed", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onServiceUnbound() {
            super.onServiceUnbound();
            Toast.makeText(TicTagDetailActivity.this, "TTService Unconnected", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onUserAlertEvent(int count, boolean isBeacon) {
            super.onUserAlertEvent(count, isBeacon);
            initUserAlert(count);
        }

        @Override
        public void onTicVersionChanged(@NonNull String hardwareVersion, @NonNull String firmwareVersion) {
            super.onTicVersionChanged(hardwareVersion, firmwareVersion);
            initTicTagVersionInfo(hardwareVersion, firmwareVersion);
        }

        @Override
        public void onLocationState(boolean open) {
            super.onLocationState(open);
            if (open) {
                initTicTagState();
            } else {
                clearInfo();
            }
        }

        @Override
        public void onScanBeacon(@NonNull ScanRecord scanRecord) {
            super.onScanBeacon(scanRecord);
            initTicTagPowerInfo(scanRecord.getPower());
        }

        @Override
        public void onTicConnectStateChanged(int oldState, int newState) {
            super.onTicConnectStateChanged(oldState, newState);
            initTicTagState();
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
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

        if (!TicManager.isServiceBond()) {
            Toast.makeText(this, "TTService unconnected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final TicDevice ticDevice = TicManager.api().getBondTic();
        if (ticDevice == null) {
            Toast.makeText(this, "Device unconnected", Toast.LENGTH_SHORT).show();
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

        // callback listener
        TicManager.addTicListener(mTicEvent);

        initTicTagDeviceInfo(ticDevice);
        initTicTagState();
    }

    /**
     * init device info
     */
    private void initTicTagDeviceInfo(@NonNull TicDevice ticDevice) {
        // device name
        final String ticDeviceName = ticDevice.getName();
        // device MAC address
        final String ticDeviceAddress = ticDevice.getAddress();
        // device hardware version
        final String ticDeviceHwVersion = ticDevice.getHwVersion();
        // device firmware version
        final String ticDeviceFwVersion = ticDevice.getFwVersion();
        // device last connect time
        final long ticDeviceLastConnected = ticDevice.getLastConnected();

        mTicTagNameTextView.setText(String.format("device name：%s", ticDeviceName));
        mTicTagAddressTextView.setText(String.format("device mac：%s", ticDeviceAddress));
        initTicTagVersionInfo(ticDeviceHwVersion, ticDeviceFwVersion);
        mTicTagAsyncTimeTextView.setText(String.format("sync time：%s", TimeUtils.millis2String(ticDeviceLastConnected)));
    }

    /**
     * init alert
     */
    private void initUserAlert(int count) {
        mUserAlertTextView.setText(String.format("alert：%s", count));
    }

    /**
     * init version info
     */
    private void initTicTagVersionInfo(@NonNull String hwVersion, @NonNull String fwVersion) {
        mTicTagHwVersionTextView.setText(String.format("hardware version：%s", hwVersion));
        mTicTagFwVersionTextView.setText(String.format("firmware version：%s", fwVersion));
    }

    /**
     * init power info
     */
    private void initTicTagPowerInfo(int power) {
        mTicTagPowerTextView.setText(String.format("power：%s%%", power));
    }

    /**
     * init location info
     */
    private void initLocationInfo(@NonNull Location location) {
        mLocationLongTextView.setText(String.format("longitude：%s", location.getLongitude()));
        mLocationLatTextView.setText(String.format("latitude：%s", location.getLatitude()));
    }

    /**
     * init current device status
     */
    private void initTicTagState() {
        if (!TicManager.isServiceBond()) {
            Toast.makeText(this, "TTService unconnected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final boolean locationState = TicManager.api().getLocationState();
        if (!locationState) {
            // location stop
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
     * reset show info
     */
    private void clearInfo() {
        mTicTagStateTextView.setText(R.string.state_tic_tag_state_loss);
        mTicTagPowerTextView.setText(String.format("power：%s", "--"));
        mUserAlertTextView.setText(String.format("alert：%s", 0));
        mLocationLongTextView.setText(String.format("longitude：%s", "--"));
        mLocationLatTextView.setText(String.format("latitude：%s", "--"));
    }
}
