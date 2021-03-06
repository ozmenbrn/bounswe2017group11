package com.culturage.oceans_eleven.culturage.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.culturage.oceans_eleven.culturage.baseClasses.HeritageItem;
import com.culturage.oceans_eleven.culturage.newsFeed.RecommendedHeritageItemsFragment;
import com.culturage.oceans_eleven.culturage.newsFeed.UniversalNewsfeedFragment;

/**
 * {@link HeritageItemListAdapter} is a {@link FragmentPagerAdapter} that can provide the layout for
 * each list item based on a data source which is a list of {@link HeritageItem} objects.
 */
public class HeritageItemListAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context appContext;


    /**
     * Create a new {@link HeritageItemListAdapter} object.
     *
     * @param appContext is the context of the app
     * @param fragMan is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     */
    public HeritageItemListAdapter(Context appContext, FragmentManager fragMan) {
        super(fragMan);
        this.appContext = appContext;

    }


    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new UniversalNewsfeedFragment();
        } else  {
            return new RecommendedHeritageItemsFragment();
        }

    }

    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Universal";
        }else {
            return "Unique";
        }
    }
}
