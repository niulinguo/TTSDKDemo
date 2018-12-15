package com.huabao.ttsdkdemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.huabao.ttsdkdemo.R;
import com.huabao.ttsdkdemo.TicEventImpl;

import net.useiov.nepenthes_sdk.api.TicManager;
import net.useiov.nepenthes_sdk.model.TicDevice;

public class MainActivity extends AppCompatActivity {

    private TextView mTTServiceStateTextView;
    private TextView mTicTagStateTextView;
    private final TicEventImpl mTicEvent = new TicEventImpl() {

        @Override
        public void onServiceBond() {
            super.onServiceBond();
            mTTServiceStateTextView.setText(R.string.state_ttservice_running);
            initBondTicState();
        }

        @Override
        public void onServiceUnbound() {
            super.onServiceUnbound();
            mTTServiceStateTextView.setText(R.string.state_ttservice_loss);
            mTicTagStateTextView.setText("--");
        }

        @Override
        public void onBondTicResult(int code) {
            super.onBondTicResult(code);
            if (code == -1) {
                // Device Bind Success
                initBondTicState();
            } else {
                final String message;
                if (code == 2) {
                    message = "please hold the side button for five seconds";
                } else {
                    message = "Ble Connect Failure";
                }
                mTicTagStateTextView.setText(String.format("Bind Failure: %s", message));
            }
        }

        @Override
        public void onBondTicRemoved() {
            super.onBondTicRemoved();
            initBondTicState();
        }
    };

    /**
     * Init Bind Device Info
     */
    private void initBondTicState() {
        final TicDevice ticDevice = TicManager.api().getBondTic();
        if (ticDevice == null) {
            mTicTagStateTextView.setText(R.string.state_no_tic_bond);
        } else {
            mTicTagStateTextView.setText(String.format("Device %s", ticDevice.getName()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTTServiceStateTextView = findViewById(R.id.tv_ttservice_state);
        mTTServiceStateTextView.setText(R.string.state_ttservice_starting);

        mTicTagStateTextView = findViewById(R.id.tv_tictag_state);

        // Callback Listener
        TicManager.addTicListener(mTicEvent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Remove Callback Listener
        TicManager.removeTicListener(mTicEvent);
    }

    /**
     * Search Device
     */
    public void onSearchDeviceButtonClicked(View view) {
        if (!hasBondTicDevice()) {
            // Open Search Device Page
            final Intent intent = new Intent(this, SearchTicTagDeviceActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Unbind Device First", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Device Info
     */
    public void onDeviceDetailButtonClicked(View view) {
        if (hasBondTicDevice()) {
            // 打开 TicTag 设备详情页面
            final Intent intent = new Intent(this, TicTagDetailActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Device Unbind", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Device Unbind
     */
    public void onRemoveDeviceButtonClicked(View view) {
        if (hasBondTicDevice()) {
            new AlertDialog.Builder(this)
                    .setMessage("Unbind Device?")
                    .setPositiveButton("Y", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Remove Bind Device
                            TicManager.api().removeBondTic();
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("N", null)
                    .show();
        } else {
            Toast.makeText(this, "Device Unbind", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Whether Has Bind Device
     */
    private boolean hasBondTicDevice() {
        if (TicManager.isServiceBond()) {
            final TicDevice ticDevice = TicManager.api().getBondTic();
            return ticDevice != null;
        }
        return false;
    }
}
