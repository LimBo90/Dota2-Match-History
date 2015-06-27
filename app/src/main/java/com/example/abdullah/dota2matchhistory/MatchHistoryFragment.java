package com.example.abdullah.dota2matchhistory;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.abdullah.dota2matchhistory.Data.MatchesContract;
import com.example.abdullah.dota2matchhistory.sync.MatchHistorySyncAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MatchHistoryFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = MatchHistoryFragment.class.getSimpleName();


    private final int MATCHES_LOADER_ID = 0;
    private final String SAVED_POSITION = "savedPosition";
    ListView mListView = null;
    private int mPosition = ListView.INVALID_POSITION;
    private Boolean mTwoPane;
    private MatchHistoryCursorAdapter mMatchHistoryAdapter;


    private static final String[] MATCHES_COLUMNS = {
            // In this case tjava.lang.Stringhe id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MatchesContract.MatchesEntry.TABLE_NAME + "." + MatchesContract.MatchesEntry._ID,
            MatchesContract.MatchesEntry.COLUMN_MATCH_ID ,
            MatchesContract.MatchesEntry.COLUMN_START_TIME,
            MatchesContract.MatchesEntry.COLUMN_LOBBY_TYPE,
            MatchesContract.MatchesEntry.COLUMN_USER_HERO
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_ID = 0;
    static final int COL_MATCH_ID = 1;
    static final int COL_START_TIME = 2;
    static  final int COL_LOBBY_TYPE = 3;
    static final int COL_USER_HERO = 4;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MATCHES_LOADER_ID, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != ListView.INVALID_POSITION){
            outState.putInt(SAVED_POSITION, mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMatchHistoryAdapter = new MatchHistoryCursorAdapter(getActivity(), null, 0);

        View rootView =  inflater.inflate(R.layout.fragment_match_history, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_match_history);
        // Get a reference to the ListView, and attach this adapter to it.
        mListView.setAdapter(mMatchHistoryAdapter);


        if(savedInstanceState != null && savedInstanceState.containsKey(SAVED_POSITION)){
            mPosition = savedInstanceState.getInt(SAVED_POSITION);
        }

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader");
        // Sort order:  Ascending, by date.
        String sortOrder = MatchesContract.MatchesEntry.COLUMN_START_TIME+ " ASC";
        Uri matchesUri = MatchesContract.MatchesEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                matchesUri,
                MATCHES_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMatchHistoryAdapter.swapCursor(null);
        Log.v(LOG_TAG, "onLoaderReset");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished");
        mMatchHistoryAdapter.swapCursor(data);

        Toast toast =  Toast.makeText(getActivity(), "no of items shown = " + data.getCount(), Toast.LENGTH_SHORT);
        toast.show();
/*
        if(mPosition == ListView.INVALID_POSITION && mTwoPane) {
            // That means no item is selected, so the first item is selected automatecaly
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    mListView.performItemClick(null, 0, mListView.getItemIdAtPosition(0));
                }
            });

        }else{
            mListView.smoothScrollToPosition(mPosition);
        }
        */
    }
}
