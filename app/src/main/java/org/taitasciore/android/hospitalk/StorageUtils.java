package org.taitasciore.android.hospitalk;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.taitasciore.android.model.User;

/**
 * Created by roberto on 25/04/17.
 */

public final class StorageUtils {

    private static final Gson gson = new Gson();

    private static SharedPreferences getSharedPref(Context context) {
        return context.getSharedPreferences("pref", Context.MODE_PRIVATE);
    }

    public static void saveUser(Context context, User user) {
        SharedPreferences pref = getSharedPref(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user", gson.toJson(user));
        editor.commit();
    }

    public static boolean isUserLogged(Context context) {
        return getSharedPref(context).contains("user");
    }

    public static User getUser(Context context) {
        SharedPreferences pref = getSharedPref(context);
        return gson.fromJson(pref.getString("user", null), User.class);
    }

    public static void removeUser(Context context) {
        SharedPreferences pref = getSharedPref(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
