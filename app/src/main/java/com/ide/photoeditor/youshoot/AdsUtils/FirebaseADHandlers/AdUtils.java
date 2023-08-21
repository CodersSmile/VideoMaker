package com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.ide.photoeditor.youshoot.AdsUtils.Interfaces.AppInterfaces;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.ConnectionDetector;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Global;
import com.ide.photoeditor.youshoot.R;

public class AdUtils {

    static NativeAd google_unifiedNativeAd;
    private static ConnectionDetector cd;
    private static InterstitialAd mpreloadAds = null;
    private static AppOpenAd appOpenAd;
    private static Global global;


    public static void showRewardAd(Activity activity) {
        cd = new ConnectionDetector(activity);

        if (cd.isConnectingToInternet()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(activity, Constants.adsJsonPOJO.getParameters().getRewarded_ad().getValueType(), adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Global.printLog("RewardedAdLoadCallback: ", loadAdError.toString());
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    rewardedAd.show(activity, new OnUserEarnedRewardListener() {

                        @Override
                        public void onUserEarnedReward(RewardItem rewardItem) {
                            //TODO user earned reward --Not to implement now--
                        }
                    });
                    rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                        @Override
                        public void onAdShowedFullScreenContent() {
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                            Global.printLog("onAdFailedToShowFullScreenContent: ", adError.toString());
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Global.printLog("onAdFailedToShowFullScreenContent: ", "onAdDismissedFullScreenContent");
                        }
                    });
                }

            });
        }
    }


    public static void showNativeAd(Activity activity, String nativeAd, LinearLayout adContainer, boolean isFullScreenAd) {
        if (Boolean.parseBoolean(Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue())) {
            cd = new ConnectionDetector(activity);
            if (!Global.isEmptyStr(nativeAd)) {
                if (cd.isConnectingToInternet()) {
                    try {
                        AdLoader adLoader = new AdLoader.Builder(activity, nativeAd).forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(NativeAd nativeAd) {
                                google_unifiedNativeAd = nativeAd;
                                NativeAdView unifiedNativeAdView;
                                try {
                                    if (isFullScreenAd) {
                                        unifiedNativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.full_native_ad_param, null);
                                    } else {
                                        unifiedNativeAdView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.small_native_ad, null);
                                    }
                                    unifiedNativeAdView.isHardwareAccelerated();
                                    populateUnifiedNativeAdView(google_unifiedNativeAd, unifiedNativeAdView, isFullScreenAd);

                                    adContainer.removeAllViews();
                                    adContainer.addView(unifiedNativeAdView);
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }).withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {

                                try {
                                    adContainer.setVisibility(View.GONE);
                                    return;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                String stringBuilder = adError + "";
                                Log.i("onAdFailedToLoad", stringBuilder.trim());
                            }
                        }).withNativeAdOptions(new NativeAdOptions.Builder().build()).build();
                        adLoader.loadAd(new AdRequest.Builder().build());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                adContainer.setVisibility(View.GONE);
            }
        } else {
            adContainer.setVisibility(View.GONE);
        }
    }

    public static void showBannerAd(LinearLayout adContainer, Activity activity) {
        cd = new ConnectionDetector(activity);


        if (!Global.isEmptyStr(Constants.adsJsonPOJO.getParameters().getBanner_id().getDefaultValue().getValue())) {
            if (cd.isConnectingToInternet()) {
                AdView mAdView = new AdView(activity);
                if (cd.isConnectingToInternet()) {
                    AdRequest adRequest = new AdRequest.Builder().build();
                    try {
                        mAdView.setVisibility(View.VISIBLE);
                        mAdView.setEnabled(true);
                        mAdView.setAdSize(AdSize.BANNER);
                        mAdView.setAdUnitId(Constants.adsJsonPOJO.getParameters().getBanner_id().getDefaultValue().getValue());
                        adContainer.setVisibility(View.VISIBLE);

                        mAdView.setAdListener(new AdListener() {
                            public void onAdFailedToLoad(int i) {
                                adContainer.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAdClicked() {
                                super.onAdClicked();
                                //                        sAppOpenadsFLAG = false;
                            }

                            public void onAdLoaded() {
                            }

                            public void onAdOpened() {
                            }

                            public void onAdLeftApplication() {
                            }

                            public void onAdClosed() {
                            }
                        });

                        mAdView.loadAd(adRequest);
                        adContainer.removeAllViews();
                        adContainer.setPadding(5, 5, 5, 5);
                        adContainer.addView(mAdView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }
                mAdView.setVisibility(View.GONE);
                adContainer.setVisibility(View.GONE);
                adContainer.removeAllViews();
            }
        } else {
            adContainer.setVisibility(View.GONE);
            adContainer.removeAllViews();
        }
    }

//    public static void showInterstitialAd(Activity activity, AppInterfaces.InterStitialADInterface interStitialADInterface) {
//        cd = new ConnectionDetector(activity);
//        global = new Global(activity);
//        global.showProgressDialog(activity, ConstantsMessages.PLEASE_WAIT);
//        if (cd.isConnectingToInternet()) {
//            AdRequest adRequest = new AdRequest.Builder().build();
//            InterstitialAd.load(activity, Constants.adsJsonPOJO.getParameters().getFull_id().getDefaultValue().getValue(), adRequest, new InterstitialAdLoadCallback() {
//                @Override
//                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                    global.hideProgressDialog();
//
//                    mpreloadAds = interstitialAd;
//                    loadInterstitialAd(activity, interStitialADInterface);
//                }
//
//                @Override
//                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                    global.hideProgressDialog();
//                    interStitialADInterface.adLoadState(false);
//                }
//            });
//        }
//    }

    public static void showInterstitialAd(Activity activity, AppInterfaces.InterStitialADInterface interStitialADInterface) {
        if (Boolean.parseBoolean(Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue())) {

            cd = new ConnectionDetector(activity);
            global = new Global(activity);

            if (cd.isConnectingToInternet()) {
                if (Constants.hitCounter == Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits())) {
                    Constants.hitCounter = 0;
                    global.showProgressDialog(activity, Global.PLEASE_WAIT);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    InterstitialAd.load(activity, Constants.adsJsonPOJO.getParameters().getFull_id().getDefaultValue().getValue(), adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            global.hideProgressDialog();

                            mpreloadAds = interstitialAd;
                            loadInterstitialAd(activity, interStitialADInterface);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            global.hideProgressDialog();
                            interStitialADInterface.adLoadState(false);
                        }
                    });
                } else {
                    Constants.hitCounter++;
                    interStitialADInterface.adLoadState(false);

                }

            }
        } else {
            interStitialADInterface.adLoadState(false);
        }
    }


    public static void showAppOpenAd(Activity activity, AppInterfaces.AppOpenADInterface appOpenADInterface) {
        if (Boolean.parseBoolean(Constants.adsJsonPOJO.getParameters().getShowAd().getDefaultValue().getValue())) {

            global = new Global(activity);

            AdRequest adRequest = new AdRequest.Builder().build();
            if (!Global.isEmptyStr(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getValue())) {
                global.showProgressDialog(activity, Global.PLEASE_WAIT);
                AppOpenAd.load(activity, Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getValue(), adRequest, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, new AppOpenAd.AppOpenAdLoadCallback() {

                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        global.hideProgressDialog();
                        appOpenAd = ad;
                        showAdIfAvailable(activity, appOpenADInterface);
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Global.printLog("adsLoading Failed==", loadAdError.getMessage());
                        global.hideProgressDialog();
                        // Handle the error.
                        appOpenADInterface.appOpenAdState(false);
                    }

                });
            } else {
                appOpenADInterface.appOpenAdState(false);
            }
        } else {
            appOpenADInterface.appOpenAdState(false);
        }
    }

    private static void showAdIfAvailable(Activity activity, AppInterfaces.AppOpenADInterface appOpenADInterface) {
        FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null;
                appOpenADInterface.appOpenAdState(true);
//                            fetchAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                appOpenADInterface.appOpenAdState(false);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                /*appOpenADInterface.appOpenAdState(true);*/
            }
        };

        appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
        appOpenAd.show(activity);

    }

    private static void loadInterstitialAd(Activity activity, AppInterfaces.InterStitialADInterface interStitialADInterface) {
        InterstitialAd interstitialAd = mpreloadAds;
        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                @Override
                public void onAdDismissedFullScreenContent() {
                    interStitialADInterface.adLoadState(true);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    interStitialADInterface.adLoadState(false);
                }

                @Override
                public void onAdShowedFullScreenContent() {
                }
            });
            interstitialAd.show(activity);
        } else {
            interStitialADInterface.adLoadState(false);
        }
    }

    private static void populateUnifiedNativeAdView(NativeAd unifiedNativeAd, NativeAdView unifiedNativeAdView, boolean flag) {
        if (flag) {
            unifiedNativeAdView.setMediaView((MediaView) unifiedNativeAdView.findViewById(R.id.ad_media));
        }
        unifiedNativeAdView.setHeadlineView(unifiedNativeAdView.findViewById(R.id.ad_headline));
        unifiedNativeAdView.setBodyView(unifiedNativeAdView.findViewById(R.id.ad_body));
        unifiedNativeAdView.setCallToActionView(unifiedNativeAdView.findViewById(R.id.ad_call_to_action));
        unifiedNativeAdView.setIconView(unifiedNativeAdView.findViewById(R.id.ad_app_icon));
        unifiedNativeAdView.setPriceView(unifiedNativeAdView.findViewById(R.id.ad_price));
        unifiedNativeAdView.setStarRatingView(unifiedNativeAdView.findViewById(R.id.ad_stars));
        unifiedNativeAdView.setStoreView(unifiedNativeAdView.findViewById(R.id.ad_store));
        unifiedNativeAdView.setAdvertiserView(unifiedNativeAdView.findViewById(R.id.ad_advertiser));
        ((TextView) unifiedNativeAdView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
        if (unifiedNativeAd.getBody() == null) {
            unifiedNativeAdView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) unifiedNativeAdView.getBodyView()).setText(unifiedNativeAd.getBody());
        }
        if (unifiedNativeAd.getCallToAction() == null) {
            unifiedNativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) unifiedNativeAdView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());
        }
        if (unifiedNativeAd.getIcon() == null) {
            unifiedNativeAdView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) unifiedNativeAdView.getIconView()).setImageDrawable(unifiedNativeAd.getIcon().getDrawable());
            unifiedNativeAdView.getIconView().setVisibility(View.VISIBLE);
        }
        if (unifiedNativeAd.getPrice() == null) {
            unifiedNativeAdView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) unifiedNativeAdView.getPriceView()).setText(unifiedNativeAd.getPrice());
        }
        if (unifiedNativeAd.getStore() == null) {
            unifiedNativeAdView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) unifiedNativeAdView.getStoreView()).setText(unifiedNativeAd.getStore());
        }
        if (unifiedNativeAd.getStarRating() == null) {
            unifiedNativeAdView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) unifiedNativeAdView.getStarRatingView()).setRating(unifiedNativeAd.getStarRating().floatValue());
            unifiedNativeAdView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        if (unifiedNativeAd.getAdvertiser() == null) {
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) unifiedNativeAdView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        unifiedNativeAdView.setNativeAd(unifiedNativeAd);
    }


}