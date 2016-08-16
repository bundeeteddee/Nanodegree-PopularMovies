package com.tinytinybites.popularmovies.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.constant.IntentExtra;
import com.tinytinybites.popularmovies.app.fragment.MovieDetailFragment;
import com.tinytinybites.popularmovies.app.http.ApiUtil;
import com.tinytinybites.popularmovies.app.model.Movie;

public class MovieDetailsActivity extends AppCompatActivity{
    //Tag
    protected static final String TAG = MovieDetailsActivity.class.getCanonicalName();

    //Variables
    private Movie mMovie;
    private ShareActionProvider mShareActionProvider;

    //Bindings
    @BindView(R.id.toolbar) Toolbar mToolbar;

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

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, MovieDetailFragment.newInstance(mMovie))
                    .commit();
        }else {
            mMovie = savedInstanceState.getParcelable(IntentExtra.MOVIE);
        }

        //Butterknife
        ButterKnife.bind(this);

        //Setup toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Set title at toolbar
        setTitle(mMovie.getTitle());
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
}
