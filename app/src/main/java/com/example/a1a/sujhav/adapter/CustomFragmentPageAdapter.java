package com.example.a1a.sujhav.adapter;

/**
 * Created by Sandeep Doodigani on 30-11-2017.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.a1a.sujhav.fragments.SampleSubmit;
import com.example.a1a.sujhav.fragments.SoilMap;
import com.example.a1a.sujhav.fragments.WeatherMap;
import com.example.a1a.sujhav.fragments.WeatherSuggestion;


public class CustomFragmentPageAdapter extends FragmentPagerAdapter {

    private static final String TAG = CustomFragmentPageAdapter.class.getSimpleName();

    private static final int FRAGMENT_COUNT = 2;

    public CustomFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SoilMap();
            case 1:
                return new SampleSubmit();

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
                return "Soil Map";
            case 1:
                return "Submit Sample";


        }
        return null;
    }
}
