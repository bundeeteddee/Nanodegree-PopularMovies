package com.tinytinybites.popularmovies.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.adapter.MovieDetailsFragmentPagerAdapter;
import com.tinytinybites.popularmovies.app.constant.IntentExtra;
import com.tinytinybites.popularmovies.app.model.Movie;

/**
 * Created by bundee on 8/15/16.
 */
public class MovieDetailFragment extends Fragment{
    //Tag
    protected static final String TAG = MovieDetailFragment.class.getCanonicalName();

    //Bindings
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;

    //Variables
    private Movie mMovie;
    private Unbinder mUnbinder;
    private MovieDetailsFragmentPagerAdapter mPageAdapter;

    /**
     * Static constructor
     * @param movie
     * @return
     */
    public static MovieDetailFragment newInstance(Movie movie){
        MovieDetailFragment fragment = new MovieDetailFragment();

        //Supply arguments
        Bundle args = new Bundle();
        args.putParcelable(IntentExtra.MOVIE, movie);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(IntentExtra.MOVIE);
        }else if(savedInstanceState == null ||
                !savedInstanceState.containsKey(IntentExtra.MOVIE)) {
            //nothing from saved instance, attempt to get it from intent bundle
            Bundle extras = getActivity().getIntent().getExtras();
            if(extras != null){
                if(extras.containsKey(IntentExtra.MOVIE)){
                    mMovie = extras.getParcelable(IntentExtra.MOVIE);
                }
            }
        }else {
            mMovie = savedInstanceState.getParcelable(IntentExtra.MOVIE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IntentExtra.MOVIE, mMovie);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        //Butterknife
        mUnbinder = ButterKnife.bind(this, view);

        //Setup tabs
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.movie_details_synopsis_title)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.movie_details_reviews_title)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.movie_details_trailers_title)));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mPageAdapter = new MovieDetailsFragmentPagerAdapter(getChildFragmentManager(), mMovie);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private Movie getMovie() {  return mMovie;}
    private void setMovie(Movie movie){ this.mMovie = movie;}

}
