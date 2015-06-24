package com.example.abdullah.dota2matchhistory;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


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
        for(int i = 0; i<=10; i++){
            View list_item = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_match,null);
            mListView.addView(list_item);
        }
        return rootView;
    }
}
