package com.sjxz.moji_weather.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.sjxz.moji_weather.R;

/**
 * @author WYH_Healer
 * @email 3425934925@qq.com
 * Created by xz on 2017/2/13.
 * Role:
 */
public class FogDayDrawer extends BaseDrawer<FogDayDrawer.FogDayHolder> {

    public FogDayDrawer(Context context, int bgResId) {
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

        for (FogDayHolder holder : mHolders) {
            holder.updateRandom(canvas, holder.matrix, mPaint, alpha);
        }

        return true;
    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (this.mHolders.size() == 0) {
            for (int i = 0; i < 7; i++) {
                FogDayHolder holder = new FogDayHolder(mContext, width, height, new Matrix(), i);
                mHolders.add(holder);
            }
        }
    }

    public static class FogDayHolder {
        float initPositionX;
        float initPositionY;
        Bitmap frame;
        RectF box;
        RectF targetBox;
        int width;
        int position = 0;
        protected Matrix matrix;
        public int[] bitmaps = {
                R.drawable.sand_1,
                R.drawable.sand_2,
                R.drawable.sand_3,
                R.drawable.sand_4,
                R.drawable.sand_5,
                R.drawable.sand_6,
                R.drawable.sand_7,
        };

        public FogDayHolder(Context context, int width, int height, Matrix matrix, int i) {
            super();
            this.position = i;
            this.width = width;
            this.matrix = matrix;
            box = new RectF();
            targetBox = new RectF();
            initPositionX = width * 0.15F * position;
            initPositionY = height * 1.0F * 0.166f * position;
            frame = BitmapFactory.decodeResource(context.getResources(), bitmaps[position]);
            box.set(0, 0, frame.getWidth(), frame.getHeight());
            matrix.reset();
            matrix.setScale(3f, 3f);
            matrix.mapRect(targetBox, box);
            matrix.postTranslate(initPositionX - targetBox.width() / 2, initPositionY - targetBox.height() / 2);

        }

        public void updateRandom(Canvas canvas, Matrix matrix, Paint paint, float alpha) {
            matrix.postTranslate(0, -1.5F);
            //边界处理
            matrix.mapRect(targetBox, box);
            if (-targetBox.bottom > frame.getHeight()) {
                matrix.postTranslate(0, -targetBox.top * 30f);
            }
            //绘制
            if (alpha < 1) {
                //说明是还在渐变
                paint.setAlpha((int) (alpha * 255));
            } else if (alpha == 1) {
                //不做任何操作'
                if (paint.getAlpha() != 255) {
                    paint.setAlpha(255);
                }
            }
            //绘制
            canvas.drawBitmap(frame, matrix, paint);
        }
    }
}
