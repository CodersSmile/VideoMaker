package com.ide.photoeditor.youshoot.vpnService;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferences {
    SharedPreferences preferences;
    Context context;

    public SharePreferences(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void setFCMToken(String token) {
        preferences.edit().putString("FCM_Token", token).apply();
    }

    public String getFCMToken(){
        return preferences.getString("FCM_Token", "");
    }

    public void setAdmob_Appid(String appid) {
        preferences.edit().putString("app", appid).apply();
    }

    public String getAdmob_Appid() {
        return preferences.getString("app", "");
    }

    public void setAdmob_Banner(String banner) {
        preferences.edit().putString("banner", banner).apply();
    }

    public String getAdmob_Banner() {
        return preferences.getString("banner", "");
    }

    public void setAdmob_Interstial(String interstial) {
        preferences.edit().putString("ins", interstial).apply();
    }

    public String getAdmob_Interstial() {
        return preferences.getString("ins", "");
    }

    public void setAdmob_Native(String natives) {
        preferences.edit().putString("native", natives).apply();
    }

    public String getAdmob_Native() {
        return preferences.getString("native", "");
    }

    public void setAdmob_nativebanner(String nativebanner) {
        preferences.edit().putString("nativebanner", nativebanner).apply();
    }

    public String getAdmob_nativebanner() {
        return preferences.getString("nativebanner", "");
    }

    public void setAppOpbId(String appOpbId) {
        preferences.edit().putString("app_poen", appOpbId).apply();
    }

    public String getAppOpbId() {
        return preferences.getString("app_poen", "");
    }

    public void setAd_show_Status(String ad_show_status) {
        preferences.edit().putString("adshow", ad_show_status).apply();
    }

    public String getAd_show_Status() {
        return preferences.getString("adshow", "");
    }

    public void setBack_Ads(String show_ads) {
        preferences.edit().putString("dis", show_ads).apply();
    }

    public String getBack_ads() {
        return preferences.getString("dis", "");
    }

    public void setAd_show_Admob(String ad_show_admob) {
        preferences.edit().putString("ad_show", ad_show_admob).apply();
    }

    public String getAd_show_Admob() {
        return preferences.getString("ad_show", "");
    }

    public void setPrivacy(String privacy) {
        preferences.edit().putString("privacy", privacy).apply();
    }

    public String getPrivacy() {
        return preferences.getString("privacy", "");
    }

    public void setApp_open_Start(String start) {
        preferences.edit().putString("starts", start).apply();
    }

    public String getApp_open_Start() {
        return preferences.getString("starts", "");
    }

    public void setBackCountAds(int countAds) {
        preferences.edit().putInt("backcount", countAds).apply();
    }

    public void setSkip(int skip) {
        preferences.edit().putInt("skip", skip).apply();
    }

    public int getSkip() {
        return preferences.getInt("skip", 0);
    }

    public int getBackCountAds() {
        return preferences.getInt("backcount", 0);
    }

    public void setContinueCount(String count) {
        preferences.edit().putString("counts", count).apply();
    }

    public String getContinueCount() {
        return preferences.getString("counts", "");
    }

    public void setMainCounter(int str) {
        preferences.edit().putInt("maincount", str).apply();
    }

    public int getMainCounter() {
        return preferences.getInt("maincount", 0);
    }

    public void setMainIner(int iner) {
        preferences.edit().putInt("maininner", iner).apply();
    }

    public int getMainIner() {
        return preferences.getInt("maininner", 0);
    }

    public void setScreenFlag(int str) {
        preferences.edit().putInt("screen", str).apply();
    }

    public int getScreenFlag() {
        return preferences.getInt("screen", 0);
    }

    public void setBackendUrl(String backendUrl) {
        preferences.edit().putString("backend_url", backendUrl).apply();
    }

    public void setCarrier(String carrier) {
        preferences.edit().putString("carrier", carrier).apply();
    }

    public String getBackendUrl() {
        return preferences.getString("backend_url", "");
    }

    public String getKey() {
        return preferences.getString("carrier", "");
    }

    public void setVpnStatus(int vpn_status) {
        preferences.edit().putInt("vpn_status", vpn_status).apply();
    }

    public void setVpnButtonStatus(int vpn_button) {
        preferences.edit().putInt("vpn_button", vpn_button).apply();
    }

    public int getVpnStatus() {
        return preferences.getInt("vpn_status", 1);
    }

    public int getVpnButtonStatus() {
        return preferences.getInt("vpn_button", 1);
    }

    public int getVpnClose() {
        return preferences.getInt("vpn_close_button", 1);
    }

    public void setVpnClose(int vpnSkipButton) {
        preferences.edit().putInt("vpn_close_button", vpnSkipButton).apply();
    }

    public void setcloseBottonBanner(boolean status) {
        preferences.edit().putBoolean("banner_status", status).apply();
    }
    public boolean getcloseBottonBanner() {
        return preferences.getBoolean("banner_status", false);
    }
    public void setfirstTime(boolean firsttime) {
        preferences.edit().putBoolean("fist_time", firsttime).apply();
    }
    public boolean getfirstTime() {
        return preferences.getBoolean("fist_time", true);
    }
    public void setDate(String date) {
        preferences.edit().putString("dates", date).apply();

    }
    public String getDate() {
        return preferences.getString("dates", "");
    }
    public void setNumberkey(String key)
    {
        preferences.edit().putString("key",key).apply();
    }
    public String getNumberkey()
    {
        return preferences.getString("key","");
    }

    public void setSecureBox(boolean status)
    {
        preferences.edit().putBoolean("status_check",status).apply();
    }

    public boolean getSecureBox()
    {

        return preferences.getBoolean("status_check",false);
    }
}
