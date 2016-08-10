package com.tinytinybites.popularmovies.app.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.activity.MovieDetailsActivity;
import com.tinytinybites.popularmovies.app.adapter.DiscoveryMoviesAdapter;
import com.tinytinybites.popularmovies.app.application.EApplication;
import com.tinytinybites.popularmovies.app.constant.IntentExtra;
import com.tinytinybites.popularmovies.app.http.ApiUtil;
import com.tinytinybites.popularmovies.app.model.Movie;
import com.tinytinybites.popularmovies.app.task.RetrieveDiscoveryMovies;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesDiscoveryFragment extends Fragment implements DiscoveryMoviesAdapter.MovieClicked, RetrieveDiscoveryMovies.FetchDiscoveryResponse {
    //Tag
    protected final static String TAG = MoviesDiscoveryFragment.class.getCanonicalName();

    //Variables
    private RecyclerView mDiscoveryMoviesRecyclerView;
    private DiscoveryMoviesAdapter mAdapter;
    private ProgressBar mProgressBar;
    private RetrieveDiscoveryMovies mRetrieveTask;

    @Override
    public void onStart() {
        super.onStart();

        //Check for sort type
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EApplication.getInstance());
        String selectedDefault = preferences.getString(getString(R.string.pref_key_discovery_sorttype), getString(R.string.pref_value_sorttype_most_popular));
        if(selectedDefault.equalsIgnoreCase(getString(R.string.pref_value_sorttype_most_popular))) {
            retrieveMovies(ApiUtil.SortType.MOST_POPULAR);
        }else{
            retrieveMovies(ApiUtil.SortType.TOP_RATED);
        }
    }

    /**
     * Function to start only 1 async task in any one time with the right sort type
     * @param sortType
     */
    private void retrieveMovies(ApiUtil.SortType sortType){
        if(mRetrieveTask != null){
            //Check that is its not null and its running, that it is the right sort type
            if(mRetrieveTask.getStatus() == AsyncTask.Status.RUNNING){
                if(mRetrieveTask.getSortType() == sortType){
                    //Its still running and its the same sort type we want. Let it keep going
                    return;
                }else{
                    //Its a different type. Cancel this
                    mRetrieveTask.cancel(true);
                }
            }
        }

        //Create new task and run
        mRetrieveTask = new RetrieveDiscoveryMovies(this, sortType);
        mRetrieveTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate
        View view = inflater.inflate(R.layout.fragment_movies_discovery, container, false);

        //Bind elements
        mProgressBar = (ProgressBar) view.findViewById(R.id.load_progress);

        //Setup recycler view
        mDiscoveryMoviesRecyclerView = (RecyclerView) view.findViewById(R.id.discovery_recyclerview);
        mDiscoveryMoviesRecyclerView.setHasFixedSize(true);
        mDiscoveryMoviesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        //Setup adapter
        mAdapter = new DiscoveryMoviesAdapter(EApplication.getInstance(), this);

        //Bind adapter
        mDiscoveryMoviesRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onMovieSelected(Movie movie, ImageView view) {
        //Fire an explicit intent to movie details
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra(IntentExtra.MOVIE, movie);
        startActivity(intent);
    }

    private void showProgressBar(){ mProgressBar.setVisibility(View.VISIBLE);}
    private void hideProgressBar(){ mProgressBar.setVisibility(View.GONE);}

    @Override
    public void OnFetchDiscoveryResponse(ArrayList<Movie> movies) {
        if(movies != null){
            mAdapter.addAll(movies);
        }

        hideProgressBar();
    }

    @Override
    public void OnFetchDiscoveryProgress() {
        //Reset adapter
        mAdapter.clearAll();

        showProgressBar();
    }

    @Override
    public void OnFetchDiscoveryError(String error) {
        showError(error);
    }

    /**
     * Convenient function to show a toast for error messages
     * @param errorMessage
     */
    private void showError(final String errorMessage){
        //Ensure toast is running on ui thread
        if(getActivity() != null &&
                !getActivity().isFinishing()){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EApplication.getInstance(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
