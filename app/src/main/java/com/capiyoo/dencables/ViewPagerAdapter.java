package com.capiyoo.dencables;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
                return new PendingDetail();
            case 3:
                return new com.capiyoo.dencables.ReportFragment();

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
                return "Customers";
            case 1:
                return "Billing";
            case 2:
                return "Pending";
            case 3:
                return "Report";

        }
        return null;
    }
}
