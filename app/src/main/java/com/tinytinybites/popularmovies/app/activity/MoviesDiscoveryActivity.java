package com.tinytinybites.popularmovies.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.constant.IntentExtra;
import com.tinytinybites.popularmovies.app.fragment.MovieDetailFragment;
import com.tinytinybites.popularmovies.app.fragment.MoviesDiscoveryFragment;
import com.tinytinybites.popularmovies.app.model.Movie;

public class MoviesDiscoveryActivity extends AppCompatActivity implements MoviesDiscoveryFragment.Callback{
    //TAg
    protected static final String TAG = MoviesDiscoveryActivity.class.getCanonicalName();

    //Variables
    private boolean mTwoPane;

    //Bindings
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_discovery);
        Log.e(TAG, "onCreate DiscoveryActivity!!!");

        //Bind
        ButterKnife.bind(this);

        //Setup toolbar
        setSupportActionBar(mToolbar);

        if (findViewById(R.id.movie_details_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;

            //Don't add detail fragment yet as we don't have anything selected/loaded from discovery. No pre-selections available as well
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movies_discovery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            MovieDetailFragment fragment = MovieDetailFragment.newInstance(movie);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, fragment, MovieDetailFragment.class.getCanonicalName())
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra(IntentExtra.MOVIE, movie);
            startActivity(intent);
        }
    }

    @Override
    public void onPreselectMovie(Movie movie) {
        //Only allow preselection of movies when using 2 pane
        if(mTwoPane){
            onItemSelected(movie);
        }
    }
}
