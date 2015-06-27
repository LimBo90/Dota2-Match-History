package com.example.abdullah.dota2matchhistory.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class MatchesProvider extends ContentProvider
{
    private static final String LOG_TAG = MatchesProvider.class.getSimpleName();
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MatchesDbHelper mOpenHelper;

    static final int MATCHES = 100;
    static final int MATCHES_WITH_MATCH_ID= 101;

    static final int PLAYERS = 200;
    static final int PLAYERS_WITH_MATCH_ID= 201;
    static final int PLAYERS_WITH_MATCH_ID_AND_ACCOUNT_ID = 202;

    static final int HEROES = 300;
    static final int HEROES_WITH_ID = 301;

    static final int ITEMS = 400;
    static final int ITEMS_WITH_ID = 401;

    //match_id= ?
    private static final String sMatchesTableMatchIDSelection =
            MatchesContract.MatchesEntry.COLUMN_MATCH_ID + " = ? ";
    private static final String sPlayersTableMatchIDSelection =
            MatchesContract.PlayersEntry.COLUMN_MATCH_ID + " = ? ";

    // match_id = ? AND account_id = ?
    private static final String sMatchIDAndAccountIDSelection =
            MatchesContract.PlayersEntry.COLUMN_MATCH_ID + " = ? AND " +
            MatchesContract.PlayersEntry.COLUMN_ACCOUNT_ID + " = ? ";

    //hero_id = ?
    private static final String sHeroIDSelection =
            MatchesContract.HeroEntry.COLUMN_HERO_ID+ " = ? ";

    //item_id = ?
    private static final String sItemIDSelection =
            MatchesContract.ItemEntry.COLUMN_ITEM_ID+ " = ? ";


    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MatchesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, MatchesContract.PATH_MATCHES, MATCHES);
        uriMatcher.addURI(authority, MatchesContract.PATH_MATCHES + "/#" ,MATCHES_WITH_MATCH_ID);

        uriMatcher.addURI(authority, MatchesContract.PATH_PLAYERS, PLAYERS);
        uriMatcher.addURI(authority, MatchesContract.PATH_PLAYERS + "/#", PLAYERS_WITH_MATCH_ID);
        uriMatcher.addURI(authority, MatchesContract.PATH_PLAYERS + "/#" + "/#", PLAYERS_WITH_MATCH_ID_AND_ACCOUNT_ID);

        uriMatcher.addURI(authority, MatchesContract.PATH_HEROES, HEROES);
        uriMatcher.addURI(authority, MatchesContract.PATH_HEROES + "/#", HEROES_WITH_ID);

        uriMatcher.addURI(authority, MatchesContract.PATH_ITEMS, ITEMS);
        uriMatcher.addURI(authority, MatchesContract.PATH_ITEMS + "/#", ITEMS_WITH_ID);

        // 3) Return the new matcher!
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MatchesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MATCHES:
                return MatchesContract.MatchesEntry.CONTENT_TYPE;
            case MATCHES_WITH_MATCH_ID:
                return MatchesContract.MatchesEntry.CONTENT_ITEM_TYPE;

            case PLAYERS:
                return MatchesContract.PlayersEntry.CONTENT_TYPE;
            case PLAYERS_WITH_MATCH_ID:
                return MatchesContract.PlayersEntry.CONTENT_TYPE;
            case PLAYERS_WITH_MATCH_ID_AND_ACCOUNT_ID:
                return MatchesContract.PlayersEntry.CONTENT_ITEM_TYPE;

            case HEROES:
                return MatchesContract.HeroEntry.CONTENT_TYPE;
            case HEROES_WITH_ID:
                return MatchesContract.HeroEntry.CONTENT_ITEM_TYPE;

            case ITEMS:
                return MatchesContract.ItemEntry.CONTENT_TYPE;
            case ITEMS_WITH_ID:
                return MatchesContract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "matches/"
            case MATCHES: {
                retCursor = mOpenHelper.getReadableDatabase().query(MatchesContract.MatchesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "matches/#"
            case MATCHES_WITH_MATCH_ID: {
                long matchId = MatchesContract.MatchesEntry.getMatchIDFromUri(uri);
                selection = sMatchesTableMatchIDSelection;
                selectionArgs = new String[]{Long.toString(matchId)};
                retCursor = mOpenHelper.getReadableDatabase().query(MatchesContract.MatchesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            // "players/"
            case PLAYERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(MatchesContract.PlayersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "players/#"
            case PLAYERS_WITH_MATCH_ID: {
                long matchId = MatchesContract.PlayersEntry.getMatchIDFromUri(uri);
                selection = sPlayersTableMatchIDSelection;
                selectionArgs = new String[]{Long.toString(matchId)};

                retCursor = mOpenHelper.getReadableDatabase().query(MatchesContract.PlayersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "players/#/#"
            case PLAYERS_WITH_MATCH_ID_AND_ACCOUNT_ID:{
                long matchId = MatchesContract.PlayersEntry.getMatchIDFromUri(uri);
                long account_id = MatchesContract.PlayersEntry.getAccountIDFromUri(uri);

                selection = sMatchIDAndAccountIDSelection;
                selectionArgs = new String[]{Long.toString(matchId), Long.toString(account_id)};
                retCursor = mOpenHelper.getReadableDatabase().query(MatchesContract.PlayersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            // "heroes/"
            case HEROES:{
                retCursor = mOpenHelper.getReadableDatabase().query(MatchesContract.HeroEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "heroes/#"
            case HEROES_WITH_ID:{
                int heroId = MatchesContract.HeroEntry.getHeroIDFromUri(uri);
                selection = sHeroIDSelection;
                selectionArgs = new String[]{Integer.toString(heroId)};

                retCursor = mOpenHelper.getReadableDatabase().query(MatchesContract.HeroEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            // "heroes/"
            case ITEMS:{
                retCursor = mOpenHelper.getReadableDatabase().query(MatchesContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            // "heroes/#"
            case ITEMS_WITH_ID: {
                int itemID = MatchesContract.ItemEntry.getItemIDFromUri(uri);
                selection = sItemIDSelection;
                selectionArgs = new String[]{Integer.toString(itemID)};

                retCursor = mOpenHelper.getReadableDatabase().query(MatchesContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted = 0;
        if(selection == null)
            selection = "1";

        switch (sUriMatcher.match(uri)){
            case MATCHES:
                rowsDeleted = db.delete(MatchesContract.MatchesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case PLAYERS:
                rowsDeleted = db.delete(MatchesContract.PlayersEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case HEROES:
                rowsDeleted = db.delete(MatchesContract.HeroEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case ITEMS:
                rowsDeleted = db.delete(MatchesContract.ItemEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri:" + uri);
        }
        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.
        if(rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        // Student: return the actual rows deleted
        return rowsDeleted;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MATCHES: {
                long _id = db.insert(MatchesContract.MatchesEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MatchesContract.MatchesEntry.buildMatchUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PLAYERS: {
                long _id = db.insert(MatchesContract.PlayersEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MatchesContract.PlayersEntry.buildPlayersUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case HEROES: {
                long _id = db.insert(MatchesContract.HeroEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MatchesContract.HeroEntry.buildHeroUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case ITEMS: {
                long _id = db.insert(MatchesContract.ItemEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MatchesContract.ItemEntry.buildItemUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsImpacted = 0;
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        switch (sUriMatcher.match(uri)) {
            case MATCHES: {
                rowsImpacted = db.update(MatchesContract.MatchesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case MATCHES_WITH_MATCH_ID:{
                long matchId = MatchesContract.MatchesEntry.getMatchIDFromUri(uri);
                selection = sMatchesTableMatchIDSelection;
                selectionArgs = new String[]{Long.toString(matchId)};
                rowsImpacted = db.update(MatchesContract.MatchesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case PLAYERS: {
                rowsImpacted = db.update(MatchesContract.PlayersEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case HEROES: {
                rowsImpacted = db.update(MatchesContract.HeroEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case ITEMS: {
                rowsImpacted = db.update(MatchesContract.ItemEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unkown uri:" + uri);
        }

        if(rowsImpacted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsImpacted;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCHES: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MatchesContract.MatchesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case PLAYERS:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MatchesContract.PlayersEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case HEROES:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MatchesContract.HeroEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            Log.v(LOG_TAG, "insertion suceeded _id = " + _id);
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case ITEMS:{
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MatchesContract.ItemEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            Log.v(LOG_TAG, "insertion suceeded _id = " + _id);
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }


}
