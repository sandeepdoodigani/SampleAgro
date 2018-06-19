package com.example.a1a.sujhav.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Pressure extends Fragment {

    ProgressDialog pDialog;
    String data;

    LineChart lineChart;
    ArrayList<String> listx;
    ArrayList<Entry> listy;
    int a;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_pressure, container, false);

        lineChart=(LineChart)view.findViewById(R.id.chartQuality);

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
                //  conn.setDoOutput(true);


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
            } catch (Exception e) {
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
                    String temp= jsonObject.getJSONObject("main").getString("pressure").toString();
                    String date=jsonObject.getString("dt_txt");

                    Float f= Float.parseFloat(temp);

                     a=Math.round(f);


                    listx.add(date);
                    listy.add(new Entry(Float.valueOf(a),i));




                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(listx!=null&&listy!=null){
                setData();
            }

            //Toast.makeText(getActivity(),s, Toast.LENGTH_SHORT).show();

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
            set1.setLineWidth(1f);
            //set1.setDrawCircleHole(true);
            set1.setDrawCircles(true);
            set1.setValueTextSize(9f);

            set1.setDrawVerticalHighlightIndicator(true);
            set1.setDrawFilled(true);

            ArrayList dataSets = new ArrayList();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(listx, dataSets);

            // set data
            lineChart.setData(data);
            lineChart.animate();
            lineChart.getLineData();
            set1.isVisible();
            pDialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();

        }
    }
}
