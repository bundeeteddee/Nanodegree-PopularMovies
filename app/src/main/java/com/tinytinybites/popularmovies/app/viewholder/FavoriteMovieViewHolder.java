package com.tinytinybites.popularmovies.app.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.adapter.FavoritedMoviesAdapter;
import com.tinytinybites.popularmovies.app.model.Movie;


/**
 * Created by bundee on 8/12/16.
 */
public class FavoriteMovieViewHolder extends RecyclerView.ViewHolder{

    //Variables
    @BindView(R.id.title) public TextView mTitle;
    @BindView(R.id.thumbnail) public ImageView mThumbnail;
    private FavoritedMoviesAdapter.MovieClicked mListener;

    public FavoriteMovieViewHolder(View itemView, FavoritedMoviesAdapter.MovieClicked listener) {
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
