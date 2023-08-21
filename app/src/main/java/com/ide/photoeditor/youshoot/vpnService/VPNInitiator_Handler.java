package com.ide.photoeditor.youshoot.vpnService;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdUtils;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdsJsonPOJO;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.FirebaseUtils;
import com.ide.photoeditor.youshoot.AdsUtils.Interfaces.AppInterfaces;
import com.ide.photoeditor.youshoot.AdsUtils.PreferencesManager.AppPreferencesManger;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Constants;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.Global;
import com.ide.photoeditor.youshoot.AdsUtils.Utils.StringUtils;
import com.ide.photoeditor.youshoot.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.northghost.caketube.OpenVpnTransportConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class VPNInitiator_Handler {
    static FirebaseRemoteConfig mFirebaseRemoteConfig;
    private String vpnKey, backendURL;
    private Boolean vpnEnabled = false;
    TextView turen_btn;
    ArrayList<Country_Code> list_country;
    List<Country> list;
    private String selected = "";
    private ClientInfo clientInfo;
    SharePreferences sp;
    private static final String TAG = "VPN Class Tag>";
    Activity activity;
    VPNInterface vpnInterface;
    AlertDialog dialog;

    public VPNInitiator_Handler(Activity activity, VPNInterface vpnInterface) {
        this.activity = activity;
        this.vpnInterface = vpnInterface;
        getCountryList();
        loginVpnMethodSTAR();
    }

    public interface VPNInterface {
        void handlePostAd();
    }

    public void loginVpnMethodSTAR() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    Constants.configData = Utilss.getConfigData(mFirebaseRemoteConfig.getString("vpn_config"));
                    Log.println(Log.ASSERT, TAG, new Gson().toJson(mFirebaseRemoteConfig.getString("vpn_config")));
                    vpnEnabled = Constants.configData.getVpn_enabled();
                    if (vpnEnabled) {
                        backendURL = Constants.configData.getConfig_details().getVpn_url();
                        vpnKey = Constants.configData.getConfig_details().getVpn_key();

                        Log.println(Log.ASSERT, TAG, "vpn_url: '" + backendURL + "'");
                        Log.println(Log.ASSERT, TAG, "vpnKey: '" + vpnKey + "'");
                        setUpVpnData();
                    } else {
                        showsplashAds();
                    }
                }
            }
        });
    }

    private void setUpVpnData() {
        if (!backendURL.isEmpty() && !vpnKey.isEmpty()) {
            if (!backendURL.equals("") && !vpnKey.equals("")) {
                clientInfo = ClientInfo.newBuilder().addUrl(backendURL).carrierId(vpnKey).build();
                UnifiedSDK.clearInstances();
                Utilss.loginClientInfo(clientInfo);
            }

            if (activity.getSharedPreferences(activity.getResources().getString(R.string.app_name), MODE_PRIVATE).getBoolean("isVpnConnected", false)) {
                getCountry();
            } else {
                showDialogVpn();
            }

        } else {
            showsplashAds();
        }
    }

    public void getCountryList() {
        try {
            String a = loadJSONFromAsset("country_short.json", activity);
            JSONObject obj = new JSONObject(a);
            JSONArray m_jArry = obj.getJSONArray("country");
            Utilss.list_Country = new ArrayList<>();
            for (int i = 0; i < m_jArry.length(); i++) {
                Country_Code counteyModal = new Country_Code();
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String code = jo_inside.getString("Code");
                String name = jo_inside.getString("Name");
                counteyModal.setCode(code);
                counteyModal.setName(name);
                Utilss.list_Country.add(counteyModal);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, "Something went wrong. please check again later", Toast.LENGTH_SHORT).show();
        }
    }

    public static String loadJSONFromAsset(String a, Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(a);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void showDialogVpn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.vpn_dialog, null);
        builder.setView(view);
        turen_btn = view.findViewById(R.id.turen_btn);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        turen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                turen_btn.setText("Connecting....");
                getCountry();
            }
        });
    }

    public void getCountry() {
        try {
            Global global = new Global(activity);
            global.showProgressDialog(activity, "Please wait...");
            UnifiedSDK.getInstance().getBackend().countries(new Callback<AvailableCountries>() {
                @Override
                public void success(@NonNull AvailableCountries availableCountries) {
                    global.hideProgressDialog();
                    try {
                        Utilss.list = new ArrayList<>();
                        list_country = new ArrayList<>();
                        list = availableCountries.getCountries();
                        Utilss.list = availableCountries.getCountries();
                        if (Utilss.list_Country.size() > 0) {
                            for (Country country : Utilss.list) {
                                for (Country_Code code : Utilss.list_Country) {

                                    if (country.getCountry().equalsIgnoreCase(code.getCode())) {
                                        Log.e("Country_Size", "Service_List " + code.getCode());
                                        Country_Code new_code = new Country_Code();
                                        new_code.setCode(code.getCode());
                                        list_country.add(new_code);
                                    }
                                }
                            }

                            if (!list_country.isEmpty()) {
                                selected = getRandomString(list_country);

                            } else {
                                selected = "";
                            }
                        } else {

                            if (!list.isEmpty()) {
                                selected = getRandomStringValue(list);
                            } else {
                                selected = "";
                            }
                        }
                        connectToVpn(selected);
                    } catch (Exception e) {
                        global.hideProgressDialog();
                        Log.e("test", "Error " + e.getLocalizedMessage());
                        connectToVpn("");
                    }

                }

                @Override
                public void failure(@NonNull VpnException e) {
                    global.hideProgressDialog();
                    Log.e("test", "Vpn Error " + e.getLocalizedMessage());
                    connectToVpn("");
                }
            });
        } catch (Exception e) {
        }

    }

    private String getRandomString(ArrayList<Country_Code> list) {
        int min = 0;
        int max = list.size();
        return list.get(new Random().nextInt(((max - min) + 1) + min)).getCode();
    }

    private String getRandomStringValue(List<Country> list) {
        int min = 0;
        int max = list.size();
        return list.get(new Random().nextInt(((max - min) + 1) + min)).getCountry();
    }

    private void connectToVpn(String country) {
        UnifiedSDK.getInstance().getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    Log.e("Vpn_Error", "success: logged in");
                    List<String> fallbackOrder = new ArrayList<>();
                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                    fallbackOrder.add(OpenVpnTransportConfig.tcp().getName());
                    fallbackOrder.add(OpenVpnTransportConfig.udp().getName());

                    List<String> bypassDomains = new LinkedList<>();
                    UnifiedSDK.getInstance().getVPN().start(new SessionConfig.Builder().withReason(TrackingConstants.GprReasons.M_UI).withTransportFallback(fallbackOrder).withTransport(HydraTransport.TRANSPORT_ID).withVirtualLocation(country).addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains)).build(), new CompletableCallback() {
                        @Override
                        public void complete() {
                            activity.getSharedPreferences(activity.getResources().getString(R.string.app_name), MODE_PRIVATE).edit().putBoolean("isVpnConnected", true).apply();
                            showsplashAds();
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                            //  connection_progress.setVisibility(View.GONE);
                            Log.e("Check_Exception", e.getMessage());
                            if (turen_btn != null) {
                                turen_btn.setText("Turn on now");
                            }


                        }
                    });
                }

            }

            @Override
            public void failure(@NonNull VpnException e) {
                Log.e("Connect_Vpn", e.getMessage());

                showsplashAds();
            }
        });

    }

    private void showsplashAds() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        AppPreferencesManger appPreferencesManger = new AppPreferencesManger(activity);
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.ADSJSON);
        Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());
        if (Constants.adsJsonPOJO != null && !StringUtils.isNull(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getValue())) {
            Constants.adsJsonPOJO = Global.getAdsData(appPreferencesManger.getAdsModel());
            Constants.hitCounter = Integer.parseInt(Constants.adsJsonPOJO.getParameters().getApp_open_ad().getDefaultValue().getHits());
            AdUtils.showAppOpenAd(activity, new AppInterfaces.AppOpenADInterface() {
                @Override
                public void appOpenAdState(boolean state_load) {
                    vpnInterface.handlePostAd();
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
                            vpnInterface.handlePostAd();
                        }
                    });
                }
            });

        }
//

    }


}
