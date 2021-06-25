package com.stv.msgservice.core.sms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import com.cjt2325.cameralibrary.util.LogUtil;

// Wrapper around content resolver methods to catch exceptions
public final class SqliteWrapper {
    private static final String TAG = "Junwang";

    private SqliteWrapper() {
        // Forbidden being instantiated.
    }

    public static Cursor query(Context context, ContentResolver resolver, Uri uri,
                               String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        try {
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (SQLiteException e) {
            LogUtil.e(TAG, "SqliteWrapper: catch an exception when query" +e.toString());
            return null;
        } catch (IllegalArgumentException e) {
            LogUtil.e(TAG, "SqliteWrapper: catch an exception when query"+e.toString());
            return null;
        }
    }

    public static int update(Context context, ContentResolver resolver, Uri uri,
                             ContentValues values, String where, String[] selectionArgs) {
        try {
            return resolver.update(uri, values, where, selectionArgs);
        } catch (SQLiteException e) {
            LogUtil.e(TAG, "SqliteWrapper: catch an exception when update"+e.toString());
            return -1;
        } catch (IllegalArgumentException e) {
            LogUtil.e(TAG, "SqliteWrapper: catch an exception when update"+e.toString());
            return -1;
        }
    }

    public static int delete(Context context, ContentResolver resolver, Uri uri,
                             String where, String[] selectionArgs) {
        try {
            return resolver.delete(uri, where, selectionArgs);
        } catch (SQLiteException e) {
            LogUtil.e(TAG, "SqliteWrapper: catch an exception when delete"+e.toString());
            return -1;
        } catch (IllegalArgumentException e) {
            LogUtil.e(TAG, "SqliteWrapper: catch an exception when delete"+e.toString());
            return -1;
        }
    }

    public static Uri insert(Context context, ContentResolver resolver,
                             Uri uri, ContentValues values) {
        try {
            return resolver.insert(uri, values);
        } catch (SQLiteException e) {
            LogUtil.e(TAG, "SqliteWrapper: catch an exception when insert"+e.toString());
            return null;
        } catch (IllegalArgumentException e) {
            LogUtil.e(TAG, "SqliteWrapper: catch an exception when insert"+e.toString());
            return null;
        }
    }
}
