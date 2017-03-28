package com.sjxz.moji_weather.activity;

import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sjxz.moji_weather.MainActivity;
import com.sjxz.moji_weather.R;
import com.sjxz.moji_weather.base.BaseActivity;
import com.sjxz.moji_weather.helper.EventCenter;
import com.sjxz.moji_weather.mvp.presenter.Presenter;
import com.sjxz.moji_weather.mvp.presenter.impl.SplashPresenterImpl;
import com.sjxz.moji_weather.mvp.view.SplashView;
import com.sjxz.moji_weather.view.CountDownView;

import butterknife.Bind;

public class SplashActivity extends BaseActivity implements SplashView {

    @Bind(R.id.splash_logodiscribe)
    TextView mTvDescription;

    @Bind(R.id.splash_version_name)
    TextView mTvVersion;

    @Bind(R.id.splash_copyright)
    TextView mTvCopyRight;

    @Bind(R.id.splash_image)
    ImageView mIvSplash;

    @Bind(R.id.countdwonview)
    CountDownView mCountDownView;

    private Presenter mSplashPresenterImpl;

    @Override
    protected boolean isApplyKitKatTranslucency() {
        return false;
    }

    @Override
    protected void onEventComming(EventCenter eventCenter) {
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionType() {
        return TransitionMode.FADE;
    }

    @Override
    protected boolean isBindEventBus() {
        return false;
    }

    @Override
    protected boolean isApplyStatusBarTranslucency() {
        return true;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_splash;
    }


    @Override
    protected void initViewsAndEvents() {
        mSplashPresenterImpl = new SplashPresenterImpl(this, this);
        mSplashPresenterImpl.initialzation();
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    public void animateBackgroundImage(Animation animation) {
        mCountDownView.setCountdownTime(4 * 1000);
        mCountDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCountDownView.isCountDownCancelled()) {
                    mCountDownView.cancelCountDown();
                    mIvSplash.clearAnimation();
                }
            }
        });
        mCountDownView.startCountDownTime(null);

        mIvSplash.startAnimation(animation);
    }

    @Override
    public void initializeViews(String versionName, String copyright, int backgroundResId) {
        mTvVersion.setText(versionName);
        if (backgroundResId == R.mipmap.morning) {
            mTvDescription.setText(getString(R.string.splash_logodiscribe_morning));
        } else if (backgroundResId == R.mipmap.afternoon) {
            mTvDescription.setText(getString(R.string.splash_logodiscribe_afternoon));
        } else if (backgroundResId == R.mipmap.night) {
            mTvDescription.setText(getString(R.string.splash_logodiscribe_night));
        }
        mTvCopyRight.setText(copyright);
        mIvSplash.setImageResource(backgroundResId);
    }

    @Override
    public void readToMain() {
        readyGoThenKill(MainActivity.class);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCountDownView != null && !mCountDownView.isCountDownCancelled()) {
            mCountDownView.cancelCountDown();
            mIvSplash.clearAnimation();
        }
    }

}
