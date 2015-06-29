package com.example.abdullah.dota2matchhistory;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.abdullah.dota2matchhistory.Data.MatchesContract;
import com.example.abdullah.dota2matchhistory.service.MatchDetailsService;


/**
 * A placeholder fragment containing a simple view.
 */
public class MatchDetailFragment extends Fragment{
    private static final String LOG_TAG = MatchDetailFragment.class.getSimpleName();
    public static final String MATCH_DETAIL_URI = "matchDetailUri";
    private static final int MaTCH_DETAILS_LOADER_ID = 5;

    private static final String[] MATCHES_COLUMNS = {
            MatchesContract.MatchesEntry.TABLE_NAME + "." + MatchesContract.MatchesEntry._ID,
            MatchesContract.MatchesEntry.COLUMN_MATCH_ID,
            MatchesContract.MatchesEntry.COLUMN_START_TIME,
            MatchesContract.MatchesEntry.COLUMN_LOBBY_TYPE,
            MatchesContract.MatchesEntry.COLUMN_USER_HERO,
            MatchesContract.MatchesEntry.COLUMN_MATCH_DETAILS_AVAILABLE,
            MatchesContract.MatchesEntry.COLUMN_RADIANT_WIN,
            MatchesContract.MatchesEntry.COLUMN_DURATION,
            MatchesContract.MatchesEntry.COLUMN_GAME_MODE,
    };

    // These indices are tied to MATCHES_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_ID = 0;
    static final int COL_MATCH_ID = 1;
    static final int COL_START_TIME = 2;
    static final int COL_LOBBY_TYPE = 3;
    static final int COL_USER_HERO = 4;
    static final int COL_MATCH_DETAILS_AVAILABLE = 5;
    static final int COL_RADIANT_WIN = 6;
    static final int COL_DURATION = 7;
    static final int COL_GAME_MODE = 8;

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
            MatchesContract.PlayersEntry.COLUMN_XPM
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

    private long mMatchID = 0;
    private LinearLayout mProgressBarContainer;
    private Boolean isFetchingMatch = false;
    private LinearLayout mViewPagerAndTabs;

    private Cursor mMatchDetailsCursor;
    private Cursor mRadiantPlayersCursor;
    private Cursor mDirePlayersCursor;

    private BroadcastReceiver mOnMatchFetchedReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(LOG_TAG, "match details fetched");
            isFetchingMatch = false;
            fetchMatchDetailsFromDatabase();

