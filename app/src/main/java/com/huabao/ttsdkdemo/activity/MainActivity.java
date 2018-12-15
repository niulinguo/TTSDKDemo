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

    // 展示 TTService 当前状态
    private TextView mTTServiceStateTextView;
    // 展示 TicTag 当前状态
    private TextView mTicTagStateTextView;
    // 事件监听
    private final TicEventImpl mTicEvent = new TicEventImpl() {

        @Override
        public void onServiceBond() {
            // TTService 已绑定
            super.onServiceBond();
            mTTServiceStateTextView.setText(R.string.state_ttservice_running);
            initBondTicState();
        }

        @Override
        public void onServiceUnbound() {
            // TTService 断开连接
            super.onServiceUnbound();
            mTTServiceStateTextView.setText(R.string.state_ttservice_loss);
            mTicTagStateTextView.setText("--");
        }

        @Override
        public void onBondTicResult(int code) {
            // TicTag 绑定结果
            super.onBondTicResult(code);
            if (code == -1) {
                // TicTag 设备绑定成功
                initBondTicState();
            } else {
                final String message;
                if (code == 2) {
                    message = "TicTag尚未绑定，请长按侧边按钮五秒";
                } else {
                    message = "蓝牙连接失败";
                }
                mTicTagStateTextView.setText(String.format("bond tic failure %s", message));
            }
        }

        @Override
        public void onBondTicRemoved() {
            // TicTag 被移除
            super.onBondTicRemoved();
            initBondTicState();
        }
    };

    /**
     * 更新 TicTag 设备绑定状态
     */
    private void initBondTicState() {
        final TicDevice ticDevice = TicManager.api().getBondTic();
        if (ticDevice == null) {
            mTicTagStateTextView.setText(R.string.state_no_tic_bond);
        } else {
            mTicTagStateTextView.setText(String.format("bond device %s", ticDevice.getName()));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTTServiceStateTextView = findViewById(R.id.tv_ttservice_state);
        mTTServiceStateTextView.setText(R.string.state_ttservice_starting);

        mTicTagStateTextView = findViewById(R.id.tv_tictag_state);

        // 添加事件监听
        TicManager.addTicListener(mTicEvent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 注销事件监听
        TicManager.removeTicListener(mTicEvent);
    }

    /**
     * 搜索、绑定 TicTag 设备
     */
    public void onSearchDeviceButtonClicked(View view) {
        if (!hasBondTicDevice()) {
            // 打开蓝牙搜索页面
            final Intent intent = new Intent(this, SearchTicTagDeviceActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请先移除绑定的 TicTag 设备", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 查看 TicTag 设备信息
     */
    public void onDeviceDetailButtonClicked(View view) {
        if (hasBondTicDevice()) {
            // 打开 TicTag 设备详情页面
            final Intent intent = new Intent(this, TicTagDetailActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "没有绑定 TicTag 设备", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * TicTag 设备解除绑定
     */
    public void onRemoveDeviceButtonClicked(View view) {
        if (hasBondTicDevice()) {
            new AlertDialog.Builder(this)
                    .setMessage("是否移除绑定的 TicTag 设备？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 移除绑定的 TicTag 设备
                            TicManager.api().removeBondTic();
                            Toast.makeText(MainActivity.this, "移除成功", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } else {
            Toast.makeText(this, "没有绑定 TicTag 设备", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否绑定了 TicTag 设备
     */
    private boolean hasBondTicDevice() {
        if (TicManager.isServiceBond()) {
            final TicDevice ticDevice = TicManager.api().getBondTic();
            return ticDevice != null;
        }
        return false;
    }
}
