package com.example.dodo;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class DoesActivity extends AppCompatActivity {

    private TabLayout myTabLayout;
    private ViewPager myViewPager;
    private TabsAccessorAdapter myTabsAccessorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_does);
        //-------------------------------------
        //-------------------------------------

        myViewPager=(ViewPager)findViewById(R.id.main_tabs_pager);
        myTabLayout= (TabLayout) findViewById(R.id.main_tabs);
        myTabsAccessorAdapter=new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout.setupWithViewPager(myViewPager);
    }

}
