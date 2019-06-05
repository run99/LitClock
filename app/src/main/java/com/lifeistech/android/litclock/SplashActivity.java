package com.lifeistech.android.litclock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Handler handler = new Handler();
        Timer timer = new Timer(false);
        timer.schedule(new TimerTask() {
                           @Override
                           public void run() {
                               handler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       // メイン画面に遷移する
                                       Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                       startActivity(intent);
                                       finish();
                                   }
                               });
                           }
                       },
                1800);

    }
}
