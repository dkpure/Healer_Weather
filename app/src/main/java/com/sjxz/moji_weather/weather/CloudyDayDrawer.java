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
public class CloudyDayDrawer extends BaseDrawer<CloudyDayDrawer.CloudyDayHolder> {

    public CloudyDayDrawer(Context context, int bgResId) {
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

        for (CloudyDayHolder holder : mHolders) {
            holder.updateRandom(canvas, holder.matrix, mPaint, alpha);
        }
        return true;
    }

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (mHolders.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                CloudyDayHolder holder = new CloudyDayHolder(mContext, width, height, new Matrix(), i);
                mHolders.add(holder);
            }
        }
    }

    public static class CloudyDayHolder {
        float initPositionX;
        float initPositionY;
        Bitmap frame;
        RectF box;
        RectF targetBox;
        int width;
        int position = 0;
        protected Matrix matrix;

        public CloudyDayHolder(Context context, int width, int height, Matrix matrix, int i) {
            super();
            this.position = i;
            this.width = width;
            this.matrix = matrix;
            box = new RectF();
            targetBox = new RectF();

            if (i == 0) {
                initPositionX = width * 0.039F;
                initPositionY = height * 0.49F;
                frame = BitmapFactory.decodeResource(context.getResources(), R.drawable.fog_day_fog_top);
            } else if (i == 1) {
                initPositionX = width * 0.039F;
                initPositionY = height * 0.69F;
                frame = BitmapFactory.decodeResource(context.getResources(), R.drawable.fine_day_cloud1);
            } else if (i == 2) {
                initPositionX = width * 0.758F;
                initPositionY = height * 0.69F;
                frame = BitmapFactory.decodeResource(context.getResources(), R.drawable.fine_day_cloud1);
            }

            box.set(0, 0, frame.getWidth(), frame.getHeight());
            matrix.reset();
            if (i == 0) {
                matrix.setScale(2f, 2f);
            } else if (i == 1 || i == 2) {
                matrix.setScale(3f, 3f);
            }

            matrix.mapRect(targetBox, box);
            matrix.postTranslate(initPositionX - targetBox.width() / 2, initPositionY - targetBox.height() / 2);
        }

        public void updateRandom(Canvas canvas, Matrix matrix, Paint paint, float alpha) {
            matrix.postTranslate(0.5F, 0);

            //边界处理
            matrix.mapRect(targetBox, box);
            if (targetBox.left > width) {
                matrix.postTranslate(-targetBox.right, 0);
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
            canvas.drawBitmap(frame, matrix, paint);
        }
    }
}
