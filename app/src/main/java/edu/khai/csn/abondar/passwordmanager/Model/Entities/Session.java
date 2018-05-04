package edu.khai.csn.abondar.passwordmanager.Model.Entities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Alexey Bondar on 18-Apr-18.
 */

public class Session {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public Session(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("users", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setLoggedin(boolean loggedin) {
        editor.putBoolean("loggedInmode", loggedin);
        editor = preferences.edit();
    }

    public boolean loggedin() {
        return preferences.getBoolean("loggedInmode", false);
    }
}
