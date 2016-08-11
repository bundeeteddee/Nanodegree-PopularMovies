package com.tinytinybites.popularmovies.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.model.MovieTrailer;
import com.tinytinybites.popularmovies.app.viewholder.MovieTrailerViewHolder;


/**
 * Created by bundee on 8/11/16.
 */
public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailerViewHolder>{
    //Tag
    protected static final String TAG = MovieTrailersAdapter.class.getCanonicalName();

    //Variables
    private List<MovieTrailer> mTrailers;
    private TrailerClicked mListener;

    /**
     * Constructor
     */
    public MovieTrailersAdapter(TrailerClicked listener) {
        mTrailers = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public MovieTrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_movie_trailer, parent, false);
        return new MovieTrailerViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(MovieTrailerViewHolder holder, int position) {
        //Get data for position
        MovieTrailer trailer = mTrailers.get(position);
        holder.mCardView.setTag(trailer);

        //Bind values to view holder
        holder.mName.setText(trailer.getName());
        holder.mSite.setText(trailer.getSite());
        holder.mType.setText(trailer.getType());
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public void addAll(List<MovieTrailer> trailers){
        mTrailers.clear();
        mTrailers.addAll(trailers);
        notifyDataSetChanged();
    }

    public List getItems(){    return mTrailers;}

    public void clearAll(){
        mTrailers.clear();
        notifyDataSetChanged();
    }

    public interface TrailerClicked{
        void onTrailerSelected(MovieTrailer trailer, View view);
    }
}
