package com.tinytinybites.popularmovies.app.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.adapter.DiscoveryMoviesAdapter;
import com.tinytinybites.popularmovies.app.adapter.FavoritedMoviesAdapter;
import com.tinytinybites.popularmovies.app.application.EApplication;
import com.tinytinybites.popularmovies.app.data.LoaderType;
import com.tinytinybites.popularmovies.app.data.MoviesContract;
import com.tinytinybites.popularmovies.app.http.ApiUtil;
import com.tinytinybites.popularmovies.app.model.Movie;
import com.tinytinybites.popularmovies.app.task.RetrieveDiscoveryMovies;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesDiscoveryFragment extends Fragment implements DiscoveryMoviesAdapter.MovieClicked,
                                                            RetrieveDiscoveryMovies.FetchDiscoveryResponse,
                                                            FavoritedMoviesAdapter.MovieClicked,
                                                            LoaderManager.LoaderCallbacks<Cursor>{
    //Tag
    protected final static String TAG = MoviesDiscoveryFragment.class.getCanonicalName();

    //Variables
    @BindView(R.id.discovery_recyclerview) RecyclerView mDiscoveryMoviesRecyclerView;
    @BindView(R.id.load_progress) ProgressBar mProgressBar;
    private DiscoveryMoviesAdapter mAdapter;
    private FavoritedMoviesAdapter mFavoriteCursorAdapter;
    private RetrieveDiscoveryMovies mRetrieveTask;
    private Unbinder mUnbinder;
    private ApiUtil.SortType mCurrentSortType = ApiUtil.SortType.NONE;

    @Override
    public void onStart() {
        super.onStart();

        //Check for sort type
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EApplication.getInstance());
        String selectedDefault = preferences.getString(getString(R.string.pref_key_discovery_sorttype), getString(R.string.pref_value_sorttype_most_popular));
        if(selectedDefault.equalsIgnoreCase(getString(R.string.pref_value_sorttype_most_popular))) {
            retrieveMovies(ApiUtil.SortType.MOST_POPULAR);
        }else if(selectedDefault.equalsIgnoreCase(getString(R.string.pref_value_sorttype_highest_rated))) {
            retrieveMovies(ApiUtil.SortType.TOP_RATED);
        }else if(selectedDefault.equalsIgnoreCase(getString(R.string.pref_value_sorttype_favorites))){
            retrieveMovies(ApiUtil.SortType.FAVORITE);
        }
    }

    /**
     * Function to start only 1 async task in any one time with the right sort type
     * @param sortType
     */
    private void retrieveMovies(ApiUtil.SortType sortType){
        switch (sortType){
            case MOST_POPULAR:
            case TOP_RATED:{
                //We do not want to keep setting adapter to recycler view as it will reset positioning of items
                //So we check availibility of adapter and check the type it is
                if(mDiscoveryMoviesRecyclerView.getAdapter() == null ||
                        !(mDiscoveryMoviesRecyclerView.getAdapter() instanceof DiscoveryMoviesAdapter)){
                    mDiscoveryMoviesRecyclerView.setAdapter(mAdapter);
                }

                if(mCurrentSortType.equals(sortType)){
                    //Do nothing more.
                    return;
                }

                mCurrentSortType = sortType;
                setToolbarTitle();
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

                //Ensure loader has been killed off
                getActivity().getSupportLoaderManager().destroyLoader(LoaderType.FAVORITE_MOVIE_LOADER);

                //Create new task and run
                mRetrieveTask = new RetrieveDiscoveryMovies(this, sortType);
                mRetrieveTask.execute();
                break;
            }
            case FAVORITE:{
                //We do not want to keep setting adapter to recycler view as it will reset positioning of items
                //So we check availibility of adapter and check the type it is
                if(mDiscoveryMoviesRecyclerView.getAdapter() == null ||
                        !(mDiscoveryMoviesRecyclerView.getAdapter() instanceof FavoritedMoviesAdapter)){
                    mDiscoveryMoviesRecyclerView.setAdapter(mFavoriteCursorAdapter);
                }

                if(mCurrentSortType.equals(sortType)){
                    //Do nothing more
                    return;
                }

                //Restart loader
                mCurrentSortType = sortType;
                setToolbarTitle();
                getActivity().getSupportLoaderManager().restartLoader(LoaderType.FAVORITE_MOVIE_LOADER, null, this);

                return;
            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate
        View view = inflater.inflate(R.layout.fragment_movies_discovery, container, false);

        //For element binding
        mUnbinder = ButterKnife.bind(this, view);

        //Setup recycler view
        mDiscoveryMoviesRecyclerView.setHasFixedSize(true);
        mDiscoveryMoviesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        //Setup adapters. One for cursor the other normal
        mAdapter = new DiscoveryMoviesAdapter(EApplication.getInstance(), this);
        mFavoriteCursorAdapter = new FavoritedMoviesAdapter(EApplication.getInstance(), this, null);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onMovieSelected(Movie movie) {
        ((Callback)getActivity()).onItemSelected(movie);
    }

    /**
     * Set title of the toolbar
     */
    private void setToolbarTitle(){
        switch(mCurrentSortType){
            case MOST_POPULAR:{
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.pref_value_sorttype_most_popular);
                break;
            }
            case TOP_RATED:{
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.pref_value_sorttype_highest_rated);
                break;
            }
            case FAVORITE:{
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.pref_value_sorttype_favorites);
                break;
            }
            default:
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.activity_title_discovery);
                break;
        }

    }

    private void showProgressBar(){ mProgressBar.setVisibility(View.VISIBLE);}
    private void hideProgressBar(){ mProgressBar.setVisibility(View.GONE);}

    @Override
    public void OnFetchDiscoveryResponse(ArrayList<Movie> movies) {
        if(movies != null){
            mAdapter.addAll(movies);

            if(!movies.isEmpty()) {
                ((Callback)getActivity()).onPreselectMovie(movies.get(0));
            }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Listen to changes in the content
        data.setNotificationUri(getActivity().getContentResolver(), MoviesContract.MovieEntry.CONTENT_URI);

        mFavoriteCursorAdapter.swapCursor(data);

        (new Handler()).post(new Runnable() {
            @Override
            public void run() {
                if(mFavoriteCursorAdapter.getItemCount() > 0){
                    ((Callback)getActivity()).onPreselectMovie(mFavoriteCursorAdapter.getItem(0));
                }
            }
        });


        hideProgressBar();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoriteCursorAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     * Gist coming from: https://gist.github.com/udacityandroid/41f9e52a36e88388624d
     */
    public interface Callback {
        void onItemSelected(Movie movie);
        void onPreselectMovie(Movie movie);
    }
}
