package com.huabao.ttsdkdemo;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import net.useiov.nepenthes_sdk.api.TicEvent;
import net.useiov.nepenthes_sdk.model.ScanRecord;

/**
 * Created by Niles
 * Date 2018/3/23
 * Email niulinguo@163.com
 */

public class TicEventImpl implements TicEvent {

    private static final String TAG = "ttsdk_demo";

    @Override
    public void onBondTicResult(int code) {
        Log.w(TAG, "TicTag bond result " + String.valueOf(code));
    }

    @Override
    public void onTicConnectStateChanged(int oldState, int newState) {
        Log.i(TAG, "TicTag connect state changed, old state " + oldState + ", new state " + newState);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i(TAG, "location changed, lat is " + location.getLatitude() + ", long is " + location.getLongitude());
    }

    @Override
    public void onLocationState(boolean open) {
        Log.w(TAG, "location state " + String.valueOf(open));
    }

    @Override
    public void onUserAlertEvent(int count, boolean isBeacon) {
        Log.e(TAG, "user alert count " + count + ", isBeacon is " + String.valueOf(isBeacon));
    }

    @Override
    public void onScanBeacon(@NonNull ScanRecord scanRecord) {
        Log.i(TAG, "find beacon info, power is " + scanRecord.getPower());
    }

    @Override
    public void onBondTicRemoved() {
        Log.e(TAG, "TicTag Unbind");
    }

    @Override
    public void onTicVersionChanged(@NonNull String hardwareVersion, @NonNull String firmwareVersion) {
        Log.e(TAG, "TicTag upgraded, hardware version is " + hardwareVersion + ", firmware version is " + firmwareVersion);
    }

    @Override
    public void onEvent(@NonNull Bundle bundle) {
        Log.w(TAG, "onEvent " + bundle.getInt("type"));
    }

    @Override
    public void onServiceBond() {
        Log.w(TAG, "TT Service Connected");
    }

    @Override
    public void onServiceUnbound() {
        Log.w(TAG, "TT Service Disconnected");
    }
}
