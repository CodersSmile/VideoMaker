package com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.RemoteMessage;
import com.ide.photoeditor.youshoot.AdsUtils.Interfaces.AppInterfaces;
import com.ide.photoeditor.youshoot.AdsUtils.PreferencesManager.AppPreferencesManger;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    public FirebaseMessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (!remoteMessage.getData().isEmpty()) {
            if (remoteMessage.getData().containsValue("10")) {
                FirebaseUtils.initiateAndStoreFirebaseRemoteConfig(getApplicationContext(), new AppInterfaces.AdDataInterface() {
                    @Override
                    public void getAdData(AdsJsonPOJO adsJsonPOJO) {
                        AppPreferencesManger appPreferencesManger = new AppPreferencesManger(getApplicationContext());
                        appPreferencesManger.setAdsModel(adsJsonPOJO);
                    }
                });
            }
        }

    }
}
