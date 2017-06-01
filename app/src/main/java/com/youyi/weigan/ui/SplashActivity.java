package com.youyi.weigan.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.view.Window;

import com.youyi.weigan.R;

public class SplashActivity extends AppCompatActivity {

    private final int DURATION = 500;
    private Window window;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        window = getWindow();
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        setContentView(R.layout.activity_splash);
        showSplash();
        new Thread(new Runnable() {
            @Override
            public void run() {
                preLoadResourse();
            }
        }).start();
    }

    View mainPage = null;

    private void preLoadResourse() {
        if (mainPage != null) return;
        mainPage = View.inflate(this, R.layout.activity_main, null);
    }

    private class SplashRunnable implements Runnable {

        @Override
        public void run() {

            ActivityOptions option = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                option = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this);
            }

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setExitTransition(new Slide().setDuration(DURATION));//A打开B的时候，A中的View是如何播放动画的
                window.setEnterTransition(new Slide().setDuration(DURATION));//A打开B的时候，B中的View是如何播放动画的
                startActivity(intent, option.toBundle());
            }
            startActivity(intent);
            finish();
        }
    }

    private Handler handler;
    private SplashRunnable splashRunnable;

    private void showSplash() {
        handler = new Handler();
        splashRunnable = new SplashRunnable();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler == null || splashRunnable == null) return;
        handler.removeCallbacks(splashRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (handler == null || splashRunnable == null) return;
        handler.postDelayed(splashRunnable, 1000);

    }
}
