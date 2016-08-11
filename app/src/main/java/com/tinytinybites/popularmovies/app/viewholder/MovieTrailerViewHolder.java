package com.tinytinybites.popularmovies.app.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.adapter.MovieTrailersAdapter;
import com.tinytinybites.popularmovies.app.model.MovieTrailer;


/**
 * Created by bundee on 8/11/16.
 */
public class MovieTrailerViewHolder extends RecyclerView.ViewHolder{

    //Variables
    private MovieTrailersAdapter.TrailerClicked mListener;

    //Bindings
    @BindView(R.id.trailer_card) public CardView mCardView;
    @BindView(R.id.name) public TextView mName;
    @BindView(R.id.site) public TextView mSite;
    @BindView(R.id.type) public TextView mType;
    @BindView(R.id.play_icon) public ImageView mPlayIcon;

    public MovieTrailerViewHolder(View itemView, MovieTrailersAdapter.TrailerClicked listener) {
        super(itemView);
        this.mListener = listener;
        ButterKnife.bind(this, itemView);
    }

    @OnClick(R.id.trailer_card)
    public void trailerPlay(View v) {
        if(mListener != null){
            mListener.onTrailerSelected((MovieTrailer) v.getTag(), v);
        }
    }
}
