package com.example.abdullah.dota2matchhistory;

import android.app.Activity;
import android.content.BroadcastReceiver;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abdullah.dota2matchhistory.Data.MatchesContract;
import com.example.abdullah.dota2matchhistory.service.MatchDetailsService;


/**
 * A placeholder fragment containing a simple view.
 */
public class MatchDetailFragment extends Fragment {
    public static final String LOG_TAG = MatchDetailFragment.class.getSimpleName();
    public static final String MATCH_DETAIL_URI = "matchDetailUri";

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


    private long mMatchID = 0;
    public TextView tv = null;
    private LinearLayout mProgressBarContainer;
    private Boolean isFetchingMatch = false;
    private LinearLayout mViewPagerAndTabs;

    private BroadcastReceiver mOnMatchFetchedReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(LOG_TAG, "match details fetched");
            isFetchingMatch = false;
            if (mProgressBarContainer != null) {
                mProgressBarContainer.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
                mViewPagerAndTabs.setVisibility(View.VISIBLE);
            }
        }
    };

    public MatchDetailFragment() {
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOnMatchFetchedReciever,
                new IntentFilter(MatchDetailsService.MATCH_DETAILS_FETCHED));
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        if (getArguments() != null) {
            Uri matchIDUri = (Uri) getArguments().getParcelable(MATCH_DETAIL_URI);
            mMatchID = MatchesContract.MatchesEntry.getMatchIDFromUri(matchIDUri);

            if (!isMatchDetailsInDatabase(matchIDUri)) {
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
        Log.v(LOG_TAG, "service called");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOnMatchFetchedReciever,
                new IntentFilter(MatchDetailsService.MATCH_DETAILS_FETCHED));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_detail, container, false);
        tv = (TextView) rootView.findViewById(R.id.fragment_match_details_textview);
        mProgressBarContainer = (LinearLayout) rootView.findViewById(R.id.fragment_detail_progress_bar);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.fragment_detail_pager);
        TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.fragmernt_detail_tabs);
        MatchDetailsPagerAdapter pagerAdapter = new MatchDetailsPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        mViewPagerAndTabs = (LinearLayout) rootView.findViewById(R.id.fragment_detail_tabs_and_pager);
        tabLayout.setupWithViewPager(viewPager);

        if (tv != null) {
            tv.setText("matchID = " + mMatchID);
        }

        if (isFetchingMatch) {
            mProgressBarContainer.setVisibility(View.VISIBLE);
            Log.v(LOG_TAG, "progres bar visible");
            tv.setVisibility(View.GONE);
            mViewPagerAndTabs.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
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
        private String[] mTabs = {"Stats", "Dire", "Radiant"};

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
            if (position == 0) {
                return new StatsFragment();
            } else {
                PlayersListFragment tf = new PlayersListFragment();
                return tf;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs[position];
        }
    }
}
