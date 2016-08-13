package com.tinytinybites.popularmovies.app.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.adapter.MovieDetailsFragmentPagerAdapter;
import com.tinytinybites.popularmovies.app.application.EApplication;
import com.tinytinybites.popularmovies.app.constant.IntentExtra;
import com.tinytinybites.popularmovies.app.data.LoaderType;
import com.tinytinybites.popularmovies.app.data.MoviesContract;
import com.tinytinybites.popularmovies.app.http.ApiUtil;
import com.tinytinybites.popularmovies.app.model.Movie;
import com.tinytinybites.popularmovies.app.transformation.VignetteFilterTransformation;
import com.tinytinybites.popularmovies.app.util.ImageUtil;
import com.tinytinybites.popularmovies.app.util.UrlUtil;

public class MovieDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    //Tag
    protected static final String TAG = MovieDetailsActivity.class.getCanonicalName();

    //Variables
    private Movie mMovie;
    private MovieDetailsFragmentPagerAdapter mPageAdapter;
    private Cursor mCursor;
    private ShareActionProvider mShareActionProvider;

    //Bindings
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.thumbnail) ImageView mThumbnail;
    @BindView(R.id.favorite) FloatingActionButton mFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        //Get parcelable from intent
        if(savedInstanceState == null ||
                !savedInstanceState.containsKey(IntentExtra.MOVIE)) {
            //nothing from saved instance, attempt to get it from intent bundle
            Bundle extras = getIntent().getExtras();
            if(extras != null){
                if(extras.containsKey(IntentExtra.MOVIE)){
                    mMovie = extras.getParcelable(IntentExtra.MOVIE);
                }
            }

            //TODO: What to do when mMovie is null?
        }else {
            mMovie = savedInstanceState.getParcelable(IntentExtra.MOVIE);
        }

        //Set title at toolbar
        setTitle(mMovie.getTitle());

        //Butterknife
        ButterKnife.bind(this);

        //Setup toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup tabs
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.movie_details_synopsis_title)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.movie_details_reviews_title)));
        mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.movie_details_trailers_title)));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mPageAdapter = new MovieDetailsFragmentPagerAdapter(getSupportFragmentManager(), mMovie);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        setupAppBar();

        //Init loader
        getSupportLoaderManager().initLoader(LoaderType.MOVIE_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(IntentExtra.MOVIE, mMovie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        //Set share Intent
        setShareIntent();

        return true;
    }

    /**
     * Update share intent
     */
    private void setShareIntent() {
        if (mShareActionProvider != null) {
            if(mMovie != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                //shareIntent.putExtra(Intent.EXTRA_TITLE, mMovie.getTitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, ApiUtil.GetPublicMovieUrl(mMovie.getId()));
                shareIntent.setType("text/plain");

                //Set
                mShareActionProvider.setShareIntent(shareIntent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupAppBar(){
        if(mMovie != null){
            //Calculate sizing
            int desiredImageHeight = (ImageUtil.GetDeviceHeight(EApplication.getInstance()) - ImageUtil.GetStatusBarHeight())*3/4;
            int desiredImageWidth = ImageUtil.GetDeviceWidth(EApplication.getInstance());

            //Set the height for thumbnail image
            CollapsingToolbarLayout.LayoutParams lp = (CollapsingToolbarLayout.LayoutParams) mThumbnail.getLayoutParams();
            lp.height = desiredImageHeight;
            mThumbnail.setLayoutParams(lp);

            //Add transformation to add vignette so toolbar and title stands out more
            Picasso.with(this)
                    .load(UrlUtil.GetMovieThumbnailUrl(mMovie, UrlUtil.ImageSize.W342))
                    .resize(desiredImageWidth, desiredImageHeight)
                    .centerCrop()
                    .transform(new VignetteFilterTransformation(this, new PointF(0.5f, 0.5f),
                            new float[] { 0.0f, 0.0f, 0.0f }, 0f, 0.75f))
                    .into(mThumbnail);
        }
    }

    @OnClick(R.id.favorite)
    public void favoriteClicked(){
        //Check current state. Remove from content provider if cursor has data
        if(mCursor != null && mCursor.getCount() > 0) {
            //Need to delete
            int rowsDeleted = getContentResolver().delete(MoviesContract.MovieEntry.buildMoviesUri(mMovie.getId()), null, null);

            if (rowsDeleted > 0) {
                //Update button state
                mFavorite.setImageResource(R.drawable.ic_favorite);
            }
        }else{
            //Need to insert
            getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, MoviesContract.MovieEntry.buildMovieContentValues(mMovie));

            //Update button state
            mFavorite.setImageResource(R.drawable.ic_favorited);

        }
    }

    // Attach loader to our movies database query
    // run when loader is initialized
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
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
            mCursor.setNotificationUri(getContentResolver(), MoviesContract.MovieEntry.CONTENT_URI);

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
