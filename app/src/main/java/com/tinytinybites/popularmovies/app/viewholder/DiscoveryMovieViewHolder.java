package com.tinytinybites.popularmovies.app.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.adapter.DiscoveryMoviesAdapter;
import com.tinytinybites.popularmovies.app.model.Movie;


/**
 * Created by bundee on 8/4/16.
 */
public class DiscoveryMovieViewHolder extends RecyclerView.ViewHolder{

    //Variables
    @BindView(R.id.title) public TextView mTitle;
    @BindView(R.id.thumbnail) public ImageView mThumbnail;
    private DiscoveryMoviesAdapter.MovieClicked mListener;

    public DiscoveryMovieViewHolder(View itemView, DiscoveryMoviesAdapter.MovieClicked listener) {
        super(itemView);

        mListener = listener;

        ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.thumbnail)
    public void thumbnailClicked(View v) {
        if(mListener != null){
            mListener.onMovieSelected((Movie) v.getTag(), (ImageView) v);
        }
    }
}
