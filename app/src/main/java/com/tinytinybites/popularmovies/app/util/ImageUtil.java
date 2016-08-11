package com.tinytinybites.popularmovies.app.util;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.tinytinybites.popularmovies.app.application.EApplication;

/**
 * Created by bundee on 8/10/16.
 */
public class ImageUtil {
    //Tag
    protected static final String TAG = ImageUtil.class.getCanonicalName();

    /**
     * Get the screen size of the tablet
     * @param mContext
     * @return
     */
    public static Point GetScreenSize(Context mContext) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        Point screenSize = new Point();
        screenSize.x = metrics.widthPixels;
        screenSize.y = metrics.heightPixels;

        return screenSize;
    }

    /**
     * Get device height
     * @param mContext
     * @return
     */
    public static int GetDeviceHeight(Context mContext){
        int orientation = mContext.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            return GetScreenSize(mContext).x;
        }else{
            return GetScreenSize(mContext).y;
        }
    }

    /**
     * Get Device width
     * @param context
     * @return
     */
    public static int GetDeviceWidth(Context context){
        int orientation = context.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            return GetScreenSize(context).y;
        }else{
            return GetScreenSize(context).x;
        }
    }

    /**
     * Convert DP to Pixels
     * @param dpSize
     * @param context
     * @return
     */
    public static final int ConvertDpToPixels(int dpSize, Context context){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpSize * scale + 0.5f);
    }

    /**
     * Get toolbar height
     * @return
     */
    public static int GetToolbarHeight() {
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (EApplication.getInstance().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv,
                    true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, EApplication.getInstance().getResources().getDisplayMetrics());
        } else {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, EApplication.getInstance().getResources().getDisplayMetrics());
        }
        return ConvertDpToPixels(actionBarHeight, EApplication.getInstance());
    }

    /**
     * Get status bar height
     * Reference: http://stackoverflow.com/a/14213035/377844
     * @return
     */
    public static int GetStatusBarHeight() {
        int result = 0;
        int resourceId = EApplication.getInstance().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = EApplication.getInstance().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
