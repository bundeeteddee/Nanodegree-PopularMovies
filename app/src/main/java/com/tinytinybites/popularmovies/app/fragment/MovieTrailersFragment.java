package com.tinytinybites.popularmovies.app.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.adapter.MovieTrailersAdapter;
import com.tinytinybites.popularmovies.app.application.EApplication;
import com.tinytinybites.popularmovies.app.constant.IntentExtra;
import com.tinytinybites.popularmovies.app.http.ApiUtil;
import com.tinytinybites.popularmovies.app.model.Movie;
import com.tinytinybites.popularmovies.app.model.MovieTrailer;
import com.tinytinybites.popularmovies.app.task.MovieTrailersTask;


/**
 * Created by bundee on 8/11/16.
 */
public class MovieTrailersFragment extends Fragment implements MovieTrailersTask.FetchTrailersResponse, MovieTrailersAdapter.TrailerClicked {
    //Tag
    protected static final String TAG = MovieTrailersFragment.class.getCanonicalName();

    //Variables
    private Movie mMovie;
    private Unbinder mUnbinder;
    private MovieTrailersAdapter mAdapter;
    private MovieTrailersTask mRetrieveTask;

    //Bindings
    @BindView(R.id.general_message) TextView mGeneralMessage;
    @BindView(R.id.recyclerview) RecyclerView mRecylerView;

    /**
     * Static constructor
     * @param movie
     * @return
     */
    public static MovieTrailersFragment newInstance(Movie movie){
        MovieTrailersFragment fragment = new MovieTrailersFragment();

        //Supply arguments
        Bundle args = new Bundle();
        args.putParcelable(IntentExtra.MOVIE, movie);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_trailers, container, false);

        //Butterknife
        mUnbinder = ButterKnife.bind(this, view);

        //Butterknife
        mUnbinder = ButterKnife.bind(this, view);

        //Setup recycler view
        mRecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        //Setup adapter
        mAdapter = new MovieTrailersAdapter(this);

        //Bind adapter
        mRecylerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IntentExtra.MOVIE, mMovie);
        outState.putParcelableArrayList(IntentExtra.TRAILERS, new ArrayList<>(mAdapter.getItems()));
        super.onSaveInstanceState(outState);
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
            if(savedInstanceState.containsKey(IntentExtra.TRAILERS)){
                if(savedInstanceState.getParcelableArrayList(IntentExtra.TRAILERS) != null){
                    ArrayList trailers = savedInstanceState.getParcelableArrayList(IntentExtra.TRAILERS);
                    if(trailers != null && mAdapter != null){
                        mAdapter.addAll(trailers);
                    }
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mMovie != null &&
                mAdapter.getItemCount() == 0){
            retrieveMovieTrailers();
        }
    }

    private void retrieveMovieTrailers() {
        if(mRetrieveTask != null){
            //Check that is its not null and its running, that it is the right sort type
            if(mRetrieveTask.getStatus() == AsyncTask.Status.RUNNING){
                //Its still running and its the same sort type we want. Let it keep going
                return;
            }
        }

        //Create new task and run
        mRetrieveTask = new MovieTrailersTask(this, mMovie.getId());
        mRetrieveTask.execute();
    }

    private Movie getMovie() {  return mMovie;}
    private void setMovie(Movie movie){ this.mMovie = movie;}

    private void showProgressBar(){ /*mProgressBar.setVisibility(View.VISIBLE);*/}
    private void hideProgressBar(){ /*mProgressBar.setVisibility(View.GONE);*/}

    @Override
    public void OnFetchTrailersProgress() {
        //Reset adapter
        mAdapter.clearAll();
        showProgressBar();
    }

    @Override
    public void OnFetchTrailersResponse(ArrayList<MovieTrailer> trailers) {
        if(trailers != null){
            mAdapter.addAll(trailers);

            if(trailers.isEmpty()){
                mGeneralMessage.setVisibility(View.VISIBLE);
                mGeneralMessage.setText("There are no trailers for this movie.");
            }else{
                mGeneralMessage.setVisibility(View.GONE);
            }
        }

        hideProgressBar();
    }

    @Override
    public void OnFetchTrailersError(String error) {
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
    public void onTrailerSelected(MovieTrailer trailer, View view) {
        //If its a youtube site, we can generate the url
        if(trailer.isFromYoutube()) {
            try {
                URL youtubeLink = ApiUtil.GetYoutubeVideoURL(trailer.getKey());
                Log.e(TAG, youtubeLink.toExternalForm());
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink.toExternalForm())));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            //TODO: Other known sites?

        }
    }
}
