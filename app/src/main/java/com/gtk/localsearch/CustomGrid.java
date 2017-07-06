package com.gtk.localsearch;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by RISHAB on 25-01-2017.
 */
public class CustomGrid extends BaseAdapter{

    private Context mContext;
    private String[] web;
    private int Imageid[];

    public CustomGrid(Context c,String[] web,int[] Imageid ) {
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return web.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid = convertView;
        resultViewHolder holder = null;
        if(grid==null)
        {
            LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflate.inflate(R.layout.single_grid, parent , false);
            holder = new resultViewHolder(grid);
            grid.setTag(holder);
        }else{
            holder = (resultViewHolder) grid.getTag();
        }

        holder.textView.setText(web[position]);
        Resources res = grid.getResources(); // need this to fetch the drawable
        Drawable draw = res.getDrawable(Imageid[position]);
        holder.imageView.setImageDrawable(draw);

        return grid;
    }

    class resultViewHolder{
        TextView textView;
        ImageView imageView;

        resultViewHolder(View v)
        {
            textView = (TextView) v.findViewById(R.id.grid_text);
            imageView = (ImageView)v.findViewById(R.id.grid_image);
        }
    }

}
