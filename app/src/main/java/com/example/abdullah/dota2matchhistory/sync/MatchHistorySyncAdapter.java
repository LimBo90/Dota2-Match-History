package com.example.abdullah.dota2matchhistory.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.abdullah.dota2matchhistory.Data.MatchesContract;
import com.example.abdullah.dota2matchhistory.R;
import com.example.abdullah.dota2matchhistory.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class MatchHistorySyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String LOG_TAG = MatchHistorySyncAdapter.class.getSimpleName();

    // Types of syncs are either matches sync or dota sync
    private static final String SYNC_TYPE_KEY = "syncType";
    // matches sync: Syncs user matches
    private static final int SYNC_TYPE_MATCHES = 0;
    //dota sync: Syncs hero and items data
    private static final int SYNC_TYPE_DOTA = 1;
    //first time sync : User just opened the application for the first time so update matches and dota data
    private static final int SYNC_TYPE_ALL = 2;

    public static final String SYNC_FINISHED = "sync_finished";

    //Sync intervals
    public static final int MATCHES_SYNC_INTERVAL = 60*60*3;
    public static final int DOTA_SYNC_INTERVAL = 60*60*24;

    public MatchHistorySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.v(LOG_TAG, "onPerformSync Called.");
        if(!Utility.isUserLoggedIn(getContext())){
            Log.d(LOG_TAG, "sync cancelled --> no user");
            return;
        }

        Context context = getContext();

        long userID = Utility.getLoggedUserID(context);
        int syncType = extras.getInt(SYNC_TYPE_KEY);
        Log.v(LOG_TAG, "syncType = " + extras.getInt(SYNC_TYPE_KEY));

        switch(syncType){
            case SYNC_TYPE_MATCHES:
                Log.v(LOG_TAG, "Synching matches");
                updateMatches(context, userID);
                break;

            case SYNC_TYPE_DOTA:
                Log.v(LOG_TAG, "Synching dota data");
                updateDotaData(context);
                break;

            case SYNC_TYPE_ALL:
                Log.v(LOG_TAG, "Synching all data");
                updateDotaData(context);
                updateMatches(context, userID);
                break;
        }

        sendSyncFinishedBroadcast(context);
    }

    private void sendSyncFinishedBroadcast(Context context) {
        Intent intent = new Intent(SYNC_FINISHED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Log.v(LOG_TAG, "broadcast sent");
    }


    /**
     * Helper method to have the sync adapter sync matches immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        // Syncs requested by the user updates matches only
        bundle.putInt(SYNC_TYPE_KEY, SYNC_TYPE_MATCHES);

        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to have the sync adapter sync matches and dota data
     * @param context The context used to access the account service
     */
    public static void syncAllImmediately(Context context){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        // sync everything
        bundle.putInt(SYNC_TYPE_KEY, SYNC_TYPE_ALL);

        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);

        }
        return newAccount;
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int matchesSyncInterval, int dotaSyncInterval) {
        Log.v(LOG_TAG, "configurePeriodicSync");
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);


        Bundle extrasMatchesSync = new Bundle();
        extrasMatchesSync.putInt(SYNC_TYPE_KEY, SYNC_TYPE_MATCHES);
        Bundle extrasDotaSync = new Bundle();
        extrasDotaSync.putInt(SYNC_TYPE_KEY, SYNC_TYPE_DOTA);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest matchesRequest = new SyncRequest.Builder().
                    syncPeriodic(matchesSyncInterval, matchesSyncInterval/3).
                    setSyncAdapter(account, authority).
                    setExtras(extrasMatchesSync).build();
            ContentResolver.requestSync(matchesRequest);

            SyncRequest dotaRequest = new SyncRequest.Builder().
                    syncPeriodic(dotaSyncInterval, dotaSyncInterval/3).
                    setSyncAdapter(account, authority).
                    setExtras(extrasDotaSync).build();
            ContentResolver.requestSync(dotaRequest);


        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, extrasMatchesSync, matchesSyncInterval);

            ContentResolver.addPeriodicSync(account,
                    authority, extrasDotaSync, dotaSyncInterval);
        }
    }



    private static void onAccountCreated(Account newAccount, Context context) {
        Log.v(LOG_TAG, "onAccountCreated");
        /*
         * Since we've created an account
         */
        MatchHistorySyncAdapter.configurePeriodicSync(context, MATCHES_SYNC_INTERVAL, DOTA_SYNC_INTERVAL);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncAllImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        Log.v(LOG_TAG, "initializeSyncAdapter");
        getSyncAccount(context);
    }


    private void updateMatches(Context context, long userID){
        String matchesStr = getMatchesFromApi(userID);
        try {
            getMatchesFromJson(matchesStr, userID, context);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


    private void updateDotaData(Context context) {
        String heroesJsonStr = getHeroesFromApi();
        String itemsJsonStr  = getItemsFromApi();

        try{
            getHeroesFromJsonStr(heroesJsonStr, context);
            getItemsFromJsonStr(itemsJsonStr, context);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }



    private String getMatchesFromApi(long userID){
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;

        // Will contain the raw JSON response as a string.
        String matchesJsonStr = null;


        int numMatches = 50;

        try {
            // Construct the URL for the dota api
            final String MATCH_HISTORY_BASE_URL =
                    "https://api.steampowered.com/IDOTA2Match_570/GetMatchHistory/V001/?";
            final String API_KEY_PARAM = "key";
            final String ACCOUT_ID_PARAM = "account_id";
            final String MATCHES_COUNT_PARAM = "matches_requested";

            Uri builtUri = Uri.parse(MATCH_HISTORY_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, Utility.API_KEY)
                    .appendQueryParameter(ACCOUT_ID_PARAM, Long.toString(userID))
                    .appendQueryParameter(MATCHES_COUNT_PARAM, Integer.toString(numMatches))
                    .build();


            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Log.v(LOG_TAG, "fetching matches for user");

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            matchesJsonStr = Utility.inputStreamToJsonStr(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return matchesJsonStr;
    }

    private void getMatchesFromJson(String matchesJsonStr,long userID, Context context)
            throws JSONException {
        Log.v(LOG_TAG, "parsing matchesJsonStr");

        final String D2A_result = "result";
        final String D2A_MATCHES = "matches";
        final String D2A_MATCH_ID = "match_id";
        final String D2A_START_TIME = "start_time";
        final String D2A_LOBBY_TYPE = "lobby_type";
        final String D2A_PLAYERS = "players";

        if(matchesJsonStr == null){
            Log.e(LOG_TAG, "matchesJsonStr is empty");
            return;
        }


        JSONObject matchesJson = new JSONObject(matchesJsonStr).getJSONObject(D2A_result);

        JSONArray matchesArray = matchesJson.getJSONArray(D2A_MATCHES);
        // Insert the new matches information into the database
        Vector<ContentValues> cVVector = new Vector<>(matchesArray.length());

        for (int i = 0; i < matchesArray.length(); i++) {
            //Match values to be collected
            long matchID;
            long startTime;
            int lobbyType;
            int userHeroID;

            JSONObject match = matchesArray.getJSONObject(i);
            matchID = match.getLong(D2A_MATCH_ID);
            startTime = match.getLong(D2A_START_TIME);
            lobbyType = match.getInt(D2A_LOBBY_TYPE);
            userHeroID = getUserHeroId(match.getJSONArray(D2A_PLAYERS),userID, context);

            if(userHeroID == -1){
                Log.v(LOG_TAG, "userHeroID is not found among players user's account private??");
            }


            ContentValues matchValues = new ContentValues();
            matchValues.put(MatchesContract.MatchesEntry.COLUMN_MATCH_ID, matchID);
            matchValues.put(MatchesContract.MatchesEntry.COLUMN_START_TIME, startTime);
            matchValues.put(MatchesContract.MatchesEntry.COLUMN_LOBBY_TYPE, lobbyType);
            matchValues.put(MatchesContract.MatchesEntry.COLUMN_USER_HERO, userHeroID);

            cVVector.add(matchValues);

        }

        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] matchesData = cVVector.toArray(new ContentValues[cVVector.size()]);
            context.getContentResolver().bulkInsert(MatchesContract.MatchesEntry.CONTENT_URI, matchesData);
        }

    }

    private int getUserHeroId(JSONArray players,long userID, Context context)
    throws JSONException{
        final String D2A_ACCOUNT_ID = "account_id";
        final String D2A_HERO_ID = "hero_id";
        long user32ID = Utility.getSteam32ID(userID);
        if(players == null) {
            return -1;
        }

        for(int i=0; i<players.length() ; i++){
            JSONObject player = players.getJSONObject(i);
            if(player.getLong(D2A_ACCOUNT_ID) == user32ID){
                return player.getInt(D2A_HERO_ID);
            }
        }
        return -1;
    }


    private String getHeroesFromApi(){
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;

        // Will contain the raw JSON response as a string.
        String heroesJsonStr = null;


        try {
            // Construct the URL for the dota api
            final String HEROES_BASE_URL =
                    "https://api.steampowered.com/IEconDOTA2_570/GetHeroes/v0001/?";
            final String API_KEY_PARAM = "key";
            final String LANGUAGE_PARAM = "language";

            Uri builtUri = Uri.parse(HEROES_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, Utility.API_KEY)
                    .appendQueryParameter(LANGUAGE_PARAM, "en_us")
                    .build();


            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Log.v(LOG_TAG, "fetching heroes data");

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            heroesJsonStr = Utility.inputStreamToJsonStr(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return heroesJsonStr;
    }

    private void getHeroesFromJsonStr(String heroesJsonStr, Context context)
    throws JSONException{
        final String D2A_RESULT = "result";
        final String D2A_HEROES = "heroes";
        final String D2A_HERO_ID = "id";
        final String D2A_HERO_NAME = "name";
        final String D2A_HERO_LOCALIZED_NAME = "localized_name";
        if(heroesJsonStr == null){
            return;
        }

        JSONArray heroes = new JSONObject(heroesJsonStr).getJSONObject(D2A_RESULT).getJSONArray(D2A_HEROES);

        // Insert the new matches information into the database
        Vector<ContentValues> cVVector = new Vector<>(heroes.length());

        Log.v(LOG_TAG, "parsing heroes data");

        for(int i=0 ; i<heroes.length(); i++){
            int id;
            String name;
            String localizedName;

            JSONObject hero = heroes.getJSONObject(i);
            id = hero.getInt(D2A_HERO_ID);
            name = hero.getString(D2A_HERO_NAME).substring(9);
            localizedName = hero.getString(D2A_HERO_LOCALIZED_NAME);

            ContentValues heroValues = new ContentValues();
            heroValues.put(MatchesContract.HeroEntry.COLUMN_HERO_ID, id);
            heroValues.put(MatchesContract.HeroEntry.COLUMN_NAME, name);
            heroValues.put(MatchesContract.HeroEntry.COLUMN_LOCALIZED_NAME, localizedName);

            cVVector.add(heroValues);
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] heroesData = cVVector.toArray(new ContentValues[cVVector.size()]);
            context.getContentResolver().bulkInsert(MatchesContract.HeroEntry.CONTENT_URI, heroesData);
        }
    }

    private String getItemsFromApi(){
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;

        // Will contain the raw JSON response as a string.
        String itemsJsonStr = null;


        try {
            // Construct the URL for the dota api
            final String ITEMS_BASE_URL =
                    "https://api.steampowered.com/IEconDOTA2_570/GetGameItems/V001/?";
            final String API_KEY_PARAM = "key";
            final String LANGUAGE_PARAM = "language";

            Uri builtUri = Uri.parse(ITEMS_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, Utility.API_KEY)
                    .appendQueryParameter(LANGUAGE_PARAM, "en_us")
                    .build();


            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Log.v(LOG_TAG, "fetching items data");

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            itemsJsonStr = Utility.inputStreamToJsonStr(inputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return itemsJsonStr;
    }

    private void getItemsFromJsonStr(String itemsJsonStr, Context context)
            throws JSONException{
        final String D2A_RESULT = "result";
        final String D2A_ITEMS = "items";
        final String D2A_ITEMS_ID = "id";
        final String D2A_ITEMS_NAME = "name";
        final String D2A_ITEMS_LOCALIZED_NAME = "localized_name";

        if(itemsJsonStr == null) {
            return;
        }

        JSONArray items = new JSONObject(itemsJsonStr).getJSONObject(D2A_RESULT).getJSONArray(D2A_ITEMS);

        // Insert the new items information into the database
        Vector<ContentValues> cVVector = new Vector<>(items.length());


        for(int i=0 ; i<items.length(); i++){
            int id;
            String name;
            String localizedName;

            JSONObject item = items.getJSONObject(i);
            id = item.getInt(D2A_ITEMS_ID);
            name = item.getString(D2A_ITEMS_NAME);
            localizedName = item.getString(D2A_ITEMS_LOCALIZED_NAME);

            ContentValues itemValues = new ContentValues();
            itemValues.put(MatchesContract.ItemEntry.COLUMN_ITEM_ID, id);
            itemValues.put(MatchesContract.ItemEntry.COLUMN_NAME, name);
            itemValues.put(MatchesContract.ItemEntry.COLUMN_LOCALIZED_NAME, localizedName);

            cVVector.add(itemValues);
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] heroesData = cVVector.toArray(new ContentValues[cVVector.size()]);
            context.getContentResolver().bulkInsert(MatchesContract.ItemEntry.CONTENT_URI, heroesData);
        }
    }
}
