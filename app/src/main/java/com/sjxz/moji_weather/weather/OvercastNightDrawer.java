package com.sjxz.moji_weather.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * @author WYH_Healer
 * @email 3425934925@qq.com
 * Created by xz on 2017/2/13.
 * Role:
 */
public class OvercastNightDrawer extends BaseDrawer<OvercastNightDrawer.OvercastDayHolder> {

    public OvercastNightDrawer(Context context, int bgResId) {
        super(context, bgResId);
    }

    @Override
    public boolean drawWeather(Canvas canvas, float alpha) {
        mPaint.setAlpha(alpha != 1 ? (int) (alpha * 255) : 255);
        canvas.drawBitmap(
                mBg,
                mSrcRect,
                mDstRect,
                mPaint
        );
        for (OvercastDayHolder holder : mHolders) {
            holder.updateRandom(canvas, holder.matrix, mPaint);
        }
        return true;
    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (this.mHolders.size() == 0) {
            for (int i = 0; i < 5; i++) {
                OvercastDayHolder holder = new OvercastDayHolder(mContext, width, height, new Matrix(), i);
                mHolders.add(holder);
            }
        }
    }

    public static class OvercastDayHolder {
        float initPositionX;
        float initPositionY;
        Bitmap frame;
        RectF box;
        RectF targetBox;
        int width;
        int position = 0;
        protected Matrix matrix;

        public OvercastDayHolder(Context mContext, int width, int height, Matrix matrix, int i) {
            super();
            this.position = i;
            this.width = width;
            this.matrix = matrix;
            box = new RectF();
            targetBox = new RectF();


        }

        public void updateRandom(Canvas canvas, Matrix matrix, Paint mPaint) {

        }
    }
}
