package com.example.abdullah.dota2matchhistory;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.abdullah.dota2matchhistory.Data.MatchesContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlayersListFragment extends Fragment {
    private final static String LOG_TAG = PlayersListFragment.class.getSimpleName();
    public static final String IS_RADIANT_PLAYERS_LIST = "isRadiant";

    ListView mListView;
    Cursor mPlayersCursor;

    private static final String[] PLAYERS_COLUMNS = {
            MatchesContract.PlayersEntry.TABLE_NAME + "." + MatchesContract.MatchesEntry._ID,
            MatchesContract.PlayersEntry.COLUMN_ACCOUNT_ID,
            MatchesContract.PlayersEntry.COLUMN_NAME,
            MatchesContract.PlayersEntry.COLUMN_AVATAR_URL,
            MatchesContract.PlayersEntry.COLUMN_PLAYER_SLOT,
            MatchesContract.PlayersEntry.COLUMN_HERO_ID,
            MatchesContract.PlayersEntry.COLUMN_ITEM_0,
            MatchesContract.PlayersEntry.COLUMN_ITEM_1,
            MatchesContract.PlayersEntry.COLUMN_ITEM_2,
            MatchesContract.PlayersEntry.COLUMN_ITEM_3,
            MatchesContract.PlayersEntry.COLUMN_ITEM_4,
            MatchesContract.PlayersEntry.COLUMN_ITEM_5,
            MatchesContract.PlayersEntry.COLUMN_KILLS,
            MatchesContract.PlayersEntry.COLUMN_DEATHS,
            MatchesContract.PlayersEntry.COLUMN_ASSISTS,
            MatchesContract.PlayersEntry.COLUMN_GOLD,
            MatchesContract.PlayersEntry.COLUMN_LAST_HITS,
            MatchesContract.PlayersEntry.COLUMN_DENIES,
            MatchesContract.PlayersEntry.COLUMN_GPM,
            MatchesContract.PlayersEntry.COLUMN_XPM,
            MatchesContract.PlayersEntry.COLUMN_LEVEL,
            MatchesContract.PlayersEntry.COLUMN_LEAVER_STATUS
    };

    // These indices are tied to MATCHES_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_PLAER_ID = 0;
    static final int COL_PLAYER_ACCOUNT_ID = 1;
    static final int COL_PLAER_NAME = 2;
    static final int COL_PLAER_AVATAR_URL= 3;
    static final int COL_PLAER_PLAYER_SLOT = 4;
    static final int COL_PLAER_HERO_ID = 5;
    static final int COL_PLAYER_ITEM_0 = 6;
    static final int COL_PLAYER_ITEM_1 = 7;
    static final int COL_PLAYER_ITEM_2 = 8;
    static final int COL_PLAYER_ITEM_3 = 9;
    static final int COL_PLAYER_ITEM_4 = 10;
    static final int COL_PLAYER_ITEM_5 = 11;
    static final int COL_PLAER_KILLS = 12;
    static final int COL_PLAER_DEATHS = 13;
    static final int COL_PLAER_ASSISTS = 14;
    static final int COL_PLAER_GOLD = 15;
    static final int COL_PLAER_LAST_HITS = 16;
    static final int COL_PLAYER_DENIES = 17;
    static final int COL_PLAER_GPM = 18;
    static final int COL_PLAER_XPM = 19;
    static final int COL_PLAYER_LEVEL = 20;
    static final int COL_LEAVER_STATUS = 21;

    /*
    public void PlayerListFragment(ContentValues[] playersData){
        mPlayersData = playersData;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_player_list, null);
        mListView = (ListView) rootView.findViewById(R.id.players_list);
        long matchID = getArguments().getLong(MatchDetailFragment.MATCH_ID_KEY);
        Boolean isRadiant =  getArguments().getBoolean(IS_RADIANT_PLAYERS_LIST);

        if(isRadiant) {
            mPlayersCursor = getActivity().getContentResolver().query(
                    MatchesContract.PlayersEntry.buildPlayersUriWithMatchID(matchID),
                    PLAYERS_COLUMNS,
                    MatchesContract.PlayersEntry.COLUMN_PLAYER_SLOT + " < 5",
                    null,
                    MatchesContract.PlayersEntry.COLUMN_PLAYER_SLOT + " ASC");
        }else{

            mPlayersCursor = getActivity().getContentResolver().query(
                    MatchesContract.PlayersEntry.buildPlayersUriWithMatchID(matchID),
                    PLAYERS_COLUMNS,
                    MatchesContract.PlayersEntry.COLUMN_PLAYER_SLOT + " > 5",
                    null,
                    MatchesContract.PlayersEntry.COLUMN_PLAYER_SLOT + " ASC");
        }
        //binding match details from cursor
        mPlayersCursor.moveToFirst();
        if(mPlayersCursor != null)
            mListView.setAdapter(new PlayersCursorAdapter(getActivity(), mPlayersCursor, 0));

        //;

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mPlayersCursor != null)
            mPlayersCursor.close();
    }
}
