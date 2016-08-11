package com.tinytinybites.popularmovies.app.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.adapter.MovieReviewsAdapter;
import com.tinytinybites.popularmovies.app.application.EApplication;
import com.tinytinybites.popularmovies.app.constant.IntentExtra;
import com.tinytinybites.popularmovies.app.model.Movie;
import com.tinytinybites.popularmovies.app.model.MovieReview;
import com.tinytinybites.popularmovies.app.task.RetrieveMovieReviews;


/**
 * Created by bundee on 8/11/16.
 */
public class MovieReviewsFragment extends Fragment implements RetrieveMovieReviews.FetchReviewsResponse {
    //Tag
    protected static final String TAG = MovieReviewsFragment.class.getCanonicalName();

    //Variables
    private Movie mMovie;
    private Unbinder mUnbinder;
    private MovieReviewsAdapter mAdapter;
    private RetrieveMovieReviews mRetrieveTask;

    //Bindings
    @BindView(R.id.general_message) TextView mGeneralMessage;
    @BindView(R.id.recyclerview) RecyclerView mRecylerView;

    /**
     * Static constructor
     * @param movie
     * @return
     */
    public static MovieReviewsFragment newInstance(Movie movie){
        MovieReviewsFragment fragment = new MovieReviewsFragment();
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
            if(savedInstanceState.containsKey(IntentExtra.REVIEWS)){
                if(savedInstanceState.getParcelableArrayList(IntentExtra.REVIEWS) != null){
                    ArrayList reviews = savedInstanceState.getParcelableArrayList(IntentExtra.REVIEWS);
                    if(reviews != null && mAdapter != null){
                        mAdapter.addAll(reviews);
                    }
                }
            }
            if(savedInstanceState.containsKey(IntentExtra.TRAILERS)){
                mAdapter.addAll(savedInstanceState.<MovieReview>getParcelableArrayList(IntentExtra.REVIEWS));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_reviews, container, false);

        //Butterknife
        mUnbinder = ButterKnife.bind(this, view);

        //Setup recycler view
        mRecylerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        //Setup adapter
        mAdapter = new MovieReviewsAdapter();

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
        outState.putParcelableArrayList(IntentExtra.REVIEWS, new ArrayList<>(mAdapter.getItems()));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mMovie != null &&
                mAdapter.getItemCount() == 0){
            retrieveMovieReviews();
        }
    }

    private Movie getMovie() {  return mMovie;}
    private void setMovie(Movie movie){ this.mMovie = movie;}

    private void showProgressBar(){ /*mProgressBar.setVisibility(View.VISIBLE);*/}
    private void hideProgressBar(){ /*mProgressBar.setVisibility(View.GONE);*/}

    /**
     * Function to start only 1 async task in any one time
     */
    private void retrieveMovieReviews(){
        if(mRetrieveTask != null){
            //Check that is its not null and its running, that it is the right sort type
            if(mRetrieveTask.getStatus() == AsyncTask.Status.RUNNING){
                //Its still running and its the same sort type we want. Let it keep going
                return;
            }
        }

        //Create new task and run
        mRetrieveTask = new RetrieveMovieReviews(this, mMovie.getId());
        mRetrieveTask.execute();
    }

    @Override
    public void OnFetchReviewsProgress() {
        //Reset adapter
        mAdapter.clearAll();

        showProgressBar();
    }

    @Override
    public void OnFetchReviewResponse(ArrayList<MovieReview> reviews) {
        if(reviews != null){
            mAdapter.addAll(reviews);

            if(reviews.isEmpty()){
                mGeneralMessage.setVisibility(View.VISIBLE);
                mGeneralMessage.setText("There are no reviews for this movie.");
            }else{
                mGeneralMessage.setVisibility(View.GONE);
            }
        }

        hideProgressBar();
    }

    @Override
    public void OnFetchReviewError(String error) {
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
