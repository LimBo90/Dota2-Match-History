package com.example.abdullah.dota2matchhistory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    private OnMatchSelectedListener mOnMatchSelectedListener;
    private MatchHistoryCursorAdapter mMatchHistoryAdapter;
    private SwipeRefreshLayout mSwipeRefreshLyout;
    private boolean mTwoPane;

    private BroadcastReceiver mSyncFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //sync finished stop the refreshing icon of the listview
            mSwipeRefreshLyout.setRefreshing(false);
            mPosition = ListView.INVALID_POSITION;
        }
    };


    // Container Activity must implement this interface
    public interface OnMatchSelectedListener {
        public void onMatchSelected(Uri matchUriWihMatchId);
    }

    private static final String[] MATCHES_COLUMNS = {
            MatchesContract.MatchesEntry.TABLE_NAME + "." + MatchesContract.MatchesEntry._ID,
            MatchesContract.MatchesEntry.COLUMN_MATCH_ID ,
            MatchesContract.MatchesEntry.COLUMN_START_TIME,
            MatchesContract.MatchesEntry.COLUMN_LOBBY_TYPE,
            MatchesContract.MatchesEntry.COLUMN_USER_HERO
    };

    // These indices are tied to MATCHES_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_ID = 0;
    static final int COL_MATCH_ID = 1;
    static final int COL_START_TIME = 2;
    static  final int COL_LOBBY_TYPE = 3;
    static final int COL_USER_HERO = 4;


    public void setTwoPane(boolean TwoPane) {
        mTwoPane = TwoPane;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MATCHES_LOADER_ID, null, this);
        try {
            mOnMatchSelectedListener = (OnMatchSelectedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnMatchSelectedListener");
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSyncFinishedReceiver,
                new IntentFilter(MatchHistorySyncAdapter.SYNC_FINISHED));
        super.onResume();
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
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    mOnMatchSelectedListener.onMatchSelected(MatchesContract.MatchesEntry.buildMatchUriWithMatchID(
                            cursor.getLong(COL_MATCH_ID)));
                }
                mPosition = position;
            }
        });
        mSwipeRefreshLyout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLyout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateMatches();
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(SAVED_POSITION)){
            mPosition = savedInstanceState.getInt(SAVED_POSITION);
        }

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "onCreateLoader");
        // Sort order:  Ascending, by date.
        String sortOrder = MatchesContract.MatchesEntry.COLUMN_START_TIME + " DESC";
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
//        Toast toast =  Toast.makeText(getActivity(), "no of items shown = " + data.getCount(), Toast.LENGTH_SHORT);
//        toast.show();

        if(mPosition == ListView.INVALID_POSITION && mTwoPane) {
            // That means no item is selected, so the first item is selected if it's a two pane layout
            mListView.post(new Runnable() {
                @Override
                public void run() {
                    mListView.performItemClick(null, 0, mListView.getItemIdAtPosition(0));
                }
            });
        }else{
            //scroll to the last position if it's a one pane layout
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mSyncFinishedReceiver);
    }

    private void updateMatches(){
        Toast.makeText(getActivity(), "list refresh", Toast.LENGTH_SHORT);
        MatchHistorySyncAdapter.syncImmediately(getActivity());
        mSwipeRefreshLyout.setRefreshing(true);
        mPosition=ListView.INVALID_POSITION;
        getLoaderManager().restartLoader(MATCHES_LOADER_ID, null, this);
    }

}
