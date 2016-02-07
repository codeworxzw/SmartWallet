package com.rbsoftware.pfm.personalfinancemanager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;




public class DrawerListAdapter extends BaseAdapter {
    private int[] imageArray;
    private String[] textArray;
    private Activity mContext;

    private LayoutInflater mLayoutInflater = null;

    public DrawerListAdapter(FragmentActivity context, int[] images, String[] time) {
        imageArray = images;
        textArray = time;
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return imageArray.length;
    }
    public class Holder
    {
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
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.drawer_list_row, null);
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
