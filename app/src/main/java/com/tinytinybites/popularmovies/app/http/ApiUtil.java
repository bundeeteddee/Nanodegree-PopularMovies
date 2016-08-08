package com.tinytinybites.popularmovies.app.http;

import android.net.Uri;

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

    //Param Key
    public static final String PARAM_KEY_API_KEY = "api_key";

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
     * Get the dicovery movies url based on the sort type needed
     * @param sortType
     * @return
     * @throws MalformedURLException
     */
    public static URL GetDiscoveryMoviesURL(SortType sortType) throws MalformedURLException {
        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendPath(sortType.toString())
                .appendQueryParameter(PARAM_KEY_API_KEY, EApplication.getInstance().getString(R.string.moviedb_api_key))
                .build();

        return new URL(builtUri.toString());
    }

}
