package com.sjxz.moji_weather.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;

public class SunDrawable extends RefreshDrawable {
    private static final float MAX_ANGLE = 180f;
    private static final int LIGHT_COUNT = 8;

    private float mCenterX;
    private float mCenterY;
    private final float RADIUS;
    private RectF mCircleBound;
    private float mPercent;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mOffset;
    private boolean mRunning;
    private float mRotationAngle;

    public SunDrawable(Context context, PullRefreshLayout layout) {
        super(context, layout);

        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(dp2px(2));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);

        RADIUS = dp2px(6);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        float radius = getRefreshLayout().getFinalOffset() / 2;
        RectF bound = new RectF(
                bounds.width() / 2 - radius,
                bounds.top - radius,
                bounds.width() / 2 + radius,
                bounds.top + radius
        );
        mCenterX = bound.centerX();
        mCenterY = bound.centerY();
        mCircleBound =
                new RectF(
                        mCenterX - RADIUS,
                        mCenterY - RADIUS,
                        mCenterX + RADIUS,
                        mCenterY + RADIUS
                );
    }

    @Override
    public void setPercent(float percent) {
        mPercent = percent;
        invalidateSelf();
    }

    @Override
    public void setColorSchemeColors(int[] colorSchemeColors) {
//        if (colorSchemeColors != null && colorSchemeColors.length > 0) {
//            mPaint.setColor(colorSchemeColors[0]);
//        }
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        mOffset += offset;
        invalidateSelf();
    }

    @Override
    public void start() {
        mRunning = true;
        mRotationAngle = 0;
        invalidateSelf();
    }

    @Override
    public void stop() {
        mRunning = false;
    }

    @Override
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();

        canvas.translate(0, mOffset / 2);
        if (isRunning()) {
            canvas.rotate(mRotationAngle, mCenterX, mCenterY);//旋转画布用于实现动画效果
            mRotationAngle = mRotationAngle < 360 ? mRotationAngle + 8 : 0;//旋转的角度
            invalidateSelf();
        }

        final float percent = mPercent;
        final float sweepAngle = MAX_ANGLE * percent;
        canvas.drawArc(mCircleBound, 180, sweepAngle, false, mPaint);//画180-360的圆弧
        canvas.drawArc(mCircleBound, 0, sweepAngle, false, mPaint);//画0-180的圆弧 MAX_ANGLE * percent是扫过的角度
        mPaint.setAlpha((int) (255f * percent));//刻度和下拉百分比渐变功能
        final float divAngle = 360 / LIGHT_COUNT;
        final float scaleFactor = 1f + 0.4f * percent;// 0.7*0.857=0.6 也就是说刻度盘占 0.6~0.7的部分
        for (int i = 0; i < LIGHT_COUNT; i++) {// 画刻度
            double radians = Math.toRadians(i * divAngle);
            float x1 = (float) (Math.cos(radians) * RADIUS * 1.6f);
            float y1 = (float) (Math.sin(radians) * RADIUS * 1.6f);
            float x2 = x1 * scaleFactor;
            float y2 = y1 * scaleFactor;
            canvas.drawLine(mCenterX + x1, y1, mCenterX + x2, y2, mPaint);
        }
        mPaint.setAlpha(255);

        canvas.restore();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getContext().getResources().getDisplayMetrics()
        );
    }
}
