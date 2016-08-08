package com.tinytinybites.popularmovies.app.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.activity.MovieDetailsActivity;
import com.tinytinybites.popularmovies.app.adapter.DiscoveryMoviesAdapter;
import com.tinytinybites.popularmovies.app.application.EApplication;
import com.tinytinybites.popularmovies.app.constant.IntentExtra;
import com.tinytinybites.popularmovies.app.http.ApiUtil;
import com.tinytinybites.popularmovies.app.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesDiscoveryFragment extends Fragment implements DiscoveryMoviesAdapter.MovieClicked {
    //Tag
    protected final static String TAG = MoviesDiscoveryFragment.class.getCanonicalName();

    //Variables
    private RecyclerView mDiscoveryMoviesRecyclerView;
    private DiscoveryMoviesAdapter mAdapter;
    private ProgressBar mProgressBar;

    @Override
    public void onStart() {
        super.onStart();

        //Check for sort type
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(EApplication.getInstance());
        String selectedDefault = preferences.getString(getString(R.string.pref_key_discovery_sorttype), getString(R.string.pref_value_sorttype_most_popular));
        if(selectedDefault.equalsIgnoreCase(getString(R.string.pref_value_sorttype_most_popular))) {
            (new RetrieveDiscoveryMovies()).execute(ApiUtil.SortType.MOST_POPULAR);
        }else{
            (new RetrieveDiscoveryMovies()).execute(ApiUtil.SortType.TOP_RATED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate
        View view = inflater.inflate(R.layout.fragment_movies_discovery, container, false);

        //Bind elements
        mProgressBar = (ProgressBar) view.findViewById(R.id.load_progress);

        //Setup recycler view
        mDiscoveryMoviesRecyclerView = (RecyclerView) view.findViewById(R.id.discovery_recyclerview);
        mDiscoveryMoviesRecyclerView.setHasFixedSize(true);
        mDiscoveryMoviesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        //Setup adapter
        mAdapter = new DiscoveryMoviesAdapter(EApplication.getInstance(), this);

        //Bind adapter
        mDiscoveryMoviesRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onMovieSelected(Movie movie, ImageView view) {
        //Fire an explicit intent to movie details
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra(IntentExtra.MOVIE, movie);
        startActivity(intent);
    }

    private void showProgressBar(){ mProgressBar.setVisibility(View.VISIBLE);}
    private void hideProgressBar(){ mProgressBar.setVisibility(View.GONE);}

    /**
     * Asynctask that retrieves movies for discovery
     */
    public class RetrieveDiscoveryMovies extends AsyncTask<ApiUtil.SortType, Void, ArrayList<Movie>>{

        @Override
        protected void onPreExecute() {
            //Reset adapter
            mAdapter.clearAll();

            showProgressBar();
        }

        @Override
        protected ArrayList<Movie> doInBackground(ApiUtil.SortType... params) {
            // Note: Code from gist: https://gist.github.com/anonymous/1c04bf2423579e9d2dcd
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String discoveryMoviesJsonStr = null;
            URL url = null;
            try{
                url = ApiUtil.GetDiscoveryMoviesURL(params[0]);
            } catch (MalformedURLException e) {
                //Failed to Generate URL
                showError(String.format(getString(R.string.movie_discovery_uri_generation_error), e.getMessage()));
                return null;
            }

            try {
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    discoveryMoviesJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    discoveryMoviesJsonStr = null;
                }
                discoveryMoviesJsonStr = buffer.toString();
            } catch (IOException e) {
                // If the code didn't successfully get the movies data, there's no point in attempting
                // to parse it. Show error
                showError(String.format(getString(R.string.movie_discovery_retrieve_error), e.getMessage()));
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {}
                }
            }

            //Parse json to get a list of movies object
            ArrayList<Movie> movies = new ArrayList<>();
            try {
                JSONObject returnResults = new JSONObject(discoveryMoviesJsonStr);

                //Get results array
                JSONArray resultsArray = returnResults.getJSONArray("results");
                for(int i = 0; i < resultsArray.length(); i++){
                    movies.add(new Movie(resultsArray.getJSONObject(i)));
                }
            } catch (JSONException e) {
                showError(String.format(getString(R.string.movie_discovery_parsing_error), e.getMessage()));
                return null;
            }

            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            //Check that we have an array coming back
            if(movies != null){
                mAdapter.addAll(movies);
            }

            hideProgressBar();
        }
    }

    /**
     * Convenient function to show a toast for error messages
     * @param errorMessage
     */
    private void showError(final String errorMessage){
        //Ensure toast is running on ui thread
        if(getActivity() != null &&
                !getActivity().isFinishing()){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EApplication.getInstance(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
