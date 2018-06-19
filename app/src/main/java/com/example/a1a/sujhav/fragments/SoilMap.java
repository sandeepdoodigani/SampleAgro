package com.example.a1a.sujhav.fragments;

/**
 * Created by Sandeep Doodigani on 30-11-2017.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.a1a.sujhav.R;



public class SoilMap extends Fragment {

    WebView myWebView;
    String AccountId;
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_soil_map, container, false);
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        AccountId = sharedpreferences.getString("AccountId", null);
        myWebView = (WebView) v.findViewById(R.id.webView);
        myWebView.loadUrl("http://www.sujhav.in/API/soil_map?FarmerID="+AccountId);

        // Enable Javascript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        myWebView.setWebViewClient(new WebViewClient());

        return v;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        //you can set the title for your toolbar here for different fragments different titles


        getActivity().setTitle("SoilMonitor");
    }
}