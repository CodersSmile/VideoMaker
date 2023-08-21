/*
 * Copyright (c) Emadyous Development
 */

package com.ide.photoeditor.youshoot.videoeditor.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;

import com.ide.photoeditor.youshoot.MyApp;
import com.ide.photoeditor.youshoot.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.ide.photoeditor.youshoot.AdsUtils.Utils.Global.getDataDirPath;

public class FileUtils {

    static class a implements FilenameFilter {
        private final String a;

        a(String str) {
            this.a = str;
        }

        public boolean accept(File file, String str) {
            return str != null && str.startsWith(this.a.substring(0, this.a.lastIndexOf("."))) && str.endsWith(this.a.substring(this.a.lastIndexOf(".")));
        }
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static String getTargetFileName(Context context, String str) {
        String name = new File(str).getAbsoluteFile().getName();
//        String sb = Environment.getExternalStorageDirectory().getAbsoluteFile() +
//                "/Download/" +
//                context.getResources().getString(R.string.app_name);

        File absoluteFile = FileUtils.TEMP_EditDIRECTORY;
        if (!absoluteFile.isDirectory()) {
            absoluteFile.mkdirs();
        }
        List<String> asList = Arrays.asList(Objects.requireNonNull(absoluteFile.list(new a(name))));
        int i = 0;
        while (true) {
            int i2 = i + 1;
            @SuppressLint("DefaultLocale") String sb3 = String.valueOf(name.substring(0, name.lastIndexOf("."))) + "-" +
                    String.format("%d", i) +
                    name.substring(name.lastIndexOf("."));
            if (!asList.contains(sb3)) {
                String sb4 = FileUtils.TEMP_EditDIRECTORY.getAbsolutePath();
                return new File(sb4, sb3).getPath();
            }
            i = i2;
        }
    }

    public static File mSdCard = getDataDirPath();
    public static File APP_DIRECTORY = new File(mSdCard,"Video Maker & Editor");
    public static final File TEMP_DIRECTORY = new File(APP_DIRECTORY, "temp");
    public static final File TEMP_EditDIRECTORY = new File(APP_DIRECTORY, "tempedit");
    public static final File TEMP_DIRECTORY_AUDIO = new File(APP_DIRECTORY, "temp_audio");
    public static final File TEMP_VID_DIRECTORY = new File(TEMP_DIRECTORY, "temp_vid");
    public static long mDeleteFileCount = 0;
    static final String ffmpegFileName = "ffmpeg";

    public static boolean deleteFile(File mFile) {
        int i = 0;
        boolean idDelete = false;
        if (mFile == null) {
            return false;
        }
        if (mFile.exists()) {
            if (mFile.isDirectory()) {
                File[] children = mFile.listFiles();
                if (children != null && children.length > 0) {
                    int length = children.length;
                    while (i < length) {
                        File child = children[i];
                        mDeleteFileCount += child.length();
                        idDelete = deleteFile(child);
                        i++;
                    }
                }
                mDeleteFileCount += mFile.length();
                idDelete = mFile.delete();
            } else {
                mDeleteFileCount += mFile.length();
                idDelete = mFile.delete();
            }
        }
        return idDelete;
    }
    public static boolean deleteThemeDir(String theme) {
        return deleteFile(getImageDirectory(theme));
    }
    public static File getImageDirectory(String theme) {
        File imageDir = new File(TEMP_DIRECTORY, theme);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        return imageDir;
    }
    public static File getImageDirectory(String theme, int iNo) {
        File imageDir = new File(getImageDirectory(theme), String.format("IMG_%03d", new Object[]{Integer.valueOf(iNo)}));
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        return imageDir;
    }
    public static void deleteTempDir() {
        for (File child : TEMP_DIRECTORY.listFiles()) {
            new AnonymousClass1(child).start();
        }
    }
    static class AnonymousClass1 extends Thread {
        private final File val$child;

        AnonymousClass1(File file) {
            this.val$child = file;
        }

        public void run() {
            FileUtils.deleteFile(this.val$child);
        }
    }
    public static String getDuration(long milliseconds) {
        String format = "";
        String secondsString = "";
        String minutesString = "";
        int hours = (int) (milliseconds / 3600000);
        int minutes = ((int) (milliseconds % 3600000)) / 60000;
        int seconds = (int) (((milliseconds % 3600000) % 60000) / 1000);
        if (hours > 0) {
            format = hours + ":";
        }
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        return format + minutesString + ":" + secondsString;
    }
    public static String getFFmpeg(Context context) {
        return new StringBuilder(String.valueOf(getFilesDirectory(context).getAbsolutePath())).append(File.separator).append(ffmpegFileName).toString();
    }
    static File getFilesDirectory(Context context) {
        return context.getFilesDir();
    }
}
