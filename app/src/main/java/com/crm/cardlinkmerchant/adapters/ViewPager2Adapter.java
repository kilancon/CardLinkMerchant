package com.crm.cardlinkmerchant.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.crm.cardlinkmerchant.fragments.Spend1Fragment;
import com.crm.cardlinkmerchant.fragments.Spend2Fragment;
import com.crm.cardlinkmerchant.fragments.Spend3Fragment;

public class ViewPager2Adapter extends FragmentStateAdapter {


    public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return  new Spend1Fragment();
            case 1:
                return  new Spend2Fragment();
            case 2:
                return  new Spend3Fragment();
            default:
                return new Spend1Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
