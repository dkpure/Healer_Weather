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
public class SunnyDrawer extends BaseDrawer<SunnyDrawer.SunnyHolder> {

    public SunnyDrawer(Context context, int bgResId) {
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
        for (SunnyHolder holder : mHolders) {
            holder.updateRandom(canvas, holder.matrix, mPaint, alpha);
        }
        return true;
    }


    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (mHolders.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                SunnyHolder holder = new SunnyHolder(mContext, width, height, new Matrix(), i);
                mHolders.add(holder);
            }
        }
    }

    public static class SunnyHolder {
        float initPositionX;
        float initPositionY;
        Bitmap frame;
        RectF box;
        RectF targetBox;
        int width;
        int position = 0;
        int alpha;
        boolean alphaUp = true;
        protected Matrix matrix;

        public SunnyHolder(Context context, int width, int height, Matrix matrix, int i) {
            super();
            this.position = i;
            this.width = width;
            this.matrix = matrix;
            box = new RectF();
            targetBox = new RectF();

            if (i == 0) {
                initPositionX = width * 0.039F;
                initPositionY = height * 0.49F;
                frame = BitmapFactory.decodeResource(context.getResources(), R.drawable.sunny_day_cloud3);
            } else if (i == 1) {
                initPositionX = width * 0.758F;
                initPositionY = height * 0.49F;
                frame = BitmapFactory.decodeResource(context.getResources(), R.drawable.sunny_day_cloud3);
            } else if (i == 2) {
                initPositionX = width * 0.765F;
                initPositionY = height * 0.265F;
                frame = BitmapFactory.decodeResource(context.getResources(), R.drawable.sunny_day_sunshine);
            }

            box.set(0, 0, frame.getWidth(), frame.getHeight());
            matrix.reset();
            if (i == 0 || i == 1) {
                matrix.setScale(6f, 6f);
            } else if (i == 2) {
                matrix.setScale(2f, 2f);
            }

            matrix.mapRect(targetBox, box);
            matrix.postTranslate(initPositionX - targetBox.width() / 2, initPositionY - targetBox.height() / 2);
        }

        public void updateRandom(Canvas canvas, Matrix matrix, Paint paint, float alphabg) {
            //移动

            if (position == 0 || position == 1) {
                matrix.postTranslate(1.5F, 0);


                //边界处理
                matrix.mapRect(targetBox, box);
                if (targetBox.left > width) {
                    matrix.postTranslate(-targetBox.right, 0);
                }
                paint.setAlpha(255);
            }

            //绘制

            if (position == 2) {
                matrix.mapRect(targetBox, box);
                matrix.postRotate(0.5F, targetBox.centerX(), targetBox.centerY());
                //透明度变化
                if (alphaUp) {
                    alpha++;
                } else {
                    alpha--;
                }
                if (alpha >= 255) {
                    alphaUp = false;
                }
                if (alpha <= 0) {
                    alphaUp = true;
                }
                paint.setAlpha(alpha);
            }


            if (alphabg < 1) {
                //说明是还在渐变
                paint.setAlpha((int) (alphabg * 255));
            } else if (alphabg == 1) {
                //不做任何操作'
                if (paint.getAlpha() != 255) {
                    paint.setAlpha(255);
                }
            }
            canvas.drawBitmap(frame, matrix, paint);
        }
    }
}
