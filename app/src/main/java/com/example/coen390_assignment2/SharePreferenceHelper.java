// COEN 390 - Assignment 2
// Nicholas Harris - 40111093
// harris.nicholas1998@gmail.com

package com.example.coen390_assignment2;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceHelper {
    private SharedPreferences sharedPreferences;
    public SharePreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("ProfilePreference", Context.MODE_PRIVATE);
    }

    public void addProfile() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        assert editor != null;
        editor.putInt("profileCount", getProfileCount());
        editor.apply();
    }

    public int getProfileCount() { return sharedPreferences.getInt("profileCount", 0); }
}
