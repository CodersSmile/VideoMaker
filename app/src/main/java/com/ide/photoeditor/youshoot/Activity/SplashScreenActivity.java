package com.ide.photoeditor.youshoot.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;
import android.view.View;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.widget.TextView;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdsJsonPOJO;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.FirebaseUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Interfaces.AppInterfaces;
import com.ide.photoeditor.youshoot.AdsUtils.PreferencesManager.AppPreferencesManger;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Global;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.StringUtils;
import com.ide.photoeditor.youshoot.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ide.photoeditor.youshoot.vpnService.LoginListener;
import com.ide.photoeditor.youshoot.vpnService.VPNInitiator_Handler;
import com.ide.photoeditor.youshoot.vpnService.VpnConnectListener;

public class SplashScreenActivity extends BaseFullActivity {

    Activity activity;
    private RelativeLayout rltest;
    private AdsJsonPOJO adsJsonPOJO;
    Thread timer;
    String userId = null;
    FirebaseAuth auth;
    AppPreferencesManger appPreferences;
    static FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_splash_screen);
        auth = FirebaseAuth.getInstance();
        userId = auth.getUid();
        activity = SplashScreenActivity.this;
        appPreferences = new AppPreferencesManger(activity);
        AppPreferencesManger appPreferencesManger = new AppPreferencesManger(this);

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.ADSJSON);

        Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());
        new VPNInitiator_Handler(activity, new VPNInitiator_Handler.VPNInterface() {
            @Override
            public void handlePostAd() {
                nextActivity();
            }
        });
        /*if (Constants.adsJsonPOJO != null && !StringUtils.isNull(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getValue())) {
            Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());
            Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
            AdUtils.showAppOpenAd(activity, new AppInterfaces.AppOpenADInterface() {
                @Override
                public void appOpenAdState(boolean state_load) {
                    nextActivity();
                }
            });


        } else {
            FirebaseUtils.initiateAndStoreFirebaseRemoteConfig(activity, new AppInterfaces.AdDataInterface() {
                @Override
                public void getAdData(AdsJsonPOJO adsJsonPOJO) {
                    //Need to call this only once per
                    appPreferencesManger.setAdsModel(adsJsonPOJO);
                    Constants.adsJsonPOJO = adsJsonPOJO;
                    Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
                    AdUtils.showAppOpenAd(activity, new AppInterfaces.AppOpenADInterface() {
                        @Override
                        public void appOpenAdState(boolean state_load) {
                            nextActivity();
                        }
                    });
                }
            });
//
        }*/

    }
    public void nextActivity() {
        timer = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
                    Intent intent = new Intent(SplashScreenActivity.this, IntroActivity.class);
                    startActivity(intent);
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
                    finish();
                }
            }
        };
        timer.start();

    }
}