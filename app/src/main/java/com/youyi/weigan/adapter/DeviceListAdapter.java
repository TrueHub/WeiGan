package com.youyi.weigan.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youyi.weigan.R;
import com.youyi.weigan.eventbean.Comm2GATT;
import com.youyi.weigan.eventbean.Event_BleDevice;
import com.youyi.weigan.utils.EventUtil;

import java.util.ArrayList;

/**
 * Created by user on 2017/6/1.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.LocalViewHolder> {

    private ArrayList<BluetoothDevice> list ;
    private Context context;

    public DeviceListAdapter(ArrayList<BluetoothDevice> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public LocalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LocalViewHolder holder = new LocalViewHolder(LayoutInflater.from(context).inflate(R.layout.devicelist, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(LocalViewHolder holder, final int position) {
        holder.tv_deviceName.setText(list.get(position).getName());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventUtil.post(new Event_BleDevice(list.get(position) , Event_BleDevice.From.Activity));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class LocalViewHolder extends RecyclerView.ViewHolder{
        private View rootView;
        private TextView tv_deviceName;

        private LocalViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.tv_deviceName = (TextView) rootView.findViewById(R.id.tv_deviceName);
        }

    }
}
