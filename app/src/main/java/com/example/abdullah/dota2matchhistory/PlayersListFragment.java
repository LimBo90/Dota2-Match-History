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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlayersListFragment extends Fragment {
    private final static String LOG_TAG = PlayersListFragment.class.getSimpleName();

    ContentValues[] mPlayersData = null;
    ListView mListView;
    private Cursor mPlayersCursor;

    /*
    public void PlayerListFragment(ContentValues[] playersData){
        mPlayersData = playersData;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player_list, null);
        mListView = (ListView) rootView.findViewById(R.id.players_list);


        return rootView;
    }

    @Override
    public void onPause() {
        mPlayersCursor.close();
        super.onPause();
    }

    public void setPlayersCursor(Cursor playersCursor) {
        mPlayersCursor = playersCursor;
    }
}
