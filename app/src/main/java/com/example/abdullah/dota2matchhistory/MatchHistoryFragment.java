package com.example.abdullah.dota2matchhistory;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MatchHistoryFragment extends Fragment {
    ListView mListView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_match_history, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_match_history);


        //TODO remove and implement real adapter
        String[] data = {
                "Leshrac",
                "Pudge",
                "Pugna",
                "Sand King",
                "Sand King",
                "Sand King",
                "Sand King"
        };
        List<String> matches = new ArrayList<>(Arrays.asList(data));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_match,
                R.id.player_name,
                matches);
        mListView.setAdapter(arrayAdapter);
        return rootView;
    }

}
