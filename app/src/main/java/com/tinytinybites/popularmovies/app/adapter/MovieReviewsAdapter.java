package com.tinytinybites.popularmovies.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.model.MovieReview;
import com.tinytinybites.popularmovies.app.viewholder.MovieReviewViewHolder;

/**
 * Created by bundee on 8/11/16.
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewViewHolder> {
    //Tag
    protected static final String TAG = MovieReviewsAdapter.class.getCanonicalName();

    //Variables
    private List<MovieReview> mReviews;

    /**
     * Constructor
     */
    public MovieReviewsAdapter() {
        mReviews = new ArrayList<>();
    }

    @Override
    public MovieReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_movie_review, parent, false);
        return new MovieReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
        //Get data for position
        MovieReview review = mReviews.get(position);

        //Bind values to view holder
        holder.mAuthorName.setText(review.getAuthorName());
        holder.mContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public void addAll(List<MovieReview> reviews){
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public List getItems(){    return mReviews;}

    public void clearAll(){
        mReviews.clear();
        notifyDataSetChanged();
    }
}
