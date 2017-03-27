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
public class RainDayDrawer extends BaseDrawer<RainDayDrawer.RainDayHolder> {

    public RainDayDrawer(Context context, int bgResId) {
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
        for (RainDayHolder holder : mHolders) {
            holder.updateRandom(canvas, holder.matrix, mPaint, alpha);
        }

        return true;
    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (mHolders.size() == 0) {
            for (int i = 0; i < 82; i++) {
                RainDayHolder holder = new RainDayHolder(mContext, width, height, new Matrix(), i);
                mHolders.add(holder);
            }
        }
    }

    public static class RainDayHolder {
        float initPositionX;
        float initPositionY;
        Bitmap frame;
        RectF box;
        RectF targetBox;
        int width;
        int height;
        int position = 0;
        protected Matrix matrix;
        int randomInt;
        Random random = new Random();

        public int[] bitmaps = {
                R.drawable.raindrop_l,
                R.drawable.raindrop_m,
                R.drawable.raindrop_s,
                R.drawable.raindrop_xl,
                R.drawable.raindrop_l,
                R.drawable.raindrop_m,
                R.drawable.raindrop_s,
                R.drawable.raindrop_xl,
                R.drawable.raindrop_l,
                R.drawable.raindrop_m,
                R.drawable.raindrop_s,
                R.drawable.raindrop_xl,
        };

        public RainDayHolder(Context mContext, int width, int height, Matrix matrix, int i) {
            super();
            this.position = i;
            this.width = width;
            this.height = height;
            this.matrix = matrix;
            box = new RectF();
            targetBox = new RectF();
            if (i == 0) {
                initPositionX = width * 0.039F;
                initPositionY = height * 0.11F;
                frame = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.moderate_rain_cloud1);
            } else if (i == 1) {
                initPositionX = width * 0.758F;
                initPositionY = height * 0.11F;
                frame = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.moderate_rain_cloud1);
            } else {
                randomInt = random.nextInt(80);
                initPositionX = width * 0.15F * (position % 12 == 0 ? 1 : position % 12);
                initPositionY = height * 1.0F * 0.05f * (randomInt % 20 == 0 ? 1 : randomInt % 20);
                frame = BitmapFactory.decodeResource(mContext.getResources(), bitmaps[randomInt % 12]);
            }

            box.set(0, 0, frame.getWidth(), frame.getHeight());
            matrix.reset();
            if (i == 0 || i == 1) {
                matrix.setScale(2f, 2f);
            } else {
                matrix.setScale(6f, 6f);
                matrix.setRotate(-20f);
            }

            matrix.mapRect(targetBox, box);
            matrix.postTranslate(initPositionX - targetBox.width() / 2, initPositionY - targetBox.height() / 2);

        }

        public void updateRandom(Canvas canvas, Matrix matrix, Paint mPaint, float alpha) {

            if (position == 0 || position == 1) {
                matrix.postTranslate(1.5F, 0);
                //边界处理
                matrix.mapRect(targetBox, box);
                if (targetBox.left > width) {
                    matrix.postTranslate(-targetBox.right, 0);
                }
            } else {
                matrix.postTranslate(10f, 30f);
                //边界处理
                matrix.mapRect(targetBox, box);
                if (targetBox.top > height) {
                    matrix.postTranslate(0, -targetBox.bottom);
                }
                if (targetBox.left > width) {
                    matrix.postTranslate(-targetBox.right, 0);
                }
            }

            //绘制
            if (alpha < 1) {
                //说明是还在渐变
                mPaint.setAlpha((int) (alpha * 255));
            } else if (alpha == 1) {
                //不做任何操作'
                if (mPaint.getAlpha() != 255) {
                    mPaint.setAlpha(255);
                }
            }

            canvas.drawBitmap(frame, matrix, mPaint);
        }
    }
}
