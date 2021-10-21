// COEN 390 - Assignment 2
// Nicholas Harris - 40111093
// harris.nicholas1998@gmail.com

package com.example.coen390_assignment2.Controllers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceHelper {
    private final SharedPreferences sharedPreferences;
    public SharePreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("ProfilePreference", Context.MODE_PRIVATE);
    }

    // Set the current display mode
    public void setDisplayMode(boolean isEnabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        assert editor != null;
        editor.putBoolean("displayMode", isEnabled);
        editor.apply();
    }

    // Get the current display mode
    public boolean getDisplayMode() { return sharedPreferences.getBoolean("displayMode", true); }
}
