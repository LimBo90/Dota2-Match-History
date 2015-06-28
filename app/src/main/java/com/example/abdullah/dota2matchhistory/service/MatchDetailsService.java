package com.example.abdullah.dota2matchhistory.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.abdullah.dota2matchhistory.Data.MatchesContract;
import com.example.abdullah.dota2matchhistory.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class MatchDetailsService extends IntentService {
    private static final String LOG_TAG = MatchDetailsService.class.getSimpleName();
    public static final String MATCH_ID_KEY = "match_id";
    public static final String MATCH_DETAILS_FETCHED = "details-fetches";
    public static final long PRIVATE_ACCOUNT_ID = 4294967295L;

    public MatchDetailsService(){
        super("MatchDetails");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long matchID = intent.getLongExtra(MATCH_ID_KEY, 0);
        Log.v(LOG_TAG, "onHandleIntent");

        if(matchID == 0){
            Log.v(LOG_TAG, "matchID = 0  returning....");
            return;
        }

        String matchDetailsJsonStr = getMatchDetailsFromApi(matchID);
        try {
            getMatchDetailsFromJson(matchDetailsJsonStr, matchID, this);
        }catch (JSONException e){
            e.printStackTrace();
        }

        sendDetailsFetchdBroadcast(this);
    }

    private String getMatchDetailsFromApi(long matchID){
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;

        // Will contain the raw JSON response as a string.
        String matchDetailsJsonStr = null;


        try {
            // Construct the URL for the dota api
            final String MATCH_DETAILS_JSON_STR =
                    "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?";
            final String API_KEY_PARAM = "key";
            final String MATCH_ID_PARAM = "match_id";

            Uri builtUri = Uri.parse(MATCH_DETAILS_JSON_STR).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, Utility.API_KEY)
                    .appendQueryParameter(MATCH_ID_PARAM, "" + matchID)
                    .build();

            Log.v(LOG_TAG,"builtUri = " + builtUri.toString());

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Log.v(LOG_TAG, "fetching match details  matchID = " + matchID);

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            matchDetailsJsonStr = Utility.inputStreamToJsonStr(inputStream);
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
        return matchDetailsJsonStr;
    }

    private void getMatchDetailsFromJson(String matchDetailsJsonStr, long matchID, Context context)
            throws JSONException {
        final String D2A_result = "result";

        //Match stats
        final String D2A_RADIANT_WIN = "radiant_win";
        final String D2A_DURATION = "duration";
        final String D2A_GAME_MODE = "game_mode";
        final String D2A_PLAYERS = "players";

        JSONObject matchDetailsJson = new JSONObject(matchDetailsJsonStr).getJSONObject(D2A_result);

        //match stats
        Boolean radiantWin = matchDetailsJson.getBoolean(D2A_RADIANT_WIN);
        int duration = matchDetailsJson.getInt(D2A_DURATION);
        int gameMode = matchDetailsJson.getInt(D2A_GAME_MODE);

        Log.v(LOG_TAG, "radiantWin = " + radiantWin);
        Log.v(LOG_TAG, "duration = " + duration);
        Log.v(LOG_TAG, "gameMode = " + gameMode);

        ContentValues extraMatchDetails = new ContentValues();
        if(radiantWin) {
            extraMatchDetails.put(MatchesContract.MatchesEntry.COLUMN_RADIANT_WIN, 1);
        }else{
            extraMatchDetails.put(MatchesContract.MatchesEntry.COLUMN_RADIANT_WIN, 0);
        }
        extraMatchDetails.put(MatchesContract.MatchesEntry.COLUMN_DURATION, duration);
        extraMatchDetails.put(MatchesContract.MatchesEntry.COLUMN_GAME_MODE, gameMode);
        extraMatchDetails.put(MatchesContract.MatchesEntry.COLUMN_MATCH_DETAILS_AVAILABLE, 1);

        this.getContentResolver().update(
                MatchesContract.MatchesEntry.buildMatchUriWithMatchID(matchID),
                extraMatchDetails,
                null,
                null
        );

        //getting players data
        final String D2A_PLAYER_ACCOUNT_ID = "account_id";
        final String D2A_PLAYER_PLAYER_SLOT = "player_slot";
        final String D2A_PLAYER_HERO_ID = "hero_id";
        final String D2A_PLAYER_ITEM_0 = "item_0";
        final String D2A_PLAYER_ITEM_1 = "item_1";
        final String D2A_PLAYER_ITEM_2 = "item_2";
        final String D2A_PLAYER_ITEM_3 = "item_3";
        final String D2A_PLAYER_ITEM_4 = "item_4";
        final String D2A_PLAYER_ITEM_5 = "item_5";
        final String D2A_PLAYER_KILLS  = "kills";
        final String D2A_PLAYER_DEATHS = "deaths";
        final String D2A_PLAYER_ASSISTS = "assists";
        final String D2A_PLAYER_GOLD = "gold";
        final String D2A_PLAYER_GOLD_SPENT = "gold_spent";
        final String D2A_PLAYER_LAST_HITS = "last_hits";
        final String D2A_PLAYER_DENIES = "denies";
        final String D2A_PLAYER_GPM = "gold_per_min";
        final String D2A_PLAYER_XPM = "xp_per_min";
        final String SAP_RESPONSE = "response";
        final String SAP_PLAYERS = "players";
        final String SAP_NICKNAME = "personaname";
        final String SAP_AVATAR = "avatarfull";


        JSONArray players = matchDetailsJson.getJSONArray(D2A_PLAYERS);
        Vector<ContentValues> cVVector = new Vector<>(players.length());

        for(int i = 0; i < players.length(); i++){

            JSONObject player = players.getJSONObject(i);
            long accountId = player.getLong(D2A_PLAYER_ACCOUNT_ID);
            int playerSlot = player.getInt(D2A_PLAYER_PLAYER_SLOT);
            int heroId = player.getInt(D2A_PLAYER_HERO_ID);
            int item0 = player.getInt(D2A_PLAYER_ITEM_0);
            int item1 = player.getInt(D2A_PLAYER_ITEM_1);
            int item2 = player.getInt(D2A_PLAYER_ITEM_2);
            int item3 = player.getInt(D2A_PLAYER_ITEM_3);
            int item4 = player.getInt(D2A_PLAYER_ITEM_4);
            int item5 = player.getInt(D2A_PLAYER_ITEM_5);
            int kills = player.getInt(D2A_PLAYER_KILLS);
            int deaths = player.getInt(D2A_PLAYER_DEATHS);
            int assists = player.getInt(D2A_PLAYER_ASSISTS);
            int totalGold = player.getInt(D2A_PLAYER_GOLD) + player.getInt(D2A_PLAYER_GOLD_SPENT);
            int lastHits = player.getInt(D2A_PLAYER_LAST_HITS);
            int denies = player.getInt(D2A_PLAYER_DENIES);
            int gpm = player.getInt(D2A_PLAYER_GPM);
            int xpm = player.getInt(D2A_PLAYER_XPM);
            String nickName;
            String avatarUrl;

            //player's acount is private
            if(accountId != PRIVATE_ACCOUNT_ID) {
                String userSteamSummary = getSteamUserSummary(Utility.getSteam64ID(accountId));
                JSONObject steamUser = new JSONObject(userSteamSummary).
                        getJSONObject(SAP_RESPONSE).
                        getJSONArray(SAP_PLAYERS).
                        getJSONObject(0);

                nickName = steamUser.getString(SAP_NICKNAME);
                avatarUrl = steamUser.getString(SAP_AVATAR);
            }else{
                //player's acount is private which means we cant get his name or avatar
                nickName = "Anonymous";
                avatarUrl = "Anonymous";
            }

            ContentValues playerValues = new ContentValues();

            playerValues.put(MatchesContract.PlayersEntry.COLUMN_ACCOUNT_ID, accountId);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_MATCH_ID, matchID);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_PLAYER_SLOT, playerSlot);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_HERO_ID, heroId);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_ITEM_0, item0);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_ITEM_1, item1);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_ITEM_2, item2);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_ITEM_3, item3);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_ITEM_4, item4);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_ITEM_5, item5);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_KILLS, kills);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_DEATHS, deaths);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_ASSISTS, assists);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_GOLD, totalGold);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_LAST_HITS, lastHits);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_DENIES, denies);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_GPM, gpm);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_XPM, xpm);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_NAME, nickName);
            playerValues.put(MatchesContract.PlayersEntry.COLUMN_AVATAR_URL, avatarUrl);
            cVVector.add(playerValues);
        }

        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] playersData = cVVector.toArray(new ContentValues[cVVector.size()]);
            context.getContentResolver().bulkInsert(MatchesContract.PlayersEntry.CONTENT_URI, playersData);
        }
    }

    private String getSteamUserSummary(long steam64ID){

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;

        // Will contain the raw JSON response as a string.
        String steamUserDetailsJsonStr = null;


        try {
            // Construct the URL for the dota api query
            final String MATCH_DETAILS_BASE_URL =
                    "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?";
            final String API_KEY_PARAM = "key";
            final String STEAM_ID_PARAM = "steamids";

            Uri builtUri = Uri.parse(MATCH_DETAILS_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, Utility.API_KEY)
                    .appendQueryParameter(STEAM_ID_PARAM, Long.toString(steam64ID))
                    .build();


            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            Log.v(LOG_TAG, "fetching steam user details steamID = " + Utility.getSteam32ID(steam64ID));

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            steamUserDetailsJsonStr = Utility.inputStreamToJsonStr(inputStream);


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
        return steamUserDetailsJsonStr;
    }

    private void sendDetailsFetchdBroadcast(Context context) {
        Intent intent = new Intent(MATCH_DETAILS_FETCHED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
