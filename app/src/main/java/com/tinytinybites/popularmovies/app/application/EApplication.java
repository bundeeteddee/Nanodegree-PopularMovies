package com.tinytinybites.popularmovies.app.application;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by bundee on 8/4/16.
 */
public class EApplication extends Application {

    protected static EApplication _instance;

    @Override
    public void onCreate() {
        super.onCreate();

        _instance = this;

        // Stetho is a tool created by facebook to view your database in chrome inspect.
        // The code below integrates Stetho into your app. More information here:
        // http://facebook.github.io/stetho/
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
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
