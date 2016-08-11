package com.tinytinybites.popularmovies.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.constant.IntentExtra;
import com.tinytinybites.popularmovies.app.model.Movie;

/**
 * Created by bundee on 8/11/16.
 */
public class MovieOverviewFragment extends Fragment {
    //Tag
    protected static final String TAG = MovieOverviewFragment.class.getCanonicalName();

    //Variables
    private Movie mMovie;
    private Unbinder mUnbinder;

    //Bindings
    @BindView(R.id.synopsis) TextView mSynopsis;

    /**
     * Static constructor
     * @param movie
     * @return
     */
    public static MovieOverviewFragment newInstance(Movie movie){
        MovieOverviewFragment fragment = new MovieOverviewFragment();
        fragment.setMovie(movie);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null ||
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
        View view = inflater.inflate(R.layout.fragment_movie_overview, container, false);

        //Butterknife
        mUnbinder = ButterKnife.bind(this, view);

        //Set data
        if(mMovie != null) {
            mSynopsis.setText(mMovie.getOverview());
        }

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
