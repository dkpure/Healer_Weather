package com.sjxz.moji_weather.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AnimationUtils;

/**
 * @author WYH_Healer
 * @email 3425934925@qq.com
 * Created by xz on 2017/2/13.
 * Role:
 */
public class DynamicWeatherView extends SurfaceView implements SurfaceHolder.Callback {

    private final DrawThread mDrawThread;

    public DynamicWeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mDrawThread = new DrawThread();
        init(context);
    }

    private BaseDrawer preDrawer, curDrawer;
    private float curDrawerAlpha = 0f;
    private BaseDrawer.Type curType = null;
    private int mWidth, mHeight;

    private void init(Context context) {
        curDrawerAlpha = 0f;
        final SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        mDrawThread.start();
    }

    private void setDrawer(BaseDrawer baseDrawer) {
        if (baseDrawer == null) {
            return;
        }

        curDrawerAlpha = 0f;
        if (this.curDrawer != null) {
            this.preDrawer = curDrawer;
        }
        this.curDrawer = baseDrawer;
    }

    public void setDrawerType(BaseDrawer.Type type) {
        if (type == null) {

            return;
        }

        if (type != curType) {
            curType = type;
            setDrawer(BaseDrawer.makeDrawerByType(getContext(), curType));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    private boolean drawSurface(Canvas canvas) {
        final int w = mWidth;
        final int h = mHeight;

        if (w == 0 || h == 0) {
            return true;
        }

        boolean needDrawNextFrame = false;
        if (curDrawer != null) {
            curDrawer.setSize(w, h);
            //curDrawerAlpha渐变切入，顺应界面和谐，切换后之前的界面渐变
            needDrawNextFrame = curDrawer.draw(canvas, 1);
        }

        if (preDrawer != null && curDrawerAlpha < 1f) {
            needDrawNextFrame = true;
            preDrawer.setSize(w, h);
            preDrawer.draw(canvas, 1f - curDrawerAlpha);
        }

        if (curDrawerAlpha < 1f) {
            curDrawerAlpha += 0.04f;
            if (curDrawerAlpha > 1) {
                curDrawerAlpha = 1f;
                preDrawer = null;
            }
        }

        return needDrawNextFrame;
    }

    public void onResume() {
        // Let the drawing thread resume running.
        mDrawThread.onResume();
    }

    public void onPause() {
        // Make sure the drawing thread is not running while we are paused.
        mDrawThread.onPause();
    }

    public void onDestroy() {
        // Make sure the drawing thread goes away.
        mDrawThread.onQuit();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Tell the drawing thread that a surface is available.
        synchronized (mDrawThread) {
            mDrawThread.setSurfaceHolder(holder);
            mDrawThread.notify();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // We need to tell the drawing thread to stop, and block until it has done so.
        synchronized (mDrawThread) {
            mDrawThread.setSurfaceHolder(holder);
            mDrawThread.notify();
            while (mDrawThread.isRendering()) {
                try {
                    mDrawThread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        holder.removeCallback(this);
    }

    private class DrawThread extends Thread {
        private volatile SurfaceHolder mSurface;
        private volatile boolean mRunning;
        private volatile boolean mActive;
        private volatile boolean mQuit;

        boolean isRendering() {
            return mActive;
        }

        void setSurfaceHolder(SurfaceHolder surfaceHolder) {
            mSurface = surfaceHolder;
        }

        void onResume() {
            mRunning = true;
            synchronized (this) {
                notify();
            }
        }

        void onPause() {
            mRunning = false;
            synchronized (this) {
                notify();
            }
        }

        void onQuit() {
            mQuit = true;
            synchronized (this) {
                notify();
            }
        }

        @Override
        public void run() {
            while (true) {
                // Log.i(TAG, "DrawThread run..");
                // Synchronize with activity: block until the activity is ready
                // and we have a surface; report whether we are active or
                // inactive
                // at this point; exit thread when asked to quit.

                synchronized (this) {
                    while (mSurface == null || !mRunning) {
                        if (mActive) {
                            mActive = false;
                            notify();
                        }

                        if (mQuit)
                            return;

                        try {
                            wait();
                        } catch (InterruptedException e) {
                        }
                    }

                    if (!mActive) {
                        mActive = true;
                        notify();
                    }

                    final long startTime = AnimationUtils.currentAnimationTimeMillis();
                    Canvas canvas = mSurface.lockCanvas();
                    if (canvas != null) {
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        drawSurface(canvas);
                        mSurface.unlockCanvasAndPost(canvas);
                    }

                    final long drawTime = AnimationUtils.currentAnimationTimeMillis() - startTime;
                    final long needSleepTime = 16 - drawTime;
                    if (needSleepTime > 0) {
                        try {
                            Thread.sleep(needSleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
