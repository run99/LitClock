package com.lifeistech.android.litclock;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    // 画面向きを指定する変数だよ変更してみよう
    // true->横向き, false->縦向き
    private final static boolean SCREEN_ORIENTATION = false;

    // 背景を時間ごとに変更するかどうかの設定
    // true->変更する, false->変更しない
    private final static boolean CHANGE_BACKGROUND = true;

    // 背景画像が変わる時間
    // 画像を変える間隔を秒でセットしよう！
    private final int CHANGE_INTERVAL = 10;

    private static final String TIME_ZONE = "Asia/Tokyo";
    private static final TimeZone sTimeZone = TimeZone.getTimeZone(TIME_ZONE);

    // どの時計を動かすかを指定する
    private static final int HOUR_TEN = 0;
    private static final int HOUR_ONE = 1;
    private static final int MIN_TEN = 3;
    private static final int MIN_ONE = 4;
    private static final int SEC_TEN = 6;
    private static final int SEC_ONE = 7;
    private static final int COLON_1 = 2;
    private static final int COLON_2 = 5;
    // 動かすImageViewのリソース
    private final int[] IMAGEVIEW_RESOURCES = {
            R.id.imageViewHourTen,
            R.id.imageViewHourOne,
            R.id.imageViewColon1,
            R.id.imageViewMinuteTen,
            R.id.imageViewMinuteOne,
            R.id.imageViewColon2,
            R.id.imageViewSecondTen,
            R.id.imageViewSecondOne
    };
    // 動かすImageView
    private ImageView[] mImageViews = new ImageView[IMAGEVIEW_RESOURCES.length];
    // 全体のトップのレイアウト
    private View mRootLayout;
    // 画像のリソース一覧
    private int[] mNumberImageResources;
    // 背景画像のリソース一覧
    private ArrayList<Integer> mBackGroundImageList = new ArrayList<Integer>();
    // 時計を動かすタイマー
    private Timer mTimer;
    private Handler mHandler = new Handler();
    private int mBackgrounImagedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(SCREEN_ORIENTATION ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        mRootLayout = findViewById(R.id.root);
        // 時間の表示用ImageViewを設定する
        for (int i = 0; i < mImageViews.length; i++) {
            mImageViews[i] = (ImageView) findViewById(IMAGEVIEW_RESOURCES[i]);
        }
        loadNumberImageResources();
        // :(コロン)の画像を設定する
        setImage(COLON_1, 10);
        setImage(COLON_2, 10);
        // 背景画像を設定する
        if (CHANGE_BACKGROUND) {
            loadBackgroundImageResources();
            Calendar calendar = Calendar.getInstance(Locale.JAPAN);
            changeBackground(calendar.getTime().getTime(), true);
        } else {
            int backgroundId = getResources().getIdentifier("haikei", "drawable", getPackageName());
            if (backgroundId != 0) {
                mRootLayout.setBackgroundResource(backgroundId);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mRootLayout.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
    }

    // 時計の動かす処理
    private void startTimer() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Calendar calendar = Calendar.getInstance(sTimeZone);
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        setImage(HOUR_TEN, hour / 10);
                        setImage(HOUR_ONE, hour % 10);
                        int minute = calendar.get(Calendar.MINUTE);
                        setImage(MIN_TEN, minute / 10);
                        setImage(MIN_ONE, minute % 10);
                        int second = calendar.get(Calendar.SECOND);
                        setImage(SEC_TEN, second / 10);
                        setImage(SEC_ONE, second % 10);

                        // 背景の変更処理
                        if (CHANGE_BACKGROUND) {
                            changeBackground(calendar.getTime().getTime(), false);
                        }
                    }
                });
            }
        }, 0, 700);
    }

    // 時計を止める処理
    private void stopTimer() {
        mTimer.cancel();
        mTimer = null;
    }

    // 画像のIDを読み込む
    private void loadNumberImageResources() {
        Resources resources = getResources();
        mNumberImageResources = new int[]{
                getNumberImageResource(resources, "zero"),
                getNumberImageResource(resources, "one"),
                getNumberImageResource(resources, "two"),
                getNumberImageResource(resources, "three"),
                getNumberImageResource(resources, "four"),
                getNumberImageResource(resources, "five"),
                getNumberImageResource(resources, "six"),
                getNumberImageResource(resources, "seven"),
                getNumberImageResource(resources, "eight"),
                getNumberImageResource(resources, "nine"),
                getNumberImageResource(resources, "colon")
        };
    }

    private int getNumberImageResource(Resources resources, String name) {
        int drawableId = resources.getIdentifier(name, "drawable", getPackageName());
        return drawableId != 0 ?
                drawableId :
                resources.getIdentifier(name, "mipmap", getPackageName());
    }

    private void loadBackgroundImageResources() {
        Resources resources = getResources();
        for (int i = 1; ; i++) {
            int drawableId = resources.getIdentifier("haikei" + String.valueOf(i), "drawable", getPackageName());
            if (drawableId != 0) {
                mBackGroundImageList.add(new Integer(drawableId));
            } else {
                break;
            }
        }
    }

    // 画像を時計に設定する処理
    public void setImage(int index, int now) {
        // 時計の時間設定する
        mImageViews[index].setImageResource(mNumberImageResources[now]);
    }

    private void changeBackground(long time, boolean force) {
        time = time / 1000;
        if (mBackGroundImageList.size() > 0) {
            if (force || time % CHANGE_INTERVAL  == 0) {
                mRootLayout.setBackgroundResource(mBackGroundImageList.get(mBackgrounImagedIndex % mBackGroundImageList.size()));
                mBackgrounImagedIndex++;
            }
        }
    }

}
