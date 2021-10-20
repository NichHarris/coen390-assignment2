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

    // PUBLIC METHODS //

    // Add a profile to the DB
    public boolean insertProfile(String name, String surname, int id, double gpa) {
        if (writeToProfile(name, surname, id, gpa)) {
            return writeToAccess(id, "created");
        }
        return false;
    }

    // Retrieve a specific profile from the DB
    @SuppressLint("Range")
    public String[] getProfile(int id) {
        String[] result = {};

        try (Cursor cursor = read().query(Config.PROFILE_TABLE_NAME, new String[]{Config.COLUMN_PROFILE_ID, Config.COLUMN_PROFILE_NAME, Config.COLUMN_PROFILE_SURNAME, Config.COLUMN_PROFILE_GPA, Config.COLUMN_PROFILE_DATE}, String.format("%s =?", Config.COLUMN_PROFILE_ID), new String[]{Integer.toString(id)}, null, null, null)) {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    // Create access entry
                    writeToAccess(id, "opened");
                    return getProfileInfo(cursor);
                }
            }
        } catch (SQLException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    // return cursor for db query sorted by type (profileId, surname)
    public Cursor sortBy(String sortType) {
        String[] returnColumns = new String[] {Config.COLUMN_PROFILE_NAME, Config.COLUMN_PROFILE_SURNAME, Config.COLUMN_PROFILE_ID, Config.COLUMN_PROFILE_DATE, Config.COLUMN_PROFILE_GPA};
        return read().query(Config.PROFILE_TABLE_NAME, returnColumns, null, null, null, null, sortType);
    }

    // Get access table entries only for a specific ID
    public Cursor getOnlyIdAccess(int id) {
        String[] returnColumns = new String[]{Config.COLUMN_ACCESS_ACCESSID, Config.COLUMN_ACCESS_PROFILEID, Config.COLUMN_ACCESS_TYPE, Config.COLUMN_ACCESS_TIME};
        String strId = String.format("%d", id);
        return read().query(Config.ACCESS_TABLE_NAME, returnColumns, String.format("%s = ?", Config.COLUMN_ACCESS_PROFILEID), new String[]{strId}, null, null, Config.COLUMN_ACCESS_ACCESSID + " DESC");
    }

    // Return all profiles in the DB
    @SuppressLint("Range")
    public List<String[]> getAllProfiles(String sortType) {
        try (Cursor cursor = sortBy(sortType)) {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    List<String[]> users = new ArrayList<>();
                    do {
                        users.add(getProfileInfo(cursor));
                    } while (cursor.moveToNext());
                    return users;
                }
            }
        } catch (SQLiteException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        }
        return Collections.emptyList();
    }

    // Add a close profile entry to access table
    public void closeProfile(int id) {
        // Create access entry
        writeToAccess(id, "closed");
    }

    // Remove a profile from the DB
    public void dropProfile(int id) {
        try (SQLiteDatabase db = write()) {
            db.delete(Config.PROFILE_TABLE_NAME, Config.COLUMN_PROFILE_ID + "=" + String.format("%d", id), null);
        } catch (SQLException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        }
    }

    // Get the number of profiles in the DB
    public int getNumUsers() {
        long num = 0;
        try {
            num = DatabaseUtils.queryNumEntries(read(), Config.PROFILE_TABLE_NAME);
        } catch (SQLException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        }
        return (int) num;
    }

    // Get a list of all accesses for a user from the DB
    @SuppressLint("Range")
    public List<String> getAccessList(int id) {

        try (Cursor cursor = getOnlyIdAccess(id)) {
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
        }
        return Collections.emptyList();
    }

    // PRIVATE METHODS //

    // get a readable db
    private SQLiteDatabase read() { return this.getReadableDatabase(); }

    // get a writable db
    private SQLiteDatabase write() { return this.getWritableDatabase(); }

    // Write to profile table
    private boolean writeToProfile(String name, String surname, int id, double gpa) {
        ContentValues cV = new ContentValues();
        cV.put(Config.COLUMN_PROFILE_ID, id);
        cV.put(Config.COLUMN_PROFILE_NAME, name);
        cV.put(Config.COLUMN_PROFILE_SURNAME, surname);
        cV.put(Config.COLUMN_PROFILE_GPA, gpa);
        cV.put(Config.COLUMN_PROFILE_DATE, getDate());
        try {
            write().insertOrThrow(Config.PROFILE_TABLE_NAME, null, cV);
            return true;
        } catch (SQLException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    // Write to Access table
    private boolean writeToAccess(int id, String type) {
        ContentValues cV = new ContentValues();
        cV.put(Config.COLUMN_ACCESS_PROFILEID, id);
        cV.put(Config.COLUMN_ACCESS_TYPE, type);
        cV.put(Config.COLUMN_ACCESS_TIME, getDate());
        try {
            write().insertOrThrow(Config.ACCESS_TABLE_NAME, null, cV);
            return true;
        } catch (SQLException e) {
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    // Get profile info for current cursor
    @SuppressLint("Range")
    private String[] getProfileInfo(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_NAME));
        String surname = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_SURNAME));
        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_DATE));
        String profileId = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_ID));
        String gpa = cursor.getString(cursor.getColumnIndex(Config.COLUMN_PROFILE_GPA));
        return new String[]{name, surname, date, profileId, gpa};
    }

    // Get the current date/time and format
    private String getDate() {
        String date = LocalDate.now().toString();
        String time = LocalTime.now().toString();
        return String.format("%s @ %s", date, time);
    }

}
