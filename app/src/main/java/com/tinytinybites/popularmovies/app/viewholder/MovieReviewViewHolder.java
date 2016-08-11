package com.tinytinybites.popularmovies.app.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinytinybites.popularmovies.app.R;


/**
 * Created by bundee on 8/11/16.
 */
public class MovieReviewViewHolder extends RecyclerView.ViewHolder{

    //Variables
    @BindView(R.id.author_name) public TextView mAuthorName;
    @BindView(R.id.review_content) public TextView mContent;

    public MovieReviewViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
