package com.capiyoo.dencables;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new MyAccount();
            case 2:
                return new PendingDetail();
            case 3:
                return new BlueToothFragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "CUSTOMERS";
            case 1:
                return "BILLING";
            case 2:
                return "PENDING";
            case 3:
                return "NOTIFICATION";

        }
        return null;
    }
}
