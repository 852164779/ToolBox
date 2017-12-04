package test.com.androidtest.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import test.com.androidtest.R;


public class SleepAdapter extends BaseAdapter {


    private LayoutInflater layoutInflater;

    private int[] strings;

    private Context mContext;

    public SleepAdapter(Context context, int[] s) {
        layoutInflater = LayoutInflater.from(context);
        this.strings = s;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return strings.length;
    }

    @Override
    public Object getItem(int position) {
        return strings[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = layoutInflater.inflate(R.layout.sleep_time_item, null);

            holder.textView = (TextView) convertView.findViewById(R.id.sleep_time_text);

            holder.imageView = (ImageView) convertView.findViewById(R.id.sleep_time_img);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        if (strings[position] > 60000) {

            holder.textView.setText(strings[position] / 60000 + " minutes");

        } else if (strings[position] == 60000) {

            holder.textView.setText("60 seconds");
        } else {
            holder.textView.setText("30 seconds");
        }

        if (Utils.get_sleep_time(mContext) == strings[position]) {

            holder.imageView.setBackgroundResource(R.drawable.battery_sleep_time_select);

        } else {
            holder.imageView.setBackgroundResource(R.drawable.battery_sleep_time_nomal);
        }
        return convertView;
    }

    public final class ViewHolder {

        public TextView textView;

        public ImageView imageView;
    }
}
