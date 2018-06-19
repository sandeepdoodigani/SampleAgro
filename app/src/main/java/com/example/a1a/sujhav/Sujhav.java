package com.example.a1a.sujhav;

import android.app.Application;

/**
 * Created by Abhishek on 23-12-2017.
 */

public class Sujhav extends Application {

    private static Sujhav mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized Sujhav getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectionRecevier.ConnectivityReceiverListener listener) {
        ConnectionRecevier.connectivityReceiverListener = listener;
    }
}
