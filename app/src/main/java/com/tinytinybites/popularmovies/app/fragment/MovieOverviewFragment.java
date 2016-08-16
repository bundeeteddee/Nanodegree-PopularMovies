package com.tinytinybites.popularmovies.app.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.squareup.picasso.Picasso;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.constant.IntentExtra;
import com.tinytinybites.popularmovies.app.data.LoaderType;
import com.tinytinybites.popularmovies.app.data.MoviesContract;
import com.tinytinybites.popularmovies.app.model.Movie;
import com.tinytinybites.popularmovies.app.util.DateUtil;
import com.tinytinybites.popularmovies.app.util.UrlUtil;

/**
 * Created by bundee on 8/11/16.
 */
public class MovieOverviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    //Tag
    protected static final String TAG = MovieOverviewFragment.class.getCanonicalName();

    //Variables
    private Movie mMovie;
    private Unbinder mUnbinder;
    private Cursor mCursor;

    //Bindings
    @BindView(R.id.synopsis) TextView mSynopsis;
    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.release_date) TextView mReleaseDate;
    @BindView(R.id.votes) TextView mVotes;
    @BindView(R.id.thumbnail) ImageView mThumbnail;
    @BindView(R.id.favorite) ImageView mFavorite;

    /**
     * Static constructor
     * @param movie
     * @return
     */
    public static MovieOverviewFragment newInstance(Movie movie){
        MovieOverviewFragment fragment = new MovieOverviewFragment();

        //Supply arguments
        Bundle args = new Bundle();
        args.putParcelable(IntentExtra.MOVIE, movie);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(IntentExtra.MOVIE);
        }else if(savedInstanceState == null ||
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
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IntentExtra.MOVIE, mMovie);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_overview, container, false);

        //Butterknife
        mUnbinder = ButterKnife.bind(this, view);

        //Set data
        if(mMovie != null) {
            mSynopsis.setText(mMovie.getOverview());
            mTitle.setText(mMovie.getTitle());
            mReleaseDate.setText(DateUtil.GetYear(mMovie.getReleaseDate()));
            mVotes.setText(String.format(getString(R.string.movie_details_votes), mMovie.getVoteAverage(), mMovie.getVoteCount()));

            //Add transformation to add vignette so toolbar and title stands out more
            Picasso.with(getActivity())
                    .load(UrlUtil.GetMovieThumbnailUrl(mMovie, UrlUtil.ImageSize.W342))
                    /*.transform(new VignetteFilterTransformation(getActivity(), new PointF(0.5f, 0.5f),
                            new float[] { 0.0f, 0.0f, 0.0f }, 0f, 0.75f))*/
                    .into(mThumbnail);
        }

        //Init loader
        mFavorite.setVisibility(View.GONE);
        getActivity().getSupportLoaderManager().restartLoader(LoaderType.MOVIE_LOADER, null, this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private Movie getMovie() {  return mMovie;}
    private void setMovie(Movie movie){ this.mMovie = movie;}

    @OnClick(R.id.favorite)
    public void favoriteClicked(){
        //Check current state. Remove from content provider if cursor has data
        if(mCursor != null && mCursor.getCount() > 0) {
            //Need to delete
            int rowsDeleted = getActivity().getContentResolver().delete(MoviesContract.MovieEntry.buildMoviesUri(mMovie.getId()), null, null);

            if (rowsDeleted > 0) {
                //Update button state
                mFavorite.setImageResource(R.drawable.ic_favorite);
            }
        }else{
            //Need to insert
            getActivity().getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, MoviesContract.MovieEntry.buildMovieContentValues(mMovie));

            //Update button state
            mFavorite.setImageResource(R.drawable.ic_favorited);
        }

        //Notify content provider
        getActivity().getContentResolver().notifyChange(MoviesContract.MovieEntry.CONTENT_URI, null);
    }

    // Attach loader to our movies database query
    // run when loader is initialized
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.buildMoviesUri(mMovie.getId()),
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;

        //Check on data
        if(mCursor != null) {
            if(getActivity() != null &&
                    !getActivity().isFinishing()) {
                mCursor.setNotificationUri(getActivity().getContentResolver(), MoviesContract.MovieEntry.CONTENT_URI);
            }

            mFavorite.setVisibility(View.VISIBLE);
            if(mCursor.getCount() > 0){
                mCursor.moveToFirst();

                //We have it in db. Update ui
                mFavorite.setImageResource(R.drawable.ic_favorited);
            }else{
                mFavorite.setImageResource(R.drawable.ic_favorite);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
