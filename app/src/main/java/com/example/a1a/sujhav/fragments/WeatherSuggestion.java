package com.example.a1a.sujhav.fragments;

/**
 * Created by Sandeep Doodigani on 4-12-2017.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.a1a.sujhav.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class WeatherSuggestion extends Fragment {

    Spinner ct,ca,st,s;
    TextView suggestion;
    Button getSuggestion;
    String data,AccountId;
    String[] crop_type =
            {"Cotton", "Maize", "GroundNuts"};
    String[] crop_age =
            {"1 week","1 week","1 week","1 months","2 months","3 months","4 months","5 months","6 months"};
    String[] soil_type =
            {"Red Sand Soil", "Red Loamy Soils", "Black Soils","Mixed red Black soils mountain soil","Alluvial Soils","Red and Yellow Soils","Sub mountain Soil","Desert soils"};
    String[] Season =
            {"Karif", "Rabi", "Summer"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View v = inflater.inflate(R.layout.fragment_weather_suggestion, container, false);
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        AccountId = sharedpreferences.getString("AccountId", null);
        ct=(Spinner) v.findViewById(R.id.spinner1);
        ca=(Spinner) v.findViewById(R.id.spinner2);
        st=(Spinner) v.findViewById(R.id.spinner3);
        s=(Spinner) v.findViewById(R.id.spinner4);
        suggestion=(TextView) v.findViewById(R.id.suggestionW);
        getSuggestion = (Button) v.findViewById(R.id.getSuggestionW);


        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this.getActivity(), R.layout.textforspinner, crop_type);
        adapter1.setDropDownViewResource(android.R.layout.simple_list_item_1);
        ct.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getActivity(), R.layout.textforspinner, crop_age);
        adapter2.setDropDownViewResource(android.R.layout.simple_list_item_1);
        ca.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this.getActivity(), R.layout.textforspinner, soil_type);
        adapter3.setDropDownViewResource(android.R.layout.simple_list_item_1);
        st.setAdapter(adapter3);

        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(this.getActivity(), R.layout.textforspinner, Season);
        adapter4.setDropDownViewResource(android.R.layout.simple_list_item_1);
        s.setAdapter(adapter4);
        getSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    data += "&" + URLEncoder.encode("FarmerID", "UTF-8") + "="
                            + URLEncoder.encode(AccountId, "UTF-8");
                    data += "&" + URLEncoder.encode("CropType", "UTF-8") + "="
                            + URLEncoder.encode(ct.getSelectedItem().toString(), "UTF-8");
                    data += "&" + URLEncoder.encode("CropAge", "UTF-8") + "="
                            + URLEncoder.encode(ca.getSelectedItem().toString(), "UTF-8");
                    data += "&" + URLEncoder.encode("SoilType", "UTF-8") + "="
                            + URLEncoder.encode(st.getSelectedItem().toString(), "UTF-8");
                    data += "&" + URLEncoder.encode("Season", "UTF-8") + "="
                            + URLEncoder.encode(s.getSelectedItem().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                if (data != null) {
                 sendData sendData = new sendData();
                    sendData.execute();
                }
            }
        });
        return v;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("WeatherSuggestion");
    }
    class sendData extends AsyncTask<Void, Void, String> {

        ProgressDialog pDialog;

        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...Sending...");
            pDialog.setCancelable(true);
            pDialog.setProgress(0);
            pDialog.setMax(100);
            pDialog.show();
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL("http://www.sujhav.in/API/weather_information");

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

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

            pDialog.dismiss();
            Log.i("DataAbhi", s);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                String status = jsonObject.getString("err");
                String msg = jsonObject.getString("msg");
                if (status.contentEquals("1")) {
                    suggestion.setText(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}