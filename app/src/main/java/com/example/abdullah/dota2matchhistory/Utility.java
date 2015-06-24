package com.example.abdullah.dota2matchhistory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Utility {
    private static final Long STEAM_ID_CONVERSION_CONSTANT = 76561197960265728L;

    /**
     * Gets the steamID of user currently logged in. (The userID of the currently logged user will be
     * savead in the SharedPreferences)
     * @param context Context to use in getting SharedPrefrence
     * @return the steamID of user currently logged in.
     */
    public static String getLoggedUserID(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_user_id_key),
                context.getString(R.string.pref_no_user_logged));
    }

    /**
     * Checks if there's a user currently logged in. It does so by checking the SharedPreferences
     * if it contains SteamID.
     * @param context Context to use in getting SharedPrefrences.
     * @return True if the there's a user currently logged in.
     */
    public static Boolean isUserLoggedIn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).
                contains(context.getString(R.string.pref_user_id_key));
    }

    /**
     * Adds the new user's steamID into the SharedPrefrences.
     * @param context Context to use in getting SharedPrefrences.
     * @param steamID the steamID of the new user
     */
    public static void addUser(Context context, String steamID){
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putString(context.getString(R.string.pref_user_id_key), steamID).
                commit();
    }
    /**
     * Removes the current user's steamID from the SharedPrefrences.
     * @param context Context to use in getting SharedPrefrences.
     */
    public static void removeUser(Context context){
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                remove(context.getString(R.string.pref_user_id_key)).
                commit();
    }

    /**
     * Converts the 32-bit steam ID to 64-bit
     * @param steam32ID The 32 bit steam ID to convert
     * @return The 64-bit steam ID
     */
    public static String getSteam64ID(String steam32ID){
        long steam32 =  Long.valueOf(steam32ID);
        long steam64 = steam32 + STEAM_ID_CONVERSION_CONSTANT;
        return  "" + steam64;
    }

    /**
     * Converts the  64-bit steam ID to 32-bit
     * @param steam64ID The 64 bit steam ID to convert
     * @return The 32-bit steam ID
     */
    public static String getSteam32ID(String steam64ID){
        long steam64 =  Long.valueOf(steam64ID);
        long steam32 = steam64 - STEAM_ID_CONVERSION_CONSTANT;
        return  "" + steam32;
    }
}
