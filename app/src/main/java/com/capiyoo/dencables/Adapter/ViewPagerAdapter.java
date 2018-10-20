package com.capiyoo.dencables.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.capiyoo.dencables.Fragments.BillingFragment;
import com.capiyoo.dencables.Fragments.HomeFragment;
import com.capiyoo.dencables.Fragments.Lineman;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new BillingFragment();
            case 2:
                return new Lineman();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Customers";
            case 1:
                return "Pending";
            case 2:
                return "Lineman";
        }
        return null;
    }
}
