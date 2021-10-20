// COEN 390 - Assignment 2
// Nicholas Harris - 40111093
// harris.nicholas1998@gmail.com

package com.example.coen390_assignment2.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Build table using an SQL Query
        String CREATE_TABLE_PROFILE = String.format("CREATE TABLE %s (%s INT NOT NULL PRIMARY KEY UNIQUE, %s TEXT, %s TEXT, %s REAL, %s TEXT)", Config.PROFILE_TABLE_NAME, Config.COLUMN_PROFILE_ID, Config.COLUMN_PROFILE_NAME, Config.COLUMN_PROFILE_SURNAME, Config.COLUMN_PROFILE_GPA, Config.COLUMN_PROFILE_DATE);
        String CREATE_TABLE_ACCESS = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INT NOT NULL, %s TEXT, %s TEXT)", Config.ACCESS_TABLE_NAME, Config.COLUMN_ACCESS_ACCESSID, Config.COLUMN_ACCESS_PROFILEID, Config.COLUMN_ACCESS_TYPE, Config.COLUMN_ACCESS_TIME);
        db.execSQL(CREATE_TABLE_PROFILE);
        db.execSQL(CREATE_TABLE_ACCESS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean insertProfile(String name, String surname, int id, double gpa) {
        boolean status = false;
        SQLiteDatabase db = write();
        ContentValues cvProfile = new ContentValues();
        cvProfile.put(Config.COLUMN_PROFILE_ID, id);
        cvProfile.put(Config.COLUMN_PROFILE_NAME, name);
        cvProfile.put(Config.COLUMN_PROFILE_SURNAME, surname);
        cvProfile.put(Config.COLUMN_PROFILE_GPA, gpa);
        cvProfile.put(Config.COLUMN_PROFILE_DATE, getDate());

        ContentValues cvAccess = new ContentValues();
        cvAccess.put(Config.COLUMN_ACCESS_PROFILEID, id);
        cvAccess.put(Config.COLUMN_ACCESS_TYPE, "created");
        cvAccess.put(Config.COLUMN_ACCESS_TIME, getDate());
        try {
            db.insertOrThrow(Config.PROFILE_TABLE_NAME, null, cvProfile);
            db.insertOrThrow(Config.ACCESS_TABLE_NAME, null, cvAccess);
            status = true;
        } catch (SQLException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
        return status;
    }

    public String[] getProfile(int id) {
        String[] result = {};
        Cursor cursor = null;

        try (SQLiteDatabase db = read()) {
            cursor = db.query(Config.PROFILE_TABLE_NAME, new String[]{Config.COLUMN_PROFILE_ID, Config.COLUMN_PROFILE_NAME, Config.COLUMN_PROFILE_SURNAME, Config.COLUMN_PROFILE_GPA, Config.COLUMN_PROFILE_DATE}, String.format("%s =?", Config.COLUMN_PROFILE_ID), new String[]{Integer.toString(id)}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_NAME));
                    @SuppressLint("Range") String surname = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_SURNAME));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_DATE));
                    @SuppressLint("Range") String profileId = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_ID));
                    @SuppressLint("Range") String gpa = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_GPA));
                    result = new String[]{name, surname, profileId, gpa, date};

                    SQLiteDatabase writeDb = this.getWritableDatabase();
                    ContentValues cvAccess = new ContentValues();
                    cvAccess.put(Config.COLUMN_ACCESS_PROFILEID, id);
                    cvAccess.put(Config.COLUMN_ACCESS_TYPE, "opened");
                    cvAccess.put(Config.COLUMN_ACCESS_TIME, getDate());
                    try {
                        writeDb.insertOrThrow(Config.ACCESS_TABLE_NAME, null, cvAccess);
                    } catch (SQLException e) {
                        Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
                    } finally {
                        writeDb.close();
                    }
                    return result;
                }
            }
        } catch (SQLException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    // return cursor for db query sorted by type (profileId, surname)
    public Cursor sortBy(String sortType) {
        String[] returnColumns = new String[] {Config.COLUMN_PROFILE_NAME, Config.COLUMN_PROFILE_SURNAME, Config.COLUMN_PROFILE_ID, Config.COLUMN_PROFILE_DATE, Config.COLUMN_PROFILE_GPA};
        return read().query(Config.PROFILE_TABLE_NAME, returnColumns, null, null, null, null, sortType);
    }

    public Cursor getOnlyIdAccess(int id) {
        String[] returnColumns = new String[]{Config.COLUMN_ACCESS_ACCESSID, Config.COLUMN_ACCESS_PROFILEID, Config.COLUMN_ACCESS_TYPE, Config.COLUMN_ACCESS_TIME};
        String strId = String.format("%d", id);
        return read().query(Config.ACCESS_TABLE_NAME, returnColumns, String.format("%s = ?", Config.COLUMN_ACCESS_PROFILEID), new String[]{strId}, null, null, Config.COLUMN_ACCESS_ACCESSID + " DESC");
    }

    // get a readable db
    public SQLiteDatabase read() { return this.getReadableDatabase(); }

    // get a writable db
    public SQLiteDatabase write() { return this.getWritableDatabase(); }


    @SuppressLint("Range")
    public List<String[]> getAllProfiles(String sortType) {
        SQLiteDatabase db = read();
        Cursor cursor = null;

        try {
            cursor = sortBy(sortType);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    List<String[]> users = new ArrayList<>();
                    do {
                        String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_NAME));
                        String surname = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_SURNAME));
                        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_DATE));
                        String profileId = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_ID));
                        String gpa = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_GPA));
                        String[] str = new String[]{name, surname, date, profileId, gpa};
                        users.add(str);
                    } while(cursor.moveToNext());
                    return users;
                }
            }
        } catch (SQLiteException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return Collections.emptyList();
    }

    public void closeProfile(int id) {
        SQLiteDatabase db = write();
        ContentValues cvAccess = new ContentValues();
        cvAccess.put(Config.COLUMN_ACCESS_PROFILEID, id);
        cvAccess.put(Config.COLUMN_ACCESS_TYPE, "closed");
        cvAccess.put(Config.COLUMN_ACCESS_TIME, getDate());
        try {
            db.insertOrThrow(Config.ACCESS_TABLE_NAME, null, cvAccess);
        } catch (SQLException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
    }

    public void dropProfile() {

    }

    public int getNumUsers() {
        long num = 0;
        try (SQLiteDatabase db = read()) {
            num = DatabaseUtils.queryNumEntries(db, Config.PROFILE_TABLE_NAME);
        } catch (SQLException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        }
        return (int) num;
    }

    @SuppressLint("Range")
    public List<String> getAccessList(int id) {
        SQLiteDatabase db = read();
        Cursor cursor = null;

        try {
            cursor = getOnlyIdAccess(id);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    List<String> access = new ArrayList<>();
                    do {
                        access.add(String.format("%s %s", cursor.getString(cursor.getColumnIndex(Config.COLUMN_ACCESS_TIME)), cursor.getString(cursor.getColumnIndex(Config.COLUMN_ACCESS_TYPE))));
                    } while (cursor.moveToNext());
                    return access;
                }
            }
        } catch (SQLiteException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return Collections.emptyList();
    }

    private String getDate() {
        String date = LocalDate.now().toString();
        String time = LocalTime.now().toString();
        return String.format("%s @ %s", date, time);
    }

}
