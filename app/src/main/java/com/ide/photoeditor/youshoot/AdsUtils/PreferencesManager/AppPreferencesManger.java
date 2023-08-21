package com.ide.photoeditor.youshoot.AdsUtils.PreferencesManager;

import android.content.Context;

import com.google.gson.Gson;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdsJsonPOJO;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;

public class AppPreferencesManger {
    private AppPreferences appPreference;

    public AppPreferencesManger(Context context) {
        appPreference = AppPreferences.getAppPreferences(context);
    }

    public void setAdsModel(AdsJsonPOJO adsJsonPOJO) {
        appPreference.putString(Constants.ADSJSON, new Gson().toJson(adsJsonPOJO));
    }
    public String getAdsModel() {
        return appPreference.getString(Constants.ADSJSON,"");
    }

}
