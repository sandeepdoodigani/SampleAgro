package com.example.a1a.sujhav;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.a1a.sujhav.adapter.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class DataService extends Service {
    String data;
    DbHelp dbHelp;
    public DataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("DataService","ServiceCreated");
        dbHelp=new DbHelp(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Sujhav.getInstance().setConnectivityListener(new ConnectionRecevier.ConnectivityReceiverListener() {
            @Override
            public void onNetworkConnectionChanged(boolean isConnected) throws JSONException {
                if(isConnected){
                    data=dbHelp.getData();
                    if (data!=null){
                        sendData sendData=new sendData();
                        sendData.execute();
                    }

                }

            }
        });



        return START_STICKY;
    }
    class sendData extends AsyncTask<Void, Void, String> {
            String status;
            String msg;

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL("http://www.sujhav.in/API/soil_management");

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
            JSONObject jsonObject= null;
            try {
                jsonObject = new JSONObject(s);
                status=jsonObject.getString("err");
                msg=jsonObject.getString("msg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(status.contentEquals("1")){
                Log.i("DataService","SentSucess");
                stopService(new Intent(DataService.this,DataService.class));
                dbHelp.deleteAll();
            }


        }
    }


    private boolean checkConnection() {
        boolean isConnected = ConnectionRecevier.isConnected();

        return isConnected;
    }



}
