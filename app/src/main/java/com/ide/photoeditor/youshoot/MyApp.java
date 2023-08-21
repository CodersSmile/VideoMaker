package com.ide.photoeditor.youshoot;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Global;
import com.ide.photoeditor.youshoot.cutomView.OnProgressReceiver;
import com.ide.photoeditor.youshoot.cutomView.theme.THEMES;
import com.ide.photoeditor.youshoot.videoeditor.model.MusicData;

import java.util.ArrayList;

public class MyApp extends Application {
    public Context context;
    public static MyApp myApp;
    public static int VIDEO_HEIGHT = 480;
    public static int VIDEO_WIDTH = 720;
    public THEMES selectedTheme = THEMES.Shine;
    public ArrayList<String> videoImages = new ArrayList();
    public int min_pos = Integer.MAX_VALUE;
    public static boolean isBreak = false;
    public boolean isFromSdCardAudio = false;
    private MusicData musicData;
    public boolean isEditModeEnable = false;
    private float second = 4.0f;
    private OnProgressReceiver onProgressReceiver;

    public static MyApp getInstance() {
        return myApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        myApp = this;
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });

    }

    public String getCurrentTheme() {
        return getSharedPreferences("theme", 0).getString("current_theme", THEMES.Shine.toString());
    }

    public void setMusicData(MusicData musicData) {
        this.isFromSdCardAudio = false;
        this.musicData = musicData;
    }

    public void setSecond(float second) {
        this.second = second;
    }

    public MusicData getMusicData() {
        return this.musicData;
    }

    public float getSecond() {
        return this.second;
    }

    public void setCurrentTheme(String currentTheme) {
        SharedPreferences.Editor editor = getSharedPreferences("theme", 0).edit();
        editor.putString("current_theme", currentTheme);
        editor.commit();
    }

    public void initArray() {
        this.videoImages = new ArrayList();
    }

    public OnProgressReceiver getOnProgressReceiver() {
        return this.onProgressReceiver;
    }

    public void setOnProgressReceiver(OnProgressReceiver onProgressReceiver) {
        this.onProgressReceiver = onProgressReceiver;
    }

    public void clearAllSelection() {
        this.videoImages.clear();
        Global.mSelectedImgPath = new ArrayList<>();
        System.gc();
    }

}
