package com.example.a1a.sujhav.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.example.a1a.sujhav.R;

/**
 * Created by Abhishek on 09-09-2016.
 */

public class SampleList extends BaseAdapter {


    ArrayList sample;
    ArrayList lat;
    ArrayList lon;
    ArrayList ph1;
    ArrayList ec1;
    ArrayList mos1;
    Context mc;



    public SampleList(Context context, ArrayList sampleno, ArrayList latitude, ArrayList logitude, ArrayList ph, ArrayList ec,ArrayList mos) {
        sample = sampleno;
        mc = context;
        lat = latitude;
        lon=logitude;
        ph1=ph;
        ec1=ec;
        mos1=mos;


    }


    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public Object getItem(int position) {

        return sample.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder vh;
        if (convertView == null) {
            v = View.inflate(mc, R.layout.record_layout, null);
            vh = new ViewHolder();
            vh.sampleNo = (TextView) v.findViewById(R.id.sampleNo);
            vh.lat = (TextView) v.findViewById(R.id.latitude);
            vh.lon= (TextView)v.findViewById(R.id.longitude);
            vh.phValue=(TextView)v.findViewById(R.id.phValue);
            vh.ecValue=(TextView)v.findViewById(R.id.ecvalue);
            vh.mosValue=(TextView)v.findViewById(R.id.moisture);



            v.setTag(vh);

        } else {
            vh = (ViewHolder) v.getTag();
        }
        try {
            vh.sampleNo.setText(sample.get(position).toString());
            vh.lat.setText(lat.get(position).toString());
            vh.lon.setText(lon.get(position).toString());
            vh.phValue.setText(ph1.get(position).toString());
            vh.ecValue.setText(ec1.get(position).toString());
            vh.mosValue.setText(mos1.get(position).toString());




        } catch (NullPointerException | IndexOutOfBoundsException e) {

            e.printStackTrace();
        }


        return v;
    }

    static class ViewHolder {
        TextView sampleNo;
        TextView lat;
        TextView lon;
        TextView phValue;
        TextView ecValue;
        TextView mosValue;

    }
}
