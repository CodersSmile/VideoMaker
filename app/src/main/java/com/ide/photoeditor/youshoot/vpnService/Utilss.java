package com.ide.photoeditor.youshoot.vpnService;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.ide.photoeditor.youshoot.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

public class Utilss {
    public static List<Country> list;
    public static List<Country_Code> list_Country;
    public static String url = "https://api.datayuge.com/v1/lookup/";
    private static final String TAG = "Utilss";
    public static List<CountryModal> listCountry;
//    public static List<Country_Code> list_Country=new ArrayList<>();

    public static String getImgURL(int resourceId) {


        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId).toString();
    }
    public static void loginClientInfo(ClientInfo clientInfo) {
        UnifiedSDK unifiedSDK = UnifiedSDK.getInstance(clientInfo);

        AuthMethod authMethod = AuthMethod.anonymous();
        unifiedSDK.getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(@NonNull User user) {
                Log.e("Vpin", "success: " + user.getAccessToken() + " " + user.getSubscriber());

            }

            @Override
            public void failure(@NonNull VpnException e) {
                Log.e("Vpin", "failure: ", e);
            }
        });
    }
    
    public static String getKeyHash(Context activity) {
        PackageInfo packageInfo;
        try {
            packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest digest;
                digest = MessageDigest.getInstance("SHA");
                digest.update(signature.toByteArray());
                String something = (Base64.encodeToString(digest.digest(), Base64.NO_WRAP));
                return something.replace("+", "*");
            }
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();

        } catch (NoSuchAlgorithmException e) {

        } catch (Exception e) {

        }
        return null;
    }

    public static CountryModal getCountryFlagPath(String a, Activity activity) {
        String jObject = loadJSONFromAsset("country_flag.json", activity);
        try {
            JSONObject object = new JSONObject(jObject);
            JSONArray array = object.getJSONArray("country_flag");
            for (int i = 0; i < array.length(); i++) {
                JSONObject list = array.getJSONObject(i);
                String path = list.getString("url");
                String name = list.getString("Name");
                String code = list.getString("Code");
                CountryModal countryModal = new CountryModal();
                countryModal.setCode(code);
                countryModal.setName(name);
                countryModal.setPathFlag(path);
                if (a.equalsIgnoreCase(code)) {
                    return countryModal;
                }
            }

        } catch (JSONException e) {

        }
        return null;
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


    public static String humanReadableByteCountOld(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    public static VPNConfigModel getConfigData(String jsonConfig) {
        Type familyType = new TypeToken<VPNConfigModel>() {
        }.getType();
        return new Gson().fromJson(jsonConfig, familyType);
    }
}
