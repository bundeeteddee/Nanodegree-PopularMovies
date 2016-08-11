package com.tinytinybites.popularmovies.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by bundee on 8/11/16.
 */
public class MovieReview implements Parcelable{

    //Keys from results
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_URL = "url";
    public static final String KEY_ID = "id";

    //Variables
    private int mId;
    private String mContent;
    private String mAuthorName;
    private String mUrl;

    /**
     * Constructor given json Object
     * @param movieReviewJsonObject
     */
    public MovieReview(JSONObject movieReviewJsonObject) {
        mId = movieReviewJsonObject.optInt(KEY_ID);
        mContent = movieReviewJsonObject.optString(KEY_CONTENT);
        mAuthorName = movieReviewJsonObject.optString(KEY_AUTHOR);
        mUrl = movieReviewJsonObject.optString(KEY_URL);
    }

    protected MovieReview(Parcel in) {
        mId = in.readInt();
        mContent = in.readString();
        mAuthorName = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mContent);
        dest.writeString(mAuthorName);
        dest.writeString(mUrl);
    }

    public String getAuthorName() {
        return mAuthorName;
    }

    public String getContent() {
        return mContent;
    }

    public int getId() {
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }
}
