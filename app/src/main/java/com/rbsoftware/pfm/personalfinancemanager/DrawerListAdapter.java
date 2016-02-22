package com.rbsoftware.pfm.personalfinancemanager;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link BaseAdapter} subclass.
 * Holds list of navigation drawer items
 **/

public class DrawerListAdapter extends BaseAdapter {
    private int[] imageArray;
    private String[] textArray;
    private Activity mContext;


    public DrawerListAdapter(FragmentActivity context, int[] images, String[] time) {
        imageArray = images;
        textArray = time;
        mContext = context;

    }

    @Override
    public int getCount() {
        return imageArray.length;
    }

    public class Holder {
        ImageView imgDrawerRow;
        TextView tvDrawerRow;

        public Holder(View base) {
            imgDrawerRow = (ImageView) base.findViewById(R.id.img_drawer_row);
            tvDrawerRow = (TextView) base.findViewById(R.id.tv_drawer_row);
        }
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View v = convertView;
        Holder viewHolder;
        if (convertView == null) {
            LayoutInflater li = mContext.getLayoutInflater();
            v = li.inflate(R.layout.drawer_list_row, parent,false);
            viewHolder = new Holder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (Holder) v.getTag();
        }

        viewHolder.imgDrawerRow.setImageResource(imageArray[pos]);
        viewHolder.tvDrawerRow.setText(textArray[pos]);

        return v;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }


}
