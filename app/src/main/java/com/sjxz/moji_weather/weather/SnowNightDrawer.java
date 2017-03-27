package com.sjxz.moji_weather.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.sjxz.moji_weather.R;

import java.util.Random;

/**
 * @author WYH_Healer
 * @email 3425934925@qq.com
 * Created by xz on 2017/2/13.
 * Role:
 */

public class SnowNightDrawer extends BaseDrawer<SnowNightDrawer.SnowNightHolder> {

    public SnowNightDrawer(Context context, int bgResId) {
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
        for (SnowNightHolder holder : mHolders) {
            holder.updateRandom(canvas, holder.matrix, mPaint, alpha);
        }
        return true;
    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (mHolders.isEmpty()) {
            for (int i = 0; i < 80; i++) {
                SnowNightHolder holder = new SnowNightHolder(mContext, width, height, new Matrix(), i);
                mHolders.add(holder);
            }
        }
    }

    public static class SnowNightHolder {
        float initPositionX;
        float initPositionY;
        Bitmap frame;
        RectF box;
        RectF targetBox;
        int width;
        int height;
        int position = 0;
        Random random = new Random();
        int randomInt;
        protected Matrix matrix;
        public int[] bitmaps = {
                R.drawable.snowflake_l,
                R.drawable.snowflake_m,
                R.drawable.snowflake_xl,
                R.drawable.snowflake_xxl,
                R.drawable.snowflake_l,
                R.drawable.snowflake_m,
                R.drawable.snowflake_xl,
                R.drawable.snowflake_xxl,
                R.drawable.snowflake_l,
                R.drawable.snowflake_m,
                R.drawable.snowflake_xl,
                R.drawable.snowflake_xxl,
        };

        public SnowNightHolder(Context context, int width, int height, Matrix matrix, int i) {
            super();
            this.position = i;
            this.width = width;
            this.matrix = matrix;
            this.height = height;
            box = new RectF();
            targetBox = new RectF();
            initPositionX = width * 0.15F * (position % 12 == 0 ? 1 : position % 12);
            randomInt = random.nextInt(80);
            initPositionY = height * 1.0F * 0.05f * (randomInt % 20 == 0 ? 1 : randomInt % 20);
            frame = BitmapFactory.decodeResource(context.getResources(), bitmaps[randomInt % 12]);
            box.set(0, 0, frame.getWidth(), frame.getHeight());
            matrix.reset();
            matrix.setScale(2f, 2f);
            matrix.mapRect(targetBox, box);
            matrix.postTranslate(initPositionX - targetBox.width() / 2, initPositionY - targetBox.height() / 2);
        }

        public void updateRandom(Canvas canvas, Matrix matrix, Paint mPaint, float alpha) {
            matrix.postTranslate(0, (randomInt % 3f == 0 ? 1.5f : randomInt % 3f));
            //边界处理
            matrix.mapRect(targetBox, box);
            if (targetBox.top > height) {
                matrix.postTranslate(0, -targetBox.bottom);
            }
            if (alpha < 1) {
                //说明是还在渐变
                mPaint.setAlpha((int) (alpha * 255));
            } else if (alpha == 1) {
                //不做任何操作'
                if (mPaint.getAlpha() != 255) {
                    mPaint.setAlpha(255);
                }
            }
            //绘制
            canvas.drawBitmap(frame, matrix, mPaint);
        }
    }
}
