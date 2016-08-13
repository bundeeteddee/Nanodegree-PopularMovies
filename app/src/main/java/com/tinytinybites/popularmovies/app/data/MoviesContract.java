package com.tinytinybites.popularmovies.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.tinytinybites.popularmovies.app.model.Movie;

/**
 * Created by bundee on 8/12/16.
 * Reference: https://github.com/udacity/android-content-provider
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.tinytinybites.popularmovies.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {
        // table name
        public static final String TABLE_NAME = "movie";
        // columns
        public static final String _ID = "_id";
        public static final String COLUMN_TITLE = "title";//String
        public static final String COLUMN_SYNOPSIS = "synopsis";//String
        public static final String COLUMN_RELEASE_DATE = "release_date";//long
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";//double/float
        public static final String COLUMN_VOTE_COUNT = "vote_count";//int
        public static final String COLUMN_POSTER_PATH = "poster_path";//string

        // create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        // create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;
        // create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        // for building URIs on insertion
        public static Uri buildMoviesUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * Given a movie object, generate content values object
         * @param movie
         * @return
         */
        public static ContentValues buildMovieContentValues(Movie movie){
            ContentValues movieContentValue = new ContentValues();
            movieContentValue.put(MovieEntry._ID, movie.getId());
            movieContentValue.put(MovieEntry.COLUMN_TITLE, movie.getTitle());
            movieContentValue.put(MovieEntry.COLUMN_SYNOPSIS, movie.getOverview());
            movieContentValue.put(MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate().getTime());
            movieContentValue.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            movieContentValue.put(MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
            movieContentValue.put(MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
            return movieContentValue;
        }
    }


}
