package com.example.abdullah.dota2matchhistory;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlayersListFragment extends Fragment {
    ContentValues[] mPlayersData = null;
    ListView mListView;

    /*
    public void PlayerListFragment(ContentValues[] playersData){
        mPlayersData = playersData;
    }
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player_list, null);
        mListView = (ListView) rootView.findViewById(R.id.players_list);

        //TODO remove and implement real adapter
        String[] data = {
                "tiktak90",
                "limbo",
                "asdfadsfa",
                "dadfaflkagl;jalkfasdfasgdfsgs",
                "asdfadfasffds",
        };
        List<String> players = new ArrayList<>(Arrays.asList(data));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.players_list_item,
                R.id.playr_name,
                players);

        mListView.setAdapter(arrayAdapter);

        return rootView;
    }
}
