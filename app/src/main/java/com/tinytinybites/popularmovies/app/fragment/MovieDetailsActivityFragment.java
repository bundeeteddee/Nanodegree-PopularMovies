package com.tinytinybites.popularmovies.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.constant.IntentExtra;
import com.tinytinybites.popularmovies.app.model.Movie;
import com.tinytinybites.popularmovies.app.util.DateUtil;
import com.tinytinybites.popularmovies.app.util.UrlUtil;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsActivityFragment extends Fragment {
    //Tag
    protected static final String TAG = MovieDetailsActivityFragment.class.getCanonicalName();

    //Variables
    private Movie mMovie;

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

            //TODO: What to do when mMovie is null?
        }else {
            mMovie = savedInstanceState.getParcelable(IntentExtra.MOVIE);
        }

        //Set title at toolbar
        getActivity().setTitle(mMovie.getTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        //Bind data if possible
        if(mMovie != null){
            Picasso.with(getContext()).load(UrlUtil.GetMovieThumbnailUrl(mMovie, UrlUtil.ImageSize.W342)).into(((ImageView)view.findViewById(R.id.thumbnail)));
            ((TextView)view.findViewById(R.id.release_date)).setText(DateUtil.GetYear(mMovie.getReleaseDate()));
            ((TextView)view.findViewById(R.id.total_votes)).setText(String.format(getString(R.string.movie_details_votes), mMovie.getVoteAverage(), mMovie.getVoteCount()));
            ((TextView)view.findViewById(R.id.synopsis)).setText(mMovie.getOverview());
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IntentExtra.MOVIE, mMovie);
        super.onSaveInstanceState(outState);
    }

}