            if (mProgressBarContainer != null) {
                mProgressBarContainer.setVisibility(View.GONE);
                mViewPagerAndTabs.setVisibility(View.VISIBLE);
            }
        }
    };

    public MatchDetailFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mDirePlayersCursor != null)
            mDirePlayersCursor.close();

        if(mRadiantPlayersCursor != null)
            mRadiantPlayersCursor.close();

        if(mMatchDetailsCursor != null)
            mMatchDetailsCursor.close();
    }

    @Override
    public void onAttach(Activity activity) {
        if (getArguments() != null) {
            Uri matchIDUri = (Uri) getArguments().getParcelable(MATCH_DETAIL_URI);
            mMatchID = MatchesContract.MatchesEntry.getMatchIDFromUri(matchIDUri);

            if (isMatchDetailsInDatabase(matchIDUri)) {
                fetchMatchDetailsFromDatabase();
            }else{
                LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOnMatchFetchedReciever,
                        new IntentFilter(MatchDetailsService.MATCH_DETAILS_FETCHED));
                fetchMatchDetailsFromApi();
            }
        }
        super.onAttach(activity);
    }

    private void fetchMatchDetailsFromApi() {
        Intent intent = new Intent(getActivity(), MatchDetailsService.class);
        intent.putExtra(MatchDetailsService.MATCH_ID_KEY, mMatchID);
        getActivity().startService(intent);
        isFetchingMatch = true;
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOnMatchFetchedReciever,
                new IntentFilter(MatchDetailsService.MATCH_DETAILS_FETCHED));
    }



    private void fetchMatchDetailsFromDatabase() {
        Log.v(LOG_TAG, "fetchMatchDetailsFromDatabase");
        ContentResolver contentResolver = getActivity().getContentResolver();
        // Getting matches details
        mMatchDetailsCursor = contentResolver.query(
                MatchesContract.MatchesEntry.buildMatchUriWithMatchID(mMatchID),
                MATCHES_COLUMNS,
                null,
                null,
                null
        );
        // Getting radiant players
        mRadiantPlayersCursor = contentResolver.query(
                MatchesContract.PlayersEntry.buildPlayersUriWithMatchID(mMatchID),
                PLAYERS_COLUMNS,
                MatchesContract.PlayersEntry.COLUMN_PLAYER_SLOT + " < 5",
                null,
                MatchesContract.PlayersEntry.COLUMN_PLAYER_SLOT + " ASC");
        // Gettind dire players
        mDirePlayersCursor = contentResolver.query(
                MatchesContract.PlayersEntry.buildPlayersUriWithMatchID(mMatchID),
                PLAYERS_COLUMNS,
                MatchesContract.PlayersEntry.COLUMN_PLAYER_SLOT + " > 5",
                null,
                MatchesContract.PlayersEntry.COLUMN_PLAYER_SLOT + " ASC"
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_detail, container, false);
        mProgressBarContainer = (LinearLayout) rootView.findViewById(R.id.fragment_detail_progress_bar);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.fragment_detail_pager);
        TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.fragmernt_detail_tabs);
        MatchDetailsPagerAdapter pagerAdapter = new MatchDetailsPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        mViewPagerAndTabs = (LinearLayout) rootView.findViewById(R.id.fragment_detail_tabs_and_pager);
        tabLayout.setupWithViewPager(viewPager);



        if (isFetchingMatch) {
            mProgressBarContainer.setVisibility(View.VISIBLE);
            Log.v(LOG_TAG, "progres bar visible");
            mViewPagerAndTabs.setVisibility(View.GONE);
        } else {
            mViewPagerAndTabs.setVisibility(View.VISIBLE);
            mProgressBarContainer.setVisibility(View.GONE);
            Log.v(LOG_TAG, "textview visible");
        }

        return rootView;
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mOnMatchFetchedReciever);
        super.onPause();
    }

    /**
     * Checks if the match details are already downloaded into the database
     *
     * @param matchWithIdUri match Uri with match id
     * @return true if the match details are in the database
     */
    public boolean isMatchDetailsInDatabase(Uri matchWithIdUri) {
        String[] projection = {MatchesContract.MatchesEntry.COLUMN_MATCH_DETAILS_AVAILABLE};
        Cursor cursor = getActivity().getContentResolver().query(
                matchWithIdUri,
                projection,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            int matchInDataBase = cursor.getInt(0);
            if (matchInDataBase == 0) {
                Log.v(LOG_TAG, "match not available in database");
                return false;
            } else {
                Log.v(LOG_TAG, "match available in database");
                return true;
            }
        } else {
            Log.e(LOG_TAG, "cursor returned empty from query with matchID");
            return false;
        }
    }



    public class MatchDetailsPagerAdapter extends FragmentPagerAdapter {
        private String[] mTabs = {"Stats", "Radiant", "Dire"};

        public MatchDetailsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            Log.v(LOG_TAG, "getItem Called position = " + position);
            switch (position) {
                case 0: {
                    StatsFragment sf = new StatsFragment();
                    sf.setMatchDetailsCursor(mMatchDetailsCursor);
                    return sf;
                }
                case 1: {
                    PlayersListFragment pls = new PlayersListFragment();
                    pls.setPlayersCursor(mRadiantPlayersCursor);
                    return pls;
                }
                case 2: {
                    PlayersListFragment pls = new PlayersListFragment();
                    pls.setPlayersCursor(mDirePlayersCursor);
                    return pls;
                }
            }
            throw new UnsupportedOperationException("Unknown tab position  position = " + position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs[position];
        }
    }
}
