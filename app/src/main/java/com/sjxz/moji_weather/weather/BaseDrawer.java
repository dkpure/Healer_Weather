package com.sjxz.moji_weather.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WYH_Healer
 * @email 3425934925@qq.com
 * Created by xz on 2017/2/13.
 * Role:
 */
public abstract class BaseDrawer<T> {
    protected final Context mContext;
    protected final List<T> mHolders = new ArrayList<>();
    protected final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final Bitmap mBg;
    protected final Rect mSrcRect;
    protected Rect mDstRect;
    private int mWidth, mHeight;

    public BaseDrawer(Context context, int bgResId) {
        mContext = context;
        mBg = BitmapFactory.decodeResource(mContext.getResources(), bgResId);
        mSrcRect = new Rect(0, 0, mBg.getWidth(), mBg.getHeight());
    }

    /**
     * @param canvas
     * @return needDrawNextFrame
     */
    public boolean draw(Canvas canvas, float alpha) {
        return drawWeather(canvas, alpha);
    }

    public abstract boolean drawWeather(Canvas canvas, float alpha);

    protected void setSize(int width, int height) {
        if (mWidth != width && mHeight != height) {
            mWidth = width;
            mHeight = height;

            mDstRect = new Rect(0, 0, width, height);
        }
    }
}
