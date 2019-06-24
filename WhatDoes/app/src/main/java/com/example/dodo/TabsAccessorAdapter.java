package com.example.dodo;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {


    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i)
    {
        switch (i)
        {
            case 0:
                YapilacaklarFragment YapilacakFragment=new YapilacaklarFragment();
                return YapilacakFragment;

            case 1:
                YapildiFragment YapildiFragment=new YapildiFragment();
                return YapildiFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position)
        {
            case 0:
                return "Yapılacaklar";
            case 1:
                return "Yapıldı";
            default:
                return null;
        }


    }
}
