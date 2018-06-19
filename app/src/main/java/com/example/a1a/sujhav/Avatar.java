package com.example.a1a.sujhav;

/**
 * Created by Sandeep Doodigani on 30-11-2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a1a.sujhav.fragments.Home;
import com.example.a1a.sujhav.fragments.SampleSubmit;
import com.example.a1a.sujhav.fragments.SoilBasesSuggestion;
import com.example.a1a.sujhav.fragments.SoilManagement;
import com.example.a1a.sujhav.fragments.WeatherMap;
import com.example.a1a.sujhav.fragments.WeatherSuggestion;

public class Avatar extends AppCompatActivity {
    android.app.FragmentManager fm;
    Fragment fragment = null;
    String AccountId;
    TextView nav_user;
    String farmer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        AccountId = sharedpreferences.getString("AccountId", null);
        farmer=sharedpreferences.getString("FarmerName", null);


        if (TextUtils.isEmpty(AccountId)) {
            Intent in = new Intent(Avatar.this, MainActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);
            finish();
        }
        else{
            setContentView(R.layout.activity_avatar);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            fm = getFragmentManager();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

            View hView =  navigationView.getHeaderView(0);
            nav_user = (TextView)hView.findViewById(R.id.user);
            nav_user.setText(farmer);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();

                    if (id == R.id.Soil_Management) {

                        // toast("Soil_Management");
                        fragment=new SoilManagement();

                        // Handle the camera action
                    } else if (id == R.id.Local_Weather_Map) {

                        //  toast("Local_Weather_Map");
                        fragment=new WeatherMap();
                    } else if (id == R.id.Submit_a_Sample) {

                        // toast("Submit_a_Sample");
                        fragment=new SampleSubmit();
                    }else if (id == R.id.Soil_Based) {
                        //toast("Soil_Based");
                        fragment=new SoilBasesSuggestion();

                    } else if (id == R.id.Weather_Based) {
                        // toast("Weather_Based");
                        fragment=new WeatherSuggestion();

                    }
                    else if (id == R.id.logout) {
                        // toast("SIGNOUT");
                        Intent in =new Intent(Avatar.this,MainActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.clear();
                        editor.apply();
                        editor.commit();
                        startActivity(in);
                        finish();
                        // fragment=new WeatherSuggestion();

                    }

                    //replacing the fragment
                    if (fragment != null) {

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.commit();
                    }

                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;

                }
            });
            fragment = new Home();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
            BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    int id = item.getItemId();

                    if (id == R.id.home) {

                        //toast("Soil_Management");
                        fragment=new Home();

                        // Handle the camera action
                    } else if (id == R.id.submitSample) {

                        //toast("Local_Weather_Map");
                        fragment=new SampleSubmit();
                    } else if (id == R.id.weather) {

                        //toast("Submit_a_Sample");
                        fragment=new WeatherSuggestion();
                    }
                    //replacing the fragment
                    if (fragment != null) {

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, fragment);
                        ft.commit();
                    }
                    return true;
                }
            });


        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.avatar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            Intent in =new Intent(Avatar.this,MainActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SharedPreferences sharedpreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
            editor.commit();
            startActivity(in);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void toast(String message)
    {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
}
