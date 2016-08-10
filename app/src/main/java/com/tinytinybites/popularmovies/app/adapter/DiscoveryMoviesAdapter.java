package com.tinytinybites.popularmovies.app.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import com.squareup.picasso.Picasso;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.model.Movie;
import com.tinytinybites.popularmovies.app.util.UrlUtil;
import com.tinytinybites.popularmovies.app.viewholder.DiscoveryMovieViewHolder;

/**
 * Created by bundee on 8/4/16.
 */
public class DiscoveryMoviesAdapter extends RecyclerView.Adapter<DiscoveryMovieViewHolder> {
    //Tag
    protected static final String TAG = DiscoveryMoviesAdapter.class.getCanonicalName();

    //Variables
    private Context mContext;
    private List<Movie> mMovies;
    private MovieClicked mListener;

    /**
     * Constructor
     * @param context
     */
    public DiscoveryMoviesAdapter(Context context, @Nullable MovieClicked listener) {
        this.mContext = context;
        mMovies = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public DiscoveryMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.griditem_discover_movie, parent, false);

        //Give it a rough height of half the recyclerview
        int height = parent.getMeasuredHeight() / 2;
        RecyclerView.LayoutParams lm = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, height);
        view.setLayoutParams(lm);

        return new DiscoveryMovieViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(DiscoveryMovieViewHolder holder, int position) {
        //Get data for position
        Movie movie = mMovies.get(position);
        holder.getThumbnail().setTag(movie);

        //Bind values to view holder
        String imageUrl = UrlUtil.GetMovieThumbnailUrl(movie, UrlUtil.ImageSize.W342);
        if(imageUrl != null &&
                imageUrl.length() > 0) {
            Picasso.with(mContext)
                    .load(imageUrl)
                    .into(holder.getThumbnail());
        }
        holder.getTitle().setText(movie.getTitle());
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void addAll(List<Movie> movies){
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    public void clearAll(){
        mMovies.clear();
        notifyDataSetChanged();
    }

    public interface MovieClicked{
        void onMovieSelected(Movie movie, ImageView view);
    }
}
