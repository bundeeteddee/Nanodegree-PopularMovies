package com.tinytinybites.popularmovies.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.tinytinybites.popularmovies.app.util.DateUtil;

/**
 * Created by bundee on 8/4/16.
 */
public class Movie implements Parcelable{

    //Keys from results
    public static final String KEY_ADULT = "adult";
    public static final String KEY_BACKDROP_PATH = "backdrop_path";
    public static final String KEY_GENRE_IDS = "genre_ids";
    public static final String KEY_ID = "id";
    public static final String KEY_ORIGINAL_LANGUAGE = "original_language";
    public static final String KEY_ORIGINAL_TITLE = "original_title";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_RELEASE_DATE = "release_date";
    public static final String KEY_POSTER_PATH = "poster_path";
    public static final String KEY_POPULARITY = "popularity";
    public static final String KEY_TITLE = "title";
    public static final String KEY_VIDEO = "video";
    public static final String KEY_VOTE_AVERAGE = "vote_average";
    public static final String KEY_VOTE_COUNT = "vote_count";


    //Variables
    private boolean mIsAdult;
    private String mBackdropPath;
    private List<Integer> mGenreIds;
    private int mId;
    private String mOriginalLanguage;
    private String mOriginalTitle;
    private String mTitle;
    private String mOverview;
    private Date mReleaseDate;
    private String mPosterPath;
    private double mPopularity;
    private boolean mHasVideo;
    private double mVoteAverage;
    private int mVoteCount;

    /**
     * Minimal constructor to reconstruct a movie from sql lite
     * @param id
     * @param title
     * @param synopsis
     * @param releaseDate
     * @param voteAverage
     * @param totlaCount
     * @param posterPath
     */
    public Movie(int id,
                 String title,
                 String synopsis,
                 long releaseDate,
                 double voteAverage,
                 int totlaCount,
                 String posterPath){
        mId = id;
        mTitle = title;
        mOverview = synopsis;
        mReleaseDate = new Date(releaseDate);
        mVoteAverage = voteAverage;
        mVoteCount = totlaCount;
        mPosterPath = posterPath;
    }

    /**
     * Constructor given json Object
     * @param movieJsonObject
     */
    public Movie(JSONObject movieJsonObject) {
        mIsAdult = movieJsonObject.optBoolean(KEY_ADULT, false);
        mBackdropPath = movieJsonObject.optString(KEY_BACKDROP_PATH);

        mGenreIds = new ArrayList<>();
        JSONArray genreIds = movieJsonObject.optJSONArray(KEY_GENRE_IDS);
        if(genreIds != null){
            for(int i = 0; i < genreIds.length(); i++){
                mGenreIds.add(genreIds.optInt(i));
            }
        }

        mId = movieJsonObject.optInt(KEY_ID);
        mOriginalLanguage = movieJsonObject.optString(KEY_ORIGINAL_LANGUAGE);
        mOriginalTitle = movieJsonObject.optString(KEY_ORIGINAL_TITLE);
        mTitle = movieJsonObject.optString(KEY_TITLE);
        mOverview = movieJsonObject.optString(KEY_OVERVIEW);

        String releaseDateString = movieJsonObject.optString(KEY_RELEASE_DATE, null);
        if(releaseDateString != null){
            mReleaseDate = DateUtil.GetSimpleDate(releaseDateString);
        }

        mPosterPath = movieJsonObject.optString(KEY_POSTER_PATH);
        mPopularity = movieJsonObject.optDouble(KEY_POPULARITY);
        mHasVideo = movieJsonObject.optBoolean(KEY_VIDEO);
        mVoteAverage = movieJsonObject.optDouble(KEY_VOTE_AVERAGE);
        mVoteCount = movieJsonObject.optInt(KEY_VOTE_COUNT);
    }

    /**
     * Constructor for Parcel
     * @param in
     */
    protected Movie(Parcel in) {
        mIsAdult = in.readByte() != 0;
        mBackdropPath = in.readString();
        mId = in.readInt();
        mOriginalLanguage = in.readString();
        mOriginalTitle = in.readString();
        mTitle = in.readString();
        mOverview = in.readString();
        mReleaseDate = new Date(in.readLong());
        mPosterPath = in.readString();
        mPopularity = in.readDouble();
        mHasVideo = in.readByte() != 0;
        mVoteAverage = in.readDouble();
        mVoteCount = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public List<Integer> getGenreIds() {
        return mGenreIds;
    }

    public boolean isHasVideo() {
        return mHasVideo;
    }

    public int getId() {
        return mId;
    }

    public boolean isIsAdult() {
        return mIsAdult;
    }

    public String getOriginalLanguage() {
        return mOriginalLanguage;
    }

    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public Date getReleaseDate() {
        return mReleaseDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (mIsAdult ? 1 : 0));
        dest.writeString(mBackdropPath);
        dest.writeInt(mId);
        dest.writeString(mOriginalLanguage);
        dest.writeString(mOriginalTitle);
        dest.writeString(mTitle);
        dest.writeString(mOverview);
        dest.writeLong(mReleaseDate.getTime());
        dest.writeString(mPosterPath);
        dest.writeDouble(mPopularity);
        dest.writeByte((byte) (mHasVideo ? 1 : 0));
        dest.writeDouble(mVoteAverage);
        dest.writeInt(mVoteCount);
    }
}
