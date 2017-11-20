package com.tadqa.android.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.tadqa.android.notification.NotificationPreviewActivity;

public class NotificationStorageHelper {

    Context mContext;
    StorageHelper mStorageHelper;
    SQLiteDatabase mDatabase;

    String TABLE_NAME = "NOTIFICATION";
    String DATABASE_NAME = "notifications.db";
    int DATABASE_VERSION = 1;

    public SQLiteDatabase open() {
        mDatabase = mStorageHelper.getWritableDatabase();
        return mDatabase;
    }

    public long addNotification(String message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("MESSAGE", message);
        return mDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getNotifications() {
        return mDatabase.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public void removeNotification(int id) {
        mDatabase.execSQL("DELETE FROM " + TABLE_NAME + " WHERE ID = " + id);
    }

    public void close() {
        mDatabase.close();
    }

    public NotificationStorageHelper(Context context) {
        mContext = context;
        mStorageHelper = new StorageHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public class StorageHelper extends SQLiteOpenHelper {

        public StorageHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY, MESSAGE String);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
