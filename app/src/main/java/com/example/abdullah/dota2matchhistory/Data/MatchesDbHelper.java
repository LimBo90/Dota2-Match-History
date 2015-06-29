package com.example.abdullah.dota2matchhistory.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.abdullah.dota2matchhistory.Data.MatchesContract.MatchesEntry;
import com.example.abdullah.dota2matchhistory.Data.MatchesContract.PlayersEntry;
import com.example.abdullah.dota2matchhistory.Data.MatchesContract.HeroEntry;
import com.example.abdullah.dota2matchhistory.Data.MatchesContract.ItemEntry;


public class MatchesDbHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = MatchesDbHelper.class.getSimpleName();

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "match_history.db";

    public MatchesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MATCHES_TABLE = "CREATE TABLE " + MatchesEntry.TABLE_NAME + " (" +
                MatchesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                MatchesEntry.COLUMN_MATCH_ID + " INTEGER NOT NULL, " +
                MatchesEntry.COLUMN_START_TIME + " INTEGER NOT NULL, " +
                MatchesEntry.COLUMN_LOBBY_TYPE + " INTEGER NOT NULL, " +
                MatchesEntry.COLUMN_USER_HERO + " INTEGER NOT NULL, " +
                MatchesEntry.COLUMN_MATCH_DETAILS_AVAILABLE + " INTEGER DEFAULT 0, " +
                MatchesEntry.COLUMN_RADIANT_WIN + " INTEGER DEFAULT 0, " +
                MatchesEntry.COLUMN_GAME_MODE + " INTEGER DEFAULT 0, " +
                MatchesEntry.COLUMN_DURATION + " INTEGER DEFAULT 0, " +
                " UNIQUE (" + MatchesEntry.COLUMN_MATCH_ID + ") ON CONFLICT IGNORE);";

        Log.v(LOG_TAG,"matces table SQL statment =" + SQL_CREATE_MATCHES_TABLE);

        final String SQL_CREATE_PLAYERS_TABLE = "CREATE TABLE " + PlayersEntry.TABLE_NAME + " (" +
                PlayersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PlayersEntry.COLUMN_MATCH_ID + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_ACCOUNT_ID + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PlayersEntry.COLUMN_AVATAR_URL + " TEXT NOT NULL, " +
                PlayersEntry.COLUMN_PLAYER_SLOT + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_HERO_ID + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_ITEM_0 + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_ITEM_1 + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_ITEM_2 + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_ITEM_3 + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_ITEM_4 + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_ITEM_5 + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_KILLS  + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_DEATHS + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_ASSISTS + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_GOLD + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_LAST_HITS + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_DENIES + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_XPM + " INTEGER NOT NULL, " +
                PlayersEntry.COLUMN_GPM + " INTEGER NOT NULL, " +

                " UNIQUE (" + PlayersEntry.COLUMN_MATCH_ID + ", "
                + PlayersEntry.COLUMN_PLAYER_SLOT + ") ON CONFLICT REPLACE);";


        Log.v(LOG_TAG,"players table SQL statment =" + SQL_CREATE_PLAYERS_TABLE);

        //Heroes table
        final String SQL_CREATE_HEROES_TABLE = "CREATE TABLE " + HeroEntry.TABLE_NAME + " (" +
                HeroEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                HeroEntry.COLUMN_HERO_ID+ " INTEGER NOT NULL, " +
                HeroEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                HeroEntry.COLUMN_LOCALIZED_NAME + " TEXT NOT NULL, " +
                " UNIQUE (" + HeroEntry.COLUMN_HERO_ID + ") ON CONFLICT IGNORE);";

        Log.v(LOG_TAG,"heroes table SQL statment =" + SQL_CREATE_HEROES_TABLE);

        // Items table
        final String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME+ " (" +
                MatchesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                ItemEntry.COLUMN_ITEM_ID + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_LOCALIZED_NAME + " TEXT NOT NULL, " +

                " UNIQUE (" + ItemEntry.COLUMN_ITEM_ID  + ") ON CONFLICT IGNORE);";

        Log.v(LOG_TAG,"matces table SQL statment =" + SQL_CREATE_ITEMS_TABLE);

        sqLiteDatabase.execSQL(SQL_CREATE_MATCHES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PLAYERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_HEROES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEMS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MatchesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlayersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HeroEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
