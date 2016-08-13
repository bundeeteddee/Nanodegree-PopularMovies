package com.tinytinybites.popularmovies.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.data.MoviesContract;
import com.tinytinybites.popularmovies.app.model.Movie;
import com.tinytinybites.popularmovies.app.util.UrlUtil;
import com.tinytinybites.popularmovies.app.viewholder.FavoriteMovieViewHolder;

/**
 * Created by bundee on 8/12/16.
 */
public class FavoritedMoviesAdapter extends CursorRecyclerViewAdapter<FavoriteMovieViewHolder> {
    //Tag
    protected static final String TAG = FavoritedMoviesAdapter.class.getCanonicalName();

    //Variables
    private Context mContext;
    private MovieClicked mListener;

    /**
     * Constructor
     * @param context
     */
    public FavoritedMoviesAdapter(Context context, @Nullable MovieClicked listener, Cursor cursor) {
        super(context, cursor);
        this.mContext = context;
        mListener = listener;
    }

    @Override
    public FavoriteMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.griditem_discover_movie, parent, false);

        //Give it a rough height of half the recyclerview
        int height = parent.getMeasuredHeight() / 2;
        RecyclerView.LayoutParams lm = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, height);
        view.setLayoutParams(lm);

        return new FavoriteMovieViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(FavoriteMovieViewHolder holder, Cursor cursor) {
        //Construct a minimal movie object
        Movie movie = new Movie(cursor.getInt(cursor.getColumnIndexOrThrow(MoviesContract.MovieEntry._ID)),
                                cursor.getString(cursor.getColumnIndexOrThrow(MoviesContract.MovieEntry.COLUMN_TITLE)),
                                cursor.getString(cursor.getColumnIndexOrThrow(MoviesContract.MovieEntry.COLUMN_SYNOPSIS)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE)),
                                cursor.getDouble(cursor.getColumnIndexOrThrow(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT)),
                                cursor.getString(cursor.getColumnIndexOrThrow(MoviesContract.MovieEntry.COLUMN_POSTER_PATH)));
        holder.mThumbnail.setTag(movie);

        //Bind values to view holder
        String imageUrl = UrlUtil.GetMovieThumbnailUrl(movie, UrlUtil.ImageSize.W342);
        if(imageUrl != null &&
                imageUrl.length() > 0) {
            Picasso.with(mContext)
                    .load(imageUrl)
                    .into(holder.mThumbnail);
        }
        holder.mTitle.setText(movie.getTitle());
    }

    public interface MovieClicked{
        void onMovieSelected(Movie movie, ImageView view);
    }
}
