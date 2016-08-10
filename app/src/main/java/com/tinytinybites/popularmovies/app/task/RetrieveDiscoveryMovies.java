package com.tinytinybites.popularmovies.app.task;

/**
 * Created by bundee on 8/10/16.
 */

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.application.EApplication;
import com.tinytinybites.popularmovies.app.http.ApiUtil;
import com.tinytinybites.popularmovies.app.model.Movie;

/**
 * Asynctask that retrieves movies for discovery
 */
public class RetrieveDiscoveryMovies extends AsyncTask<Void, Void, ArrayList<Movie>> {
    protected static final String TAG = RetrieveDiscoveryMovies.class.getCanonicalName();

    //Variables
    private ApiUtil.SortType mSortType;
    private WeakReference<FetchDiscoveryResponse> mDelegate;

    /**
     * Constructor
     * @param delegate
     * @param sortType
     */
    public RetrieveDiscoveryMovies(FetchDiscoveryResponse delegate,
                                   ApiUtil.SortType sortType) {
        this.mDelegate = new WeakReference<>(delegate);
        this.mSortType = sortType;
    }

    public ApiUtil.SortType getSortType() {
        return mSortType;
    }

    @Override
    protected void onPreExecute() {
        if(mDelegate.get() != null){
            mDelegate.get().OnFetchDiscoveryProgress();
        }
    }

    @Override
    protected ArrayList<Movie> doInBackground(Void... params) {
        // Note: Code from gist: https://gist.github.com/anonymous/1c04bf2423579e9d2dcd
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String discoveryMoviesJsonStr = null;
        URL url = null;
        try{
            url = ApiUtil.GetDiscoveryMoviesURL(mSortType);
        } catch (MalformedURLException e) {
            //Failed to Generate URL
            if(mDelegate.get() != null){
                mDelegate.get().OnFetchDiscoveryError(String.format(EApplication.getInstance().getString(R.string.movie_discovery_uri_generation_error), e.getMessage()));
            }
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
            if(mDelegate.get() != null){
                mDelegate.get().OnFetchDiscoveryError(String.format(EApplication.getInstance().getString(R.string.movie_discovery_retrieve_error), e.getMessage()));
            }
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
            if(mDelegate.get() != null){
                mDelegate.get().OnFetchDiscoveryError(String.format(EApplication.getInstance().getString(R.string.movie_discovery_parsing_error), e.getMessage()));
            }
            return null;
        }

        return movies;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        if(mDelegate.get() != null){
            mDelegate.get().OnFetchDiscoveryResponse(movies);
        }
    }

    public interface FetchDiscoveryResponse{
        void OnFetchDiscoveryProgress();
        void OnFetchDiscoveryResponse(ArrayList<Movie> movies);
        void OnFetchDiscoveryError(String error);
    }
}