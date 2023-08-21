/*
 * Copyright (c) Emadyous Development
 */

package com.ide.photoeditor.youshoot.videoeditor.utils;

import android.database.Cursor;

public class ContentUtill {
    public static String getLong(Cursor cursor) {
        String sb = "" +
                cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        return sb;
    }

    public static String getTime(Cursor cursor, String str) {
        return TimeUtils.toFormattedTime(getInt(cursor, str));
    }

    public static int getInt(Cursor cursor, String str) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(str));
    }
}
