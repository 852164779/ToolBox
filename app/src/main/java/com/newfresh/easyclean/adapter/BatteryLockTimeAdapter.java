package com.newfresh.easyclean.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newfresh.easyclean.R;
import com.newfresh.easyclean.util.XmlShareUtil;


/**
 * Created by xlc on 2016/10/12.
 */
public class BatteryLockTimeAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private int[] strings;
    private Context mContext;
    private String str_s = "";
    private String str_m = "";

    public BatteryLockTimeAdapter (Context context, int[] s) {
        layoutInflater = LayoutInflater.from(context);
        this.strings = s;
        this.mContext = context;
        str_s = context.getResources().getString(R.string.batter_sleep_s);
        str_m = context.getResources().getString(R.string.batter_sleep_m);
    }

    @Override
    public int getCount () {
        return strings.length;
    }

    @Override
    public Object getItem (int position) {
        return strings[position];
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if ( convertView == null ) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.sleep_time_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.sleep_time_text);
            holder.imageView = (ImageView) convertView.findViewById(R.id.sleep_time_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if ( strings[position] > 60000 ) {
            holder.textView.setText(strings[position] / 60000 + str_m);
        } else if ( strings[position] == 60000 ) {
            holder.textView.setText("60 " + str_s);
        } else {
            holder.textView.setText("30 " + str_s);
        }

        if ( XmlShareUtil.get_sleep_time(mContext) == strings[position] ) {
            holder.imageView.setBackgroundResource(R.drawable.battery_sleep_time_select);
        } else {
            holder.imageView.setBackgroundResource(R.drawable.battery_sleep_time_nomal);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView textView;
        public ImageView imageView;
    }
}