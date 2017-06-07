package com.youyi.weigan.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.youyi.weigan.R;
import com.youyi.weigan.beans.GravA;
import com.youyi.weigan.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.ContentValues.TAG;

/**
 * Created by user on 2017/4/7.
 */

public class MyGravADataAdapter extends BaseAdapter {
    private ArrayList<GravA> list;
    private LayoutInflater layoutInflater;

    public MyGravADataAdapter(ArrayList<GravA> list, Context context) {
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_result, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        GravA obj = list.get(position);
        viewHolder.tv__result_time.setText(DateUtils.getDateToString(obj.getTime() * 100));
        viewHolder.tv__result_x.setText(String.valueOf(obj.getVelX()));
        viewHolder.tv__result_y.setText(String.valueOf(obj.getVelY()));
        viewHolder.tv__result_z.setText(String.valueOf(obj.getVelZ()));
        return convertView;
    }

    private static class ViewHolder {
        private View rootView;
        private TextView tv__result_time;
        private TextView tv__result_x;
        private TextView tv__result_y;
        private TextView tv__result_z;

        private ViewHolder(View rootView) {
            this.rootView = rootView;
            this.tv__result_time = (TextView) rootView.findViewById(R.id.tv__result_time);
            this.tv__result_x = (TextView) rootView.findViewById(R.id.tv__result_x);
            this.tv__result_y = (TextView) rootView.findViewById(R.id.tv__result_y);
            this.tv__result_z = (TextView) rootView.findViewById(R.id.tv__result_z);
        }

    }
}
