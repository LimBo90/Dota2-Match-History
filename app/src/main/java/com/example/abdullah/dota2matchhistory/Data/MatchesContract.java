package com.example.abdullah.dota2matchhistory.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class MatchesContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.abdullah.dota2matchhistory";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_MATCHES = "matches";
    public static final String PATH_PLAYERS = "players";
    public static final String PATH_HEROES = "heroes";
    public static final String PATH_ITEMS = "items";

    /* Inner class that defines the table contents of the location table */
    public static final class MatchesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MATCHES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MATCHES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MATCHES;

        // Table name
        public static final String TABLE_NAME = "matches";


        public static final String COLUMN_MATCH_ID = "match_id";

        public static final String COLUMN_START_TIME = "start_time";

        public static final String COLUMN_LOBBY_TYPE = "lobby_type";

        public static final String COLUMN_USER_HERO = "user_hero";

        public static final String COLUMN_MATCH_DETAILS_AVAILABLE = "match_details_available";

        public static final String COLUMN_RADIANT_WIN = "radiant_win";

        public static final String COLUMN_GAME_MODE = "game_mode";

        public static final String COLUMN_DURATION = "duration";


        public static Uri buildMatchUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMatchUriWithMatchID(long matchID) {
            return CONTENT_URI.buildUpon().
                    appendPath(Long.toString(matchID)).
                    build();
        }

        public static long getMatchIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class PlayersEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYERS;

        public static final String TABLE_NAME = "players";

        public static final String COLUMN_MATCH_ID = "match_id";

        public static final String COLUMN_ACCOUNT_ID = "account_id";

        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_AVATAR_URL = "avatar_url";

        public static final String COLUMN_PLAYER_SLOT = "player_slot";

        public static final String COLUMN_HERO_ID = "hero_id";

        public static final String COLUMN_ITEM_0 = "item_0";

        public static final String COLUMN_ITEM_1 = "item_1";

        public static final String COLUMN_ITEM_2 = "item_2";

        public static final String COLUMN_ITEM_3 = "item_3";

        public static final String COLUMN_ITEM_4 = "item_4";

        public static final String COLUMN_ITEM_5 = "item_5";

        public static final String COLUMN_KILLS = "kills";

        public static final String COLUMN_DEATHS = "deaths";

        public static final String COLUMN_ASSISTS = "assists";

        public static final String COLUMN_GOLD = "gold";

        public static final String COLUMN_LAST_HITS = "last_hits";

        public static final String COLUMN_DENIES = "denies";

        public static final String COLUMN_XPM = "xpm";

        public static final String COLUMN_GPM = "gpm";


        public static Uri buildPlayersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static Uri buildPlayersUriWithMatchID(long matchID) {
            return CONTENT_URI.buildUpon().
                    appendPath(Long.toString(matchID)).
                    build();
        }

        public static Uri buildPlayersUriWithMatchIDAndAccountID(long matchID, long accountID) {
            return CONTENT_URI.buildUpon().
                    appendPath(Long.toString(matchID)).
                    appendPath(Long.toString(accountID)).
                    build();
        }

        public static long getMatchIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getAccountIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

    public static final class HeroEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HEROES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HEROES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HEROES;

        // Table name
        public static final String TABLE_NAME = "heroes";


        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_HERO_ID = "hero_id";
        public static final String COLUMN_LOCALIZED_NAME = "localized_name";

        public static Uri buildHeroUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildHeroUriWithHeroID(int heroID) {
            return CONTENT_URI.buildUpon().
                    appendPath(Integer.toString(heroID)).
                    build();
        }

        public static int getHeroIDFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }

    public static final class ItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // Table name
        public static final String TABLE_NAME = "items";


        public static final String COLUMN_NAME = "name";

        public static final String COLUMN_ITEM_ID = "item_id";

        public static final String COLUMN_LOCALIZED_NAME = "localized_name";

        public static Uri buildItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildItemUriWithItemID(int heroID) {
            return CONTENT_URI.buildUpon().
                    appendPath(Integer.toString(heroID)).
                    build();
        }

        public static int getItemIDFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
    }
}
