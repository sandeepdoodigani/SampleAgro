package com.example.a1a.sujhav.adapter;

/**
 * Created by Sandeep Doodigani on 30-11-2017.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.a1a.sujhav.fragments.Humidity;
import com.example.a1a.sujhav.fragments.Pressure;
import com.example.a1a.sujhav.fragments.Temperture;


public class PageAdapters extends FragmentPagerAdapter {

    private static final String TAG = PageAdapters.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 2;

    public PageAdapters(FragmentManager fm) {
        super(fm);
    }



    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Temperture();
            case 1:
                return new Humidity();

        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Temperature";
            case 1:
                return "Humidity";


        }
        return null;
    }
}
