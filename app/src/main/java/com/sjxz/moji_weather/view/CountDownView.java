package com.sjxz.moji_weather.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.sjxz.moji_weather.R;

/**
 * @author WYH_Healer
 * @email 3425934925@qq.com
 * Created by xz on 2017/3/7.
 * Role:
 */
public class CountDownView extends View {
    private static final int START_SWEEP = -90;

    private int mCircleSolidColor = Color.BLUE;
    private int mCircleStrokeColor = Color.WHITE;
    private int mCircleStrokeWidth = 10;
    private int mCircleRadius = 60;
    private int mProgressColor = Color.GRAY;
    private int mProgressWidth = 11;
    private int mSmallCircleSolidColor = Color.BLACK;
    private int mSmallCircleStrokeColor = Color.WHITE;
    private float mSmallCircleStrokeWidth = 8;
    private float mSmallCircleRadius = 30;
    private int mTextColor = Color.WHITE;
    private float mTextSize = 30;
    private float mCurrentAngle;
    private long mCountdownTime;
    private Paint mCirclePaint;
    private Paint mProgressPaint;
    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    private String mTextDesc;
    private RectF mCircleBound;
    private ValueAnimator mAnimator;
    private CountDownTimer mTimer;
    private boolean mCancelled;

    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(attrs);
        initPaint();
    }

    private void initStyle(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownProgress);
        try {
            int indexCount = a.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.CountDownProgress_default_circle_solide_color:
                        mCircleSolidColor = a.getColor(attr, mCircleSolidColor);
                        break;
                    case R.styleable.CountDownProgress_default_circle_stroke_color:
                        mCircleStrokeColor = a.getColor(attr, mCircleStrokeColor);
                        break;
                    case R.styleable.CountDownProgress_default_circle_stroke_width:
                        mCircleStrokeWidth = (int) a.getDimension(attr, mCircleStrokeWidth);
                        break;
                    case R.styleable.CountDownProgress_default_circle_radius:
                        mCircleRadius = (int) a.getDimension(attr, mCircleRadius);
                        break;
                    case R.styleable.CountDownProgress_progress_color:
                        mProgressColor = a.getColor(attr, mProgressColor);
                        break;
                    case R.styleable.CountDownProgress_progress_width:
                        mProgressWidth = (int) a.getDimension(attr, mProgressWidth);
                        break;
                    case R.styleable.CountDownProgress_small_circle_solide_color:
                        mSmallCircleSolidColor = a.getColor(attr, mSmallCircleSolidColor);
                        break;
                    case R.styleable.CountDownProgress_small_circle_stroke_color:
                        mSmallCircleStrokeColor = a.getColor(attr, mSmallCircleStrokeColor);
                        break;
                    case R.styleable.CountDownProgress_small_circle_stroke_width:
                        mSmallCircleStrokeWidth = (int) a.getDimension(attr, mSmallCircleStrokeWidth);
                        break;
                    case R.styleable.CountDownProgress_small_circle_radius:
                        mSmallCircleRadius = (int) a.getDimension(attr, mSmallCircleRadius);
                        break;
                    case R.styleable.CountDownProgress_text_color:
                        mTextColor = a.getColor(attr, mTextColor);
                        break;
                    case R.styleable.CountDownProgress_text_size:
                        mTextSize = (int) a.getDimension(attr, mTextSize);
                        break;
                }
            }
        } finally {
            if (a != null)
                a.recycle();
        }

        mCircleBound = new RectF(0, 0, mCircleRadius * 2, mCircleRadius * 2);
    }

    private void initPaint() {
        //默认圆
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setDither(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);
        mCirclePaint.setColor(mCircleStrokeColor);//这里先画边框的颜色，后续再添加画笔画实心的颜色

        //默认圆上面的进度弧度
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setDither(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);//设置画笔笔刷样式

        //文字画笔
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setDither(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setStrokeWidth(mCircleStrokeWidth);
        mBackgroundPaint.setColor(mProgressColor);//这里先画边框的颜色，后续再添加画笔画实心的颜色
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        canvas.translate(getPaddingLeft(), getPaddingTop());
        //背景圆
        canvas.drawCircle(mCircleRadius, mCircleRadius, mCircleRadius, mBackgroundPaint);
        //画默认圆
        canvas.drawCircle(mCircleRadius, mCircleRadius, mCircleRadius, mCirclePaint);
        //画进度圆弧
        canvas.drawArc(mCircleBound, START_SWEEP, mCurrentAngle, false, mProgressPaint);
        //画中间文字
        float textWidth = mTextPaint.measureText(mTextDesc);
        float textHeight = (mTextPaint.descent() + mTextPaint.ascent()) / 2;
        canvas.drawText(
                mTextDesc,
                mCircleRadius - textWidth / 2,
                mCircleRadius - textHeight,
                mTextPaint
        );

        canvas.restore();
    }

    /**
     * 如果该View布局的宽高开发者没有精确的告诉，则需要进行测量，如果给出了精确的宽高则我们就不管了
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize;
        int heightSize;
        int strokeWidth = Math.max(mCircleStrokeWidth, mProgressWidth);

        //精确指定宽高
        if (widthMode != MeasureSpec.EXACTLY) {
            widthSize = getPaddingLeft() + mCircleRadius * 2 + strokeWidth + getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            heightSize = getPaddingTop() + mCircleRadius * 2 + strokeWidth + getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void startCountDownTime(final OnCountdownFinishListener ls) {
        mCancelled = false;
        setClickable(true);
        mAnimator = ValueAnimator.ofFloat(0, 1.0f);
        mAnimator.setDuration(mCountdownTime);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatCount(0);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentAngle = (float) animation.getAnimatedValue() * 360.f;
                invalidate();
            }
        });

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (ls != null) {
                    ls.onCountdownFinished();
                }
                if (mCountdownTime > 0) {
                    setClickable(true);
                } else {
                    setClickable(false);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        mAnimator.start();
        startCountDown();
    }

    public void cancelCountDown() {
        if (!mCancelled && mTimer != null) {
            mCancelled = true;
            mTimer.cancel();
            mTimer = null;

            if (mAnimator != null)
                mAnimator.cancel();
        }
    }

    public boolean isCountDownCancelled() {
        return mCancelled;
    }

    private void startCountDown() {
        mTimer = new CountDownTimer(mCountdownTime + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mCountdownTime = mCountdownTime - 1000;
                mTextDesc = "跳过(" + ((mCountdownTime / 1000)) + ")";
                invalidate();
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    public void setCountdownTime(long countdownTime) {
        mCountdownTime = countdownTime;
        mTextDesc = countdownTime / 1000 + "″";
    }

    public interface OnCountdownFinishListener {
        void onCountdownFinished();
    }
}

