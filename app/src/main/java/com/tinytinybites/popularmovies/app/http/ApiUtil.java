package com.tinytinybites.popularmovies.app.http;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.net.MalformedURLException;
import java.net.URL;
import com.tinytinybites.popularmovies.app.R;
import com.tinytinybites.popularmovies.app.application.EApplication;

/**
 * Created by bundee on 8/4/16.
 */
public class ApiUtil {
    //Tag
    protected static final String TAG = ApiUtil.class.getCanonicalName();

    //Base Url
    public static final String BASE_API_URL = "http://api.themoviedb.org/3/movie/";
    public static final String BASE_YOUTUBE_URL = "https://www.youtube.com/";

    //Path
    public static final String PATH_TRAILERS = "videos";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_YOUTUBE_WATCH = "watch";

    //Param Key
    public static final String PARAM_KEY_API_KEY = "api_key";
    public static final String PARAM_YOUTUBE_KEY_VIDEO = "v";

    //Sort Type enum
    public enum SortType{
        MOST_POPULAR("popular"),
        TOP_RATED("top_rated");

        private final String mKey;
        SortType(String mKey) {
            this.mKey = mKey;
        }

        @Override
        public String toString() {
            return mKey;
        }
    }

    /**
     * Get the discovery movies url based on the sort type needed
     * @param sortType
     * @return
     * @throws MalformedURLException
     */
    public static URL GetDiscoveryMoviesURL(@NonNull SortType sortType) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendPath(sortType.toString())
                .appendQueryParameter(PARAM_KEY_API_KEY, EApplication.getInstance().getString(R.string.moviedb_api_key))
                .build();

        return new URL(builtUri.toString());
    }

    /**
     * Get available trailers given a movie id
     * @param movieId
     * @return
     * @throws MalformedURLException
     */
    public static URL GetMovieTrailersURL(@NonNull int movieId) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(PATH_TRAILERS)
                .appendQueryParameter(PARAM_KEY_API_KEY, EApplication.getInstance().getString(R.string.moviedb_api_key))
                .build();

        return new URL(builtUri.toString());
    }

    /**
     * Get available movie reviews given a movie id
     * @param movieId
     * @return
     * @throws MalformedURLException
     */
    public static URL GetMovieReviewsURL(@NonNull int movieId) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(PATH_REVIEWS)
                .appendQueryParameter(PARAM_KEY_API_KEY, EApplication.getInstance().getString(R.string.moviedb_api_key))
                .build();

        return new URL(builtUri.toString());
    }

    /**
     * Get the url to watch a video in Youtube
     * Example: https://www.youtube.com/watch?v=JsbG97hO6lA
     * @param youtubeId
     * @return
     * @throws MalformedURLException
     */
    public static URL GetYoutubeVideoURL(String youtubeId) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_YOUTUBE_URL).buildUpon()
                .appendPath(PATH_YOUTUBE_WATCH)
                .appendQueryParameter(PARAM_YOUTUBE_KEY_VIDEO, youtubeId)
                .build();

        return new URL(builtUri.toString());

    }
}
