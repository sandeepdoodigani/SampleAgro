package com.example.a1a.sujhav.fragments;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.a1a.sujhav.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class WindSpeed extends Fragment {

    ProgressDialog pDialog;
    TextView text;
    LineChart lineChart;
    ArrayList<String> listx;
    ArrayList<Entry> listy;
    public WindSpeed() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_wind_speed, container, false);
        lineChart=(LineChart)view.findViewById(R.id.chartTemp);
        listx=new ArrayList<String>();
        listy=new ArrayList<Entry>();
        getData l=new getData();
        l.execute();
        return view;
    }


    class getData extends AsyncTask<Void, Void, String> {


        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            pDialog.setProgress(0);
            pDialog.setMax(100);
            pDialog.show();
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?lat=17&lon=7&APPID=f287a28bbf03187cfd870a8749bd5bdc&units=metric");
                URLConnection conn = url.openConnection();
                // conn.setDoOutput(true);


                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    System.out.println(" bufferedreader response :" + bufferedReader);

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);


                    }
                    bufferedReader.close();
                    Log.e("res", stringBuilder.toString());
                    return stringBuilder.toString();
                } finally {

                }
            } catch (Exception e ) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObjectA =new JSONObject(s);
                JSONArray jsonArray=jsonObjectA.getJSONArray("list");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject=jsonArray.getJSONObject(i);

                    String temp= jsonObject.getJSONObject("wind").getString("speed").toString();
                    String date=jsonObject.getString("dt_txt");


                    //  text.setText(stepCount);
                    listx.add(date);
                    listy.add(new Entry(Float.valueOf(temp),i));




                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(listx!=null&&listy!=null){
                setData();
            }

            //  Toast.makeText(getActivity(),s, Toast.LENGTH_SHORT).show();

        }
    }
    private void setData() {
        try {

            LineDataSet set1;

            // create a dataset and give it a type
            set1 = new LineDataSet(listy, "Time");
            set1.setFillAlpha(110);
            set1.setColor(Color.DKGRAY);
            set1.setCircleColor(Color.RED);
            set1.setDrawCircleHole(true);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);

            ArrayList dataSets = new ArrayList();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(listx, dataSets);

            // set data
            lineChart.setData(data);
            set1.isVisible();
            pDialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();

        }
    }


}
