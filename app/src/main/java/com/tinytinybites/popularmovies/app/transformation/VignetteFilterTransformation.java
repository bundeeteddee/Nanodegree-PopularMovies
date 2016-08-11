package com.tinytinybites.popularmovies.app.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;

import java.util.Arrays;
import com.squareup.picasso.Transformation;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;

/**
 * Created by bundee on 8/11/16.
 * Reference from: https://github.com/wasabeef/picasso-transformations
 */
public class VignetteFilterTransformation implements Transformation{
    //Variables
    private Context mContext;
    private GPUImageVignetteFilter mFilter = new GPUImageVignetteFilter();
    private PointF mCenter;
    private float[] mVignetteColor;
    private float mVignetteStart;
    private float mVignetteEnd;

    /**
     *
     * @param context
     */
    public VignetteFilterTransformation(Context context) {
        mContext = context;
        mCenter = new PointF();
    }

    /**
     *
     * @param context
     * @param center
     * @param color
     * @param start
     * @param end
     */
    public VignetteFilterTransformation(Context context,
                                        PointF center,
                                        float[] color,
                                        float start,
                                        float end) {
        mContext = context;
        mCenter = center;
        mVignetteColor = color;
        mVignetteStart = start;
        mVignetteEnd = end;
        mFilter.setVignetteCenter(mCenter);
        mFilter.setVignetteColor(mVignetteColor);
        mFilter.setVignetteStart(mVignetteStart);
        mFilter.setVignetteEnd(mVignetteEnd);
    }

    @Override
    public Bitmap transform(Bitmap source) {
        GPUImage gpuImage = new GPUImage(mContext);
        gpuImage.setImage(source);
        gpuImage.setFilter(mFilter);
        Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();

        source.recycle();

        return bitmap;
    }

    @Override
    public String key() {
        return "VignetteFilterTransformation(center=" + mCenter.toString() +
            ",color=" + Arrays.toString(mVignetteColor) +
            ",start=" + mVignetteStart + ",end=" + mVignetteEnd + ")";
        }
}
