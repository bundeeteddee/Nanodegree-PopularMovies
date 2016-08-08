package com.tinytinybites.popularmovies.app.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.adapter.DiscoveryMoviesAdapter;
import com.tinytinybites.popularmovies.app.model.Movie;


/**
 * Created by bundee on 8/4/16.
 */
public class DiscoveryMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //Variables
    private View mRoot;
    private TextView mTitle;
    private ImageView mThumbnail;
    private DiscoveryMoviesAdapter.MovieClicked mListener;

    public DiscoveryMovieViewHolder(View itemView, DiscoveryMoviesAdapter.MovieClicked listener) {
        super(itemView);

        mRoot = itemView;
        mListener = listener;
        if(mListener != null){
            getThumbnail().setOnClickListener(this);
        }
    }

    public ImageView getThumbnail(){
        if(mThumbnail == null){
            mThumbnail = (ImageView) mRoot.findViewById(R.id.thumbnail);
        }
        return mThumbnail;
    }

    public TextView getTitle(){
        if(mTitle == null){
            mTitle = (TextView) mRoot.findViewById(R.id.title);
        }
        return mTitle;
    }

    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onMovieSelected((Movie) v.getTag(), (ImageView) v);
        }
    }
}
