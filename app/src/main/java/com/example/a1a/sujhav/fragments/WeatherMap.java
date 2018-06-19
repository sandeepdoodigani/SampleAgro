package com.example.a1a.sujhav.fragments;

/**
 * Created by Sandeep Doodigani on 4-12-2017.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.a1a.sujhav.R;
import com.example.a1a.sujhav.adapter.CustomFragmentPageAdapter;
import com.example.a1a.sujhav.adapter.TransparentTileOWM;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;


public class WeatherMap extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
  Spinner selection;
    GoogleMap map;
    Double latitude,longitude;
    private static String OWM_TILE_URL = "http://tile.openweathermap.org/map/%s/%d/%d/%d.png?appid=f287a28bbf03187cfd870a8749bd5bdc";
    private Spinner spinner;
    private String tileType = "temp";
    private TileOverlay tileOver;
    View view;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
       view=inflater.inflate(R.layout.fragment_weather_map, container, false);
        spinner = (Spinner) view.findViewById(R.id.selection);
        statusCheck();
        String[] tileName = new String[]{"Temperature","Clouds","Precipitations", "Snow", "Rain", "Wind", "Sea level press."};
        ArrayAdapter<String> adpt = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, tileName);
        spinner.setAdapter(adpt);
        createLocationRequest();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
       spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               switch (i) {
                   case 0:
                       tileType = "temp";
                       break;
                   case 1:
                       tileType = "clouds";
                       break;
                   case 2:
                       tileType = "precipitation";
                       break;
                   case 3:
                       tileType = "snow";
                       break;
                   case 4:
                       tileType = "rain";
                       break;
                   case 5:
                       tileType = "wind";
                       break;
                   case 6:
                       tileType = "pressure";
                       break;

               }
               if (map != null) {
                   tileOver.remove();
                   setUpMap();
               }
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("WeatherMap");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;

        setUpMap();
    }
    private TileProvider createTransparentTileProvider() {
        return new TransparentTileOWM(tileType);
    }
    private void setUpMap() {
        //tileOver = map.addTileOverlay(new TileOverlayOptions().tileProvider(createTilePovider()));

       tileOver = map.addTileOverlay(new TileOverlayOptions().tileProvider(createTransparentTileProvider()));
    }
private TileProvider createTilePovider() {
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {
                String fUrl = String.format(OWM_TILE_URL, tileType == null ? "temp" : tileType, zoom, x, y);
                URL url = null;
                try {
                    url = new URL(fUrl);
                }
                catch(MalformedURLException mfe) {
                    mfe.printStackTrace();
                }

                return url;
            }
        } ;

        return tileProvider;
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
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng MyLocation = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 14));
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
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}