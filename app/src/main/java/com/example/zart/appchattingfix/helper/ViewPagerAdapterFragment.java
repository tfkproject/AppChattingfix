package com.example.zart.appchattingfix.helper;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapterFragment extends FragmentPagerAdapter {

    private final List<Fragment> listFragmen = new ArrayList<>();
    private final List<String> lisTitle = new ArrayList<>();

    public ViewPagerAdapterFragment(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return listFragmen.get(i);
//        return FragmenKontak.newInstance(i + 1);
    }

    @Override
    public int getCount() {
        return lisTitle.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return lisTitle.get(position);
    }


    public void AddFragment (Fragment fragment, String title){
        listFragmen.add(fragment);
        lisTitle.add(title);
    }

}
