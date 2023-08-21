package com.ide.photoeditor.youshoot.AdsUtils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ide.photoeditor.youshoot.AdsUtils.FirebaseADHandlers.AdsJsonPOJO;
import com.ide.photoeditor.youshoot.BuildConfig;
import com.ide.photoeditor.youshoot.MyApp;
import com.ide.photoeditor.youshoot.R;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.os.Build.VERSION.SDK_INT;

public class Global {
    public static final String PLEASE_WAIT = "We are processing your request...";
    public static ArrayList<String> mSelectedImgPath = new ArrayList<>();

    private Context context;

    public Global(Context context) {
        this.context = context;
    }

    static AlertDialog alertDialog;
    private static ProgressDialog dialog;

    public static void showProgressDialog(Activity activity, String msg) {
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = ((Activity) activity).getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.progressbar, null);
            dialogBuilder.setView(dialogView);

            if (alertDialog == null) {
                alertDialog = dialogBuilder.create();

            }
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            alertDialog.setCancelable(false);

            WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lp.dimAmount = 0.5f;
            alertDialog.getWindow().setAttributes(lp);

            if (!alertDialog.isShowing())
                alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }

            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
                alertDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sout(String TagToString, Object whatToPrint) {
        if (BuildConfig.DEBUG) {
            System.out.println(TagToString + " " + whatToPrint);
        }
    }


    public static AdsJsonPOJO getAdsData(String json) {
        Type familyType = new TypeToken<AdsJsonPOJO>() {
        }.getType();
        return new Gson().fromJson(json, familyType);

    }

    public static long mLastClickTime = 0;

    public static void checkClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    public static boolean isLatestVersion() {
        return SDK_INT >= Build.VERSION_CODES.R;
    }

    public static Uri getContentMediaUri() {
        if (isLatestVersion()) {
            return MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
    }

    public static void printLog(String key, String val) {
        if (BuildConfig.DEBUG)
            Log.e(key + "*===", "===*" + val);
    }

    public static boolean isEmptyStr(String str) {
        if (str == null || str.trim().isEmpty() || str.equalsIgnoreCase(""))
            return true;
        return false;
    }

    public static String getRootFileForSave() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }

    public static File getDataDirPath() {
        return MyApp.getInstance().getFilesDir();
    }

    public static Uri getContentVideoUri() {
        if (isLatestVersion()) {
            return MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
    }
    public static Uri getUri(File file, Context context) {
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            contentUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        } else {
            contentUri = Uri.fromFile(file);
        }
        return contentUri;
    }
}
