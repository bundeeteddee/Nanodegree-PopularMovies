package com.tinytinybites.popularmovies.app.util;

import com.tinytinybites.popularmovies.app.model.Movie;

/**
 * Created by bundee on 8/4/16.
 */
public final class UrlUtil {
    //Statics
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    //Size Enum: value coming from :
    //https://docs.google.com/document/d/1ZlN1fUsCSKuInLECcJkslIqvpKlP7jWL2TP9m6UiA6I/pub?embedded=true
    public enum ImageSize{
        W92("w92"),
        W154("w154"),
        W185("w185"),
        W342("w342"),
        W500("w500"),
        W780("w780"),
        ORIGINAL("original");

        private final String size;
        ImageSize(String size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return this.size;
        }
    }

    /**
     * Given a movie and size, return the url to get thumbnail
     * @param movie
     * @param size
     * @return
     */
    public static final String GetMovieThumbnailUrl(Movie movie, ImageSize size){
        StringBuilder builder = new StringBuilder();

        builder.append(IMAGE_BASE_URL);
        builder.append(size.toString());
        builder.append(movie.getPosterPath());

        return builder.toString();
    }

}
