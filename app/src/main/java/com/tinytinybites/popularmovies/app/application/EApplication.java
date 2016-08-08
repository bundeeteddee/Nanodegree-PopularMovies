package com.tinytinybites.popularmovies.app.application;

import android.app.Application;

/**
 * Created by bundee on 8/4/16.
 */
public class EApplication extends Application {

    protected static EApplication _instance;

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;
    }

    /**
     * Get instance
     * Note: Can be used as a context that doesn't leak
     * @return
     */
    public static EApplication getInstance(){
        //Note: Application class started when application starts, _instance should never be null.
        return _instance;
    }


}
