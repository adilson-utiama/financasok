package com.asuprojects.walletok.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titulos = new ArrayList<>();

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }
    public void adicionar(Fragment fragment, String tituloAba){
        this.fragments.add(fragment);
        this.titulos.add(tituloAba);
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.titulos.get(position);
    }
}
