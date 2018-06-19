package com.example.a1a.sujhav;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.a1a.sujhav.adapter.PageAdapter2;
import com.example.a1a.sujhav.adapter.PageAdapters;

public class Wind extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wind);
        tabLayout = (TabLayout)findViewById(R.id.tabs1);
        viewPager = (ViewPager)findViewById(R.id.view_pager);

        viewPager.setAdapter(new PageAdapter2(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }
}
