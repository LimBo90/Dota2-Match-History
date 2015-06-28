package com.example.abdullah.dota2matchhistory;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utility {
    private static final long STEAM_ID_CONVERSION_CONSTANT = 76561197960265728L;
    private static final String LOG_TAG = Utility.class.getSimpleName();
    public static final String API_KEY = "E1BC79DCDCF6BADE298DBF8513289D98";

    /**
     * Gets the steamID of user currently logged in. (The userID of the currently logged user will be
     * savead in the SharedPreferences)
     * @param context Context to use in getting SharedPrefrence
     * @return The 64-bit steam ID of the user currently logged in.
     */
    public static long getLoggedUserID(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(context.getString(R.string.pref_user_id_key),
                -1);
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
     * @param steam64ID the 64-bit steamID of the new user
     */
    public static void addUser(Context context, Long steam64ID){
        PreferenceManager.getDefaultSharedPreferences(context).edit().
                putLong(context.getString(R.string.pref_user_id_key), steam64ID).
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
    public static long getSteam64ID(long steam32ID){
        long steam64ID = steam32ID + STEAM_ID_CONVERSION_CONSTANT;
        return steam64ID;
    }

    /**
     * Converts the  64-bit steam ID to 32-bit
     * @param steam64ID The 64 bit steam ID to convert
     * @return The 32-bit steam ID
     */
    public static long getSteam32ID(long steam64ID){
        long steam32ID = steam64ID - STEAM_ID_CONVERSION_CONSTANT;
        return  steam32ID;
    }

    public static String inputStreamToJsonStr(InputStream inputStream) {
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        String result;
        if (inputStream == null) {
            // Nothing to do.
            return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
            return buffer.toString();
        }
    }

    public static String  getDateString(long utcDate){
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
        return dateFormat.format(utcDate*1000);
    }

    public static String getTimeString(long utcDate){
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");
        return timeFormat.format(utcDate*1000);
    }

    public static String getLobbyType(int id) {
        switch (id) {
            case 0:
                return "Public Matchmaking";
            case 1:
                return "Practice";
            case 2:
                return "Tournament";
            case 3:
                return "Tutorial";
            case 4:
                return "Co-op with bots";
            case 5:
                return "Team match";
            case 6:
                return "Solo Queue";
            case 7:
                return "Ranked";
            case 8:
                return "Solo Mid 1vs1";
            default:
                return "Invalid";

        }
    }
}
