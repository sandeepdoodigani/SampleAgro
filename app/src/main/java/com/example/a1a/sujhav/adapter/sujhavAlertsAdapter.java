package com.example.a1a.sujhav.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a1a.sujhav.R;

import java.util.List;

/**
 * Created by Abhishek on 25-12-2017.
 */

public class sujhavAlertsAdapter extends RecyclerView.Adapter<sujhavAlertsAdapter.MyView> {

    private List<String> title;
    private List<String> description;

    public class MyView extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView des;

        public MyView(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            des = (TextView) view.findViewById(R.id.description);

        }
    }


    public sujhavAlertsAdapter(List<String> title,List<String> description) {
        this.title = title;
        this.description=description;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sujhav_alerts, parent, false);

        return new MyView(itemView);
    }



    @Override
    public void onBindViewHolder(MyView holder, final int position) {

        holder.title.setText(title.get(position));
        holder.des.setText(description.get(position));

    }

    @Override
    public int getItemCount() {
        return title.size();
    }

}