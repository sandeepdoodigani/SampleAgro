package com.example.a1a.sujhav.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1a.sujhav.ConnectionRecevier;
import com.example.a1a.sujhav.R;
import com.example.a1a.sujhav.WeatherActivity;
import com.example.a1a.sujhav.Wind;
import com.example.a1a.sujhav.adapter.sujhavAlertsAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import pl.pawelkleczkowski.customgauge.CustomGauge;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home extends Fragment  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ConnectionRecevier.ConnectivityReceiverListener{

    private CustomGauge gauge1;
    RecyclerView recyclerView;
    List title,description;
    Double latitude,longitude;
    ImageView left,right;
    LinearLayout r,w;
    Fragment fragment = null;
    TextView t1,t2,t,p,h,ws,wd;
    GoogleApiClient mGoogleApiClient;
    String temp,hum,pressure,windspeed,winddirection;
    int i =0;
    sujhavAlertsAdapter sujhavAlertsAdapter;
    LocationRequest mLocationRequest;
    LinearLayoutManager HorizontalLayout;
    public Home() {
        // Required empty public constructor
    }


    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        r=view.findViewById(R.id.temphum);
        w=view.findViewById(R.id.wind);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        recyclerView=view.findViewById(R.id.sujhavAlerts);
        createLocationRequest();
        t1=view.findViewById(R.id.wea);
        t2=view.findViewById(R.id.soil);
        t=view.findViewById(R.id.temperature);
        h=view.findViewById(R.id.humidity);
        p=view.findViewById(R.id.pressure);
        ws=view.findViewById(R.id.windspeed);
        wd=view.findViewById(R.id.direc);
        left=view.findViewById(R.id.left);
        right=view.findViewById(R.id.right);
        title=new ArrayList();
        description=new ArrayList();
        title.add("Alert 1");
        title.add("Alert 2");
        title.add("Alert 3");
        title.add("Alert 4");
        description.add("Descrption 1");
        description.add("Descrption 2");
        description.add("Descrption 3");
        description.add("Descrption 4");

        sujhavAlertsAdapter = new sujhavAlertsAdapter(title,description);
        HorizontalLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);

        recyclerView.setAdapter(sujhavAlertsAdapter);
        gauge1=view.findViewById(R.id.gauge1);
        gauge1.setValue(1);




        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WeatherActivity.class);
                startActivity(intent);
            }
        });

        w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Wind.class);
                startActivity(intent);
            }
        });

        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment=new WeatherSuggestion();
                if (fragment != null) {

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }

            }
        });


        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment=new SoilBasesSuggestion();
                if (fragment != null) {

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_frame, fragment);
                    ft.commit();
                }

            }
        });


        return view;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) throws JSONException {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest,this
        );

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        Log.i("Location",""+latitude+","+longitude);

        if(latitude!=0&&longitude!=0){

            sendData s=new sendData();
            s.execute();
        }

    }

    class sendData extends AsyncTask<Void, Void, String> {


        protected void onPreExecute() {

        }

        protected String doInBackground(Void... urls) {

            try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=f287a28bbf03187cfd870a8749bd5bdc&units=metric");
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

            }


            catch (Exception e ) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObjectA =new JSONObject(s);

                temp = jsonObjectA.getJSONObject("main").getString("temp").toString();
                hum = jsonObjectA.getJSONObject("main").getString("humidity").toString();
                pressure = jsonObjectA.getJSONObject("main").getString("pressure").toString();

                windspeed = jsonObjectA.getJSONObject("wind").getString("speed").toString();
                winddirection = jsonObjectA.getJSONObject("wind").getString("deg").toString();

                Float f= Float.parseFloat(temp);

                i=Math.round(f);

                t.setText(i+"°C");
                h.setText(hum+"%");
                p.setText("Pressure :"+pressure+" hPa");
                ws.setText("Wind speed: "+windspeed+"mts/sec");
                wd.setText("Wind Direction :"+winddirection);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000*10);
        mLocationRequest.setFastestInterval(1000*5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}
