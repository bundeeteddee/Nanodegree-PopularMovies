package com.tinytinybites.popularmovies.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tinytinybites.popularmovies.app.fragment.MovieOverviewFragment;
import com.tinytinybites.popularmovies.app.fragment.MovieReviewsFragment;
import com.tinytinybites.popularmovies.app.fragment.MovieTrailersFragment;
import com.tinytinybites.popularmovies.app.model.Movie;

/**
 * Created by bundee on 8/11/16.
 */
public class MovieDetailsFragmentPagerAdapter extends FragmentPagerAdapter{
    //Tag
    protected static final String TAG = MovieDetailsFragmentPagerAdapter.class.getCanonicalName();

    //Variables
    private Movie mMovie;

    /**
     * Constructor
     * @param fm
     * @param movie
     */
    public MovieDetailsFragmentPagerAdapter(FragmentManager fm, Movie movie) {
        super(fm);
        this.mMovie = movie;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:{
                //Synopsis
                return MovieOverviewFragment.newInstance(mMovie);
            }
            case 1:{
                //Review
                return MovieReviewsFragment.newInstance(mMovie);
            }
            case 2:{
                //Trailers
                return MovieTrailersFragment.newInstance(mMovie);
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
