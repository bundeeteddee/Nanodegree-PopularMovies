package com.tinytinybites.popularmovies.app.task;

/**
 * Created by bundee on 8/11/16.
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
import com.tinytinybites.popularmovies.app.model.MovieReview;

/**
 * Asynctask that retrieves movie reviews
 */
public class RetrieveMovieReviews extends AsyncTask<Void, Void, ArrayList<MovieReview>> {
    protected static final String TAG = RetrieveMovieReviews.class.getCanonicalName();

    //Variables
    private int mMovieId;
    private WeakReference<FetchReviewsResponse> mDelegate;

    /**
     * Constructor
     * @param delegate
     * @param movieId
     */
    public RetrieveMovieReviews(FetchReviewsResponse delegate,
                                int movieId) {
        this.mDelegate = new WeakReference<>(delegate);
        this.mMovieId = movieId;
    }

    public int getMovieId() {
        return mMovieId;
    }

    @Override
    protected void onPreExecute() {
        if(mDelegate.get() != null){
            mDelegate.get().OnFetchReviewsProgress();
        }
    }

    @Override
    protected ArrayList<MovieReview> doInBackground(Void... params) {
        // Note: Code from gist: https://gist.github.com/anonymous/1c04bf2423579e9d2dcd
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieReviewsJsonStr = null;
        URL url = null;
        try{
            url = ApiUtil.GetMovieReviewsURL(mMovieId);
        } catch (MalformedURLException e) {
            //Failed to Generate URL
            if(mDelegate.get() != null){
                mDelegate.get().OnFetchReviewError(String.format(EApplication.getInstance().getString(R.string.movie_reviews_uri_generation_error), e.getMessage()));
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
                movieReviewsJsonStr = null;
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
                movieReviewsJsonStr = null;
            }
            movieReviewsJsonStr = buffer.toString();
        } catch (IOException e) {
            // If the code didn't successfully get the movies data, there's no point in attempting
            // to parse it. Show error
            if(mDelegate.get() != null){
                mDelegate.get().OnFetchReviewError(String.format(EApplication.getInstance().getString(R.string.movie_reviews_retrieve_error), e.getMessage()));
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

        //Parse json to get a list of movie review objects
        ArrayList<MovieReview> reviews = new ArrayList<>();
        try {
            JSONObject returnResults = new JSONObject(movieReviewsJsonStr);

            //Get results array
            JSONArray resultsArray = returnResults.getJSONArray("results");
            for(int i = 0; i < resultsArray.length(); i++){
                reviews.add(new MovieReview(resultsArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            if(mDelegate.get() != null){
                mDelegate.get().OnFetchReviewError(String.format(EApplication.getInstance().getString(R.string.movie_reviews_parsing_error), e.getMessage()));
            }
            return null;
        }

        return reviews;
    }

    @Override
    protected void onPostExecute(ArrayList<MovieReview> reviews) {
        if(mDelegate.get() != null){
            mDelegate.get().OnFetchReviewResponse(reviews);
        }
    }

    public interface FetchReviewsResponse{
        void OnFetchReviewsProgress();
        void OnFetchReviewResponse(ArrayList<MovieReview> reviews);
        void OnFetchReviewError(String error);
    }
}