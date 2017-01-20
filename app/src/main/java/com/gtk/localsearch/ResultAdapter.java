package com.gtk.localsearch;

import android.content.Context;
import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by RISHAB on 21-12-2016.
 */
public class ResultAdapter extends ArrayAdapter<ResultPlace>{

    ArrayList<ResultPlace> resultplace;
    Context context;

    public ResultAdapter(Context context, int resource, ArrayList<ResultPlace> objects) {
        super(context, resource, objects);
        resultplace = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        resultViewHolder holder = null;
        if(row==null)
        {
            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflate.inflate(R.layout.row , parent , false);
            holder = new resultViewHolder(row);
            row.setTag(holder);
        }else{
            holder = (resultViewHolder) row.getTag();
        }

        ResultPlace rp = resultplace.get(position);
        holder.title.setText(rp.getTitle());
        holder.address.setText(rp.getAddress());
        return row;
    }

    class resultViewHolder{
        TextView title;
        TextView address;

        resultViewHolder(View v)
        {
            title =(TextView) v.findViewById(R.id.title);
            address =(TextView) v.findViewById(R.id.address);
        }
    }
}
