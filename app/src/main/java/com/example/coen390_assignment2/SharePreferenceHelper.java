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

    public void setDisplayMode(boolean isEnabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        assert editor != null;
        editor.putBoolean("displayMode", isEnabled);
        editor.apply();
    }

    public boolean getDisplayMode() { return sharedPreferences.getBoolean("displayMode", true); }
}
