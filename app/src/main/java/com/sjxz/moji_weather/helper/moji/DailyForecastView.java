package com.sjxz.moji_weather.helper.moji;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.sjxz.moji_weather.R;
import com.sjxz.moji_weather.bean.weather.DailyForecastBean;
import com.sjxz.moji_weather.util.Utils;

import java.util.List;

/**
 * @author WYH_Healer
 * @email 3425934925@qq.com
 * Created by xz on 2016/11/30.
 * Role:一周的天气预报(自定义view样式曲线)
 */
public class DailyForecastView extends View {

    private int mWidth, mHeight;
    private float mPercent;
    private float mDensity;
    private Path mMaxTempPath = new Path();
    private Path mMinTempPath = new Path();
    private final TextPaint mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintBlue = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mPaintOrange = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<DailyForecastBean> mForecastList;
    private Bitmap[] mWeatherIcons;
    private Data[] mDatas;
    private float[] mXPos;
    private float[] mYMax;
    private float[] mYMin;
    private static final int[] mImageResIds =
            {
                    R.drawable.w0, R.drawable.w2, R.drawable.w1,
                    R.drawable.w7, R.drawable.w8, R.drawable.w6,
                    R.drawable.w45, R.drawable.w14, R.drawable.w13,
                    R.drawable.w30, R.drawable.w31, R.drawable.w34,
            };

    private static class Data {
        float minOffsetPercent, maxOffsetPercent;// 差值%
        int tmp_max, tmp_min;
        String date;
        String wind_sc;
        String cond_txt_d;

        String monthandday;//几月几号
        String day_weather;//早上的天气
        String night_weather;//晚上的天气
        String cloud_power;//风速
    }

    public DailyForecastView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDensity = context.getResources().getDisplayMetrics().density;
        mWeatherIcons = new Bitmap[mImageResIds.length];
        for (int i = 0; i < mWeatherIcons.length; i++) {
            mWeatherIcons[i] = BitmapFactory.decodeResource(getResources(), mImageResIds[i]);
        }

        if (isInEditMode())
            return;

