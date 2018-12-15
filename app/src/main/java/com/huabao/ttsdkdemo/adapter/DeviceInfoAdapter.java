package com.huabao.ttsdkdemo.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huabao.ttsdkdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Niles
 * Date 2018/3/23
 * Email niulinguo@163.com
 */

public class DeviceInfoAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<BluetoothDevice> mBluetoothDevices = new ArrayList<>();

    public DeviceInfoAdapter(Context context) {
        mContext = context;
    }

    public BluetoothDevice getBluetoothDevice(int position) {
        return mBluetoothDevices.get(position);
    }

    public boolean addWithCheck(@NonNull BluetoothDevice bluetoothDevice) {
        for (BluetoothDevice device : mBluetoothDevices) {
            // exclude duplicate ble device
            if (device.equals(bluetoothDevice)) {
                return false;
            }
        }
        mBluetoothDevices.add(bluetoothDevice);
        return true;
    }

    @Override
    public int getCount() {
        return mBluetoothDevices.size();
    }

    @Override
    public Object getItem(int position) {
        return mBluetoothDevices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final View itemView;
        if (convertView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_device_info_layout, parent, false);
        } else {
            itemView = convertView;
        }

        final ViewHolder viewHolder;
        if (itemView.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.nameView = itemView.findViewById(R.id.tv_device_name);
            viewHolder.addressView = itemView.findViewById(R.id.tv_device_address);
            itemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) itemView.getTag();
        }

        final BluetoothDevice bluetoothDevice = mBluetoothDevices.get(position);
        viewHolder.nameView.setText(bluetoothDevice.getName());
        viewHolder.addressView.setText(bluetoothDevice.getAddress());

        return itemView;
    }

    private static final class ViewHolder {
        TextView nameView;
        TextView addressView;
    }
}
