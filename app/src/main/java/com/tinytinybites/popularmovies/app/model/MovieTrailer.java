package com.tinytinybites.popularmovies.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by bundee on 8/11/16.
 */
public class MovieTrailer implements Parcelable{

    //Keys from results
    public static final String KEY_SITE = "site";
    public static final String KEY_NAME = "name";
    public static final String KEY_KEY = "key";
    public static final String KEY_ID = "id";
    public static final String KEY_SIZE = "size";
    public static final String KEY_TYPE = "type";

    //Known sites
    private static final String SITE_YOUTUBE = "YouTube";

    //Variables
    private String mId;
    private String mKey;
    private String mName;
    private String mSite;
    private int mSize;
    private String mType;

    /**
     * Constructor given json Object
     * @param movieTrailerJsonObject
     */
    public MovieTrailer(JSONObject movieTrailerJsonObject) {
        mId = movieTrailerJsonObject.optString(KEY_ID);
        mKey = movieTrailerJsonObject.optString(KEY_KEY);
        mName = movieTrailerJsonObject.optString(KEY_NAME);
        mSite = movieTrailerJsonObject.optString(KEY_SITE);
        mSize = movieTrailerJsonObject.optInt(KEY_SIZE);
        mType = movieTrailerJsonObject.optString(KEY_TYPE);
    }

    protected MovieTrailer(Parcel in) {
        mId = in.readString();
        mKey = in.readString();
        mName = in.readString();
        mSite = in.readString();
        mSize = in.readInt();
        mType = in.readString();
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public String getId() {
        return mId;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    public String getSite() {
        return mSite;
    }

    public int getSize() {
        return mSize;
    }

    public String getType() {
        return mType;
    }

    public boolean isFromYoutube(){ return getSite().equalsIgnoreCase(SITE_YOUTUBE);}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mKey);
        dest.writeString(mName);
        dest.writeString(mSite);
        dest.writeInt(mSize);
        dest.writeString(mType);
    }
}