        init();
    }

    private void init() {
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(1f * mDensity);
        mPaint.setTextSize(12f * mDensity);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mPaintBlue.setColor(getResources().getColor(R.color.material_deep_teal_20));
        mPaintBlue.setStrokeWidth(1f * mDensity);
        mPaintBlue.setTextSize(12f * mDensity);
        mPaintBlue.setStyle(Paint.Style.STROKE);
        mPaintBlue.setTextAlign(Paint.Align.CENTER);

        mPaintOrange.setColor(getResources().getColor(R.color.orange_light));
        mPaintOrange.setStrokeWidth(1f * mDensity);
        mPaintOrange.setTextSize(12f * mDensity);
        mPaintOrange.setStyle(Paint.Style.STROKE);
        mPaintOrange.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        if (isInEditMode())
            return;

        mPaint.setStyle(Paint.Style.FILL);//设置字体风格
        //一共需要 顶部文字2(+图占8行)+底部文字2 + 【间距1 + 日期1 + 间距0.5 +　晴1 + 间距0.5f + 微风1 + 底部边距1f 】 = 18行  +[同字]
        //                                  12     13       14      14.5    15.5      16      17       18
        final float textSize = mHeight / 30f;
        mPaint.setTextSize(textSize);
        final float textOffset = getTextPaintOffset(mPaint);
        final float dH = textSize * 8f;
        final float dCenterY = textSize * 14f;

        if (mDatas == null || mDatas.length <= 1) {
            canvas.drawLine(0, dCenterY, mWidth, dCenterY, mPaint);//没有数据的情况下只画一条线
            return;
        }

        final float dW = (float) mWidth/ (float) mDatas.length;
        mMaxTempPath.reset();
        mMinTempPath.reset();
        final int length = mDatas.length;
        final float textPercent = (mPercent >= 0.6f) ? ((mPercent - 0.6f) / 0.4f) : 0f;
        final float pathPercent = (mPercent >= 0.6f) ? 1f : (mPercent / 0.6f);

        //画底部的三行文字和标注最高最低温度
        mPaint.setAlpha((int) (255 * textPercent));
        for (int i = 0; i < length; i++) {
            mXPos[i] = i * dW + dW / 2f;//画圆的中心点
            mYMax[i] = dCenterY - mDatas[i].maxOffsetPercent * dH;
            mYMin[i] = dCenterY - mDatas[i].minOffsetPercent * dH;

        }
        mPaint.setAlpha(255);

        mMaxTempPath.moveTo(0, mYMax[0]);
        mMinTempPath.moveTo(0, mYMin[0]);
        for (int i = 0; i < (length - 1); i++) {
            final float midX = (mXPos[i] + mXPos[i + 1]) / 2f;
            final float midYMax = (mYMax[i] + mYMax[i + 1]) / 2f;
            final float midYMin = (mYMin[i] + mYMin[i + 1]) / 2f;

            mMaxTempPath.cubicTo(mXPos[i] - 1, mYMax[i], mXPos[i], mYMax[i], midX, midYMax);
//			mMaxTempPath.quadTo(mXPos[i], mYMax[i], midX, midYMax);
            mMinTempPath.cubicTo(mXPos[i] - 1, mYMin[i], mXPos[i], mYMin[i], midX, midYMin);
//			mMinTempPath.quadTo(mXPos[i], mYMin[i], midX, midYMin);
        }

        final int lastPos = mXPos.length - 1;
        mMaxTempPath.cubicTo(
                mXPos[lastPos] - 1,
                mYMax[lastPos],
                mXPos[lastPos],
                mYMax[lastPos],
                mWidth,
                mYMax[lastPos]
        );
        mMinTempPath.cubicTo(
                mXPos[lastPos] - 1,
                mYMin[lastPos],
                mXPos[lastPos],
                mYMin[lastPos],
                mWidth,
                mYMin[lastPos]
        );

        //draw max_tmp and min_tmp path
        mPaint.setStyle(Paint.Style.STROKE);
        final boolean needClip = pathPercent < 1f;
        if (needClip) {
            canvas.save();
            canvas.clipRect(0, 0, this.mWidth * pathPercent, this.mHeight);
        }
        canvas.drawPath(mMaxTempPath, mPaintOrange);
        canvas.drawPath(mMinTempPath, mPaintBlue);

        mPaint.setStyle(Paint.Style.FILL);//设置字体风格
        for (int i = 0; i < length; i++) {
            final Data d = mDatas[i];
            int CircleMaxY = 0;
            int CircleMaxYPre = 0;
            if (length >= 2 && i >= 1) {
                if ((mDatas[i - 1].tmp_max - d.tmp_max) > 3) {
                    CircleMaxY = 7 * Math.abs(mDatas[i - 1].tmp_max - d.tmp_max) / 3;
                } else if (-(mDatas[i - 1].tmp_max - d.tmp_max) > 3) {
                    CircleMaxY = -7 * Math.abs(mDatas[i - 1].tmp_max - d.tmp_max) / 3;
                }
                if (length > i + 1) {
                    if ((d.tmp_max - mDatas[i + 1].tmp_max) > 3) {
                        CircleMaxYPre = -4 * Math.abs(d.tmp_max - mDatas[i + 1].tmp_max) / 3;
                    } else if (-(d.tmp_max - mDatas[i + 1].tmp_max) > 3) {
                        CircleMaxYPre = 4 * Math.abs(d.tmp_max - mDatas[i + 1].tmp_max) / 3;
                    }
                }
            }

            //上方地址
            canvas.drawText(d.tmp_max + "°", mXPos[i], mYMax[i] - textSize + textOffset + (CircleMaxY > 0 || CircleMaxYPre > 0 ? -16 : 0), mPaint);// - textSize
            canvas.drawText(Utils.prettyDate(d.date) + "", mXPos[i], textSize + textOffset, mPaint);//日期d.date.substring(5)
            canvas.drawText(d.monthandday + "", mXPos[i], textSize * 2.5f + textOffset, mPaint);//“晴"
            canvas.drawText(d.day_weather + "", mXPos[i], textSize * 4f + textOffset, mPaint);//微风
            canvas.drawBitmap(Utils.big(checkDayWeather(d.day_weather), 80, 80), mXPos[i] - 40, textSize * 5f + textOffset, mPaint);

            //下方数据
            canvas.drawText(d.tmp_min + "°", mXPos[i], mYMin[i] + textSize + textOffset, mPaint);
            canvas.drawBitmap(Utils.big(checkNightWeather(d.night_weather), 80, 80), mXPos[i] - 40, textSize * 20.5f + textOffset, mPaint);
            canvas.drawText(d.night_weather + "", mXPos[i], textSize * 24f + textOffset, mPaint);//日期d.date.substring(5)
            canvas.drawText(d.wind_sc + "", mXPos[i], textSize * 25.5f + textOffset, mPaint);//“晴"
            canvas.drawText(d.cloud_power + "", mXPos[i], textSize * 27f + textOffset, mPaint);//微风

            //圆点
            canvas.drawCircle(mXPos[i], mYMax[i] - CircleMaxYPre - CircleMaxY, 10, mPaint);
            canvas.drawCircle(mXPos[i], mYMin[i], 10, mPaint);
        }

        if (needClip) {
            canvas.restore();
        }
        if (mPercent < 1) {
            mPercent += 0.025f;// 0.025f;
            mPercent = Math.min(mPercent, 1f);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * R.drawable.w0, R.drawable.w2,
     * R.drawable.w1, R.drawable.w7, R.drawable.w8,
     * R.drawable.w6, R.drawable.w45, R.drawable.w14, R.drawable.w13,
     * <p>
     * R.drawable.w30,
     * R.drawable.w31, R.drawable.w34,
     */
    public Bitmap checkDayWeather(String weather) {

        switch (weather) {
            case "晴":
                return mWeatherIcons[0];
            case "阴":
                return mWeatherIcons[1];
            case "多云":
                return mWeatherIcons[2];
            case "小雨":
                return mWeatherIcons[3];
            case "中雨":
                return mWeatherIcons[4];
            case "雨夹雪":
                return mWeatherIcons[5];
            case "霾":
                return mWeatherIcons[6];
            case "小雪":
                return mWeatherIcons[7];
            case "阵雪":
                return mWeatherIcons[8];

        }
        return BitmapFactory.decodeResource(getResources(), R.drawable.w0);
    }

    public Bitmap checkNightWeather(String weather) {

        switch (weather) {
            case "晴":
                return mWeatherIcons[9];
            case "阴":
                return mWeatherIcons[1];
            case "多云":
                return mWeatherIcons[10];
            case "小雨":
                return mWeatherIcons[3];
            case "中雨":
                return mWeatherIcons[4];
            case "雨夹雪":
                return mWeatherIcons[5];
            case "霾":
                return mWeatherIcons[6];
            case "小雪":
                return mWeatherIcons[7];
            case "阵雪":
                return mWeatherIcons[11];
        }
        return mWeatherIcons[0];
    }


    public void setData(List<DailyForecastBean> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        if (mForecastList == list) {
            mPercent = 0f;
            invalidate();
            return;
        }

        mForecastList = list;
        mDatas = new Data[mForecastList.size()];
        mXPos = new float[mDatas.length];
        mYMax = new float[mDatas.length];
        mYMin = new float[mDatas.length];

        try {
            int all_max = Integer.MIN_VALUE;
            int all_min = Integer.MAX_VALUE;
            for (int i = 0, len = mForecastList.size(); i < len; i++) {
                DailyForecastBean forecast = mForecastList.get(i);
                int max = Integer.valueOf(forecast.getTemperature_max());
                int min = Integer.valueOf(forecast.getTemperature_min());
                if (all_max < max) {
                    all_max = max;
                }
                if (all_min > min) {
                    all_min = min;
                }
                final Data data = new Data();
                data.tmp_max = max;
                data.tmp_min = min;
                data.date = forecast.getWeekday();
                data.wind_sc = forecast.getCloud_direction().equals("无持续风向") ? "无风向" : forecast.getCloud_direction();
                data.cond_txt_d = forecast.getDay_weather();
                data.monthandday = forecast.getDate();
                data.day_weather = forecast.getDay_weather();
                data.night_weather = forecast.getNight_weather();
                data.cloud_power = forecast.getCloud_speed();
                mDatas[i] = data;
            }
            float all_distance = Math.abs(all_max - all_min);
            float average_distance = (all_max + all_min) / 2f;
            for (Data d : mDatas) {
                d.maxOffsetPercent = (d.tmp_max - average_distance) / all_distance;
                d.minOffsetPercent = (d.tmp_min - average_distance) / all_distance;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mPercent = 0f;
        invalidate();
    }


    /**
     * 进行尺寸匹配
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    public static float getTextPaintOffset(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return -(fontMetrics.bottom - fontMetrics.top) / 2f - fontMetrics.top;
    }
}
