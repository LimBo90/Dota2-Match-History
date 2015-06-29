package com.example.abdullah.dota2matchhistory;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.abdullah.dota2matchhistory.Data.MatchesContract;
import com.example.abdullah.dota2matchhistory.service.MatchDetailsService;


/**
 * A placeholder fragment containing a simple view.
 */
public class MatchDetailFragment extends Fragment{

    private static final String LOG_TAG = MatchDetailFragment.class.getSimpleName();
    public static final String MATCH_DETAIL_URI = "matchDetailUri";
    public static final String MATCH_ID_KEY = "matchId";




    private long mMatchID = 0;
    private LinearLayout mProgressBarContainer;
    private LinearLayout mViewPagerAndTabs;
    private RelativeLayout mErrorMessage;
    private Boolean isFetchingMatch = true;
    private Boolean connectionFailed = false;


    private BroadcastReceiver mOnMatchFetchedReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(LOG_TAG, "match details fetched");
            isFetchingMatch = false;
            displayContent();
        }
    };
    private BroadcastReceiver mOnConnectionChangerListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Utility.isNetworkAvailable(context)){
                fetchMatchDetailsFromApi();
                context.unregisterReceiver(mOnConnectionChangerListener);
                isFetchingMatch=true;
            }
        }
    };

    public MatchDetailFragment() {
    }


    @Override
    public void onAttach(Activity activity) {
        if (getArguments() != null) {
            Uri matchIDUri = (Uri) getArguments().getParcelable(MATCH_DETAIL_URI);
            mMatchID = MatchesContract.MatchesEntry.getMatchIDFromUri(matchIDUri);

            if (!isMatchDetailsInDatabase(matchIDUri)) {
                //getting match details from api
                if(Utility.isNetworkAvailable(getActivity())) {
                    //network available
                LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOnMatchFetchedReciever,
                        new IntentFilter(MatchDetailsService.MATCH_DETAILS_FETCHED));
                    fetchMatchDetailsFromApi();

                }else{
                    //network not available
                    connectionFailed = true;
                    displayErrorMessage(getString(R.string.connection_failed), getString(R.string.check_internet_connection));
                    getActivity().registerReceiver(mOnConnectionChangerListener,
                            new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                }

            }else{
                isFetchingMatch = false;
            }
        }
        super.onAttach(activity);
    }

    private void fetchMatchDetailsFromApi() {
        Intent intent = new Intent(getActivity(), MatchDetailsService.class);
        intent.putExtra(MatchDetailsService.MATCH_ID_KEY, mMatchID);
        isFetchingMatch = true;
        getActivity().startService(intent);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOnMatchFetchedReciever,
                    new IntentFilter(MatchDetailsService.MATCH_DETAILS_FETCHED));

        displayProgressBar();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_detail, container, false);
        mProgressBarContainer = (LinearLayout) rootView.findViewById(R.id.fragment_detail_progress_bar);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.fragment_detail_pager);
        TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.fragmernt_detail_tabs);
        MatchDetailsPagerAdapter pagerAdapter = new MatchDetailsPagerAdapter(this.getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        mViewPagerAndTabs = (LinearLayout) rootView.findViewById(R.id.fragment_detail_tabs_and_pager);
        mErrorMessage = (RelativeLayout)rootView.findViewById(R.id.connection_failed);
        tabLayout.setupWithViewPager(viewPager);

        if(connectionFailed){
            displayErrorMessage(getString(R.string.connection_failed), getString(R.string.check_internet_connection));
        }else if(isFetchingMatch){
            displayProgressBar();
        }else {
            displayContent();
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
                    Bundle args = new Bundle();
                    Log.v(LOG_TAG, "stats tab matchID = " + mMatchID);
                    args.putLong(MATCH_ID_KEY, mMatchID);
                    sf.setArguments(args);
                    return sf;
                }
                case 1: {
                    PlayersListFragment pls = new PlayersListFragment();
                    Bundle args = new Bundle();
                    Log.v(LOG_TAG, "radiant tab matchID = " + mMatchID);
                    args.putLong(MATCH_ID_KEY, mMatchID);
                    args.putBoolean(PlayersListFragment.IS_RADIANT_PLAYERS_LIST, true);
                    pls.setArguments(args);
                    return pls;
                }
                case 2: {
                    PlayersListFragment pls = new PlayersListFragment();
                    Bundle args = new Bundle();
                    args.putLong(MATCH_ID_KEY, mMatchID);
                    Log.v(LOG_TAG, "dire tab matchID = " + mMatchID);
                    args.putLong(MATCH_ID_KEY, mMatchID);
                    args.putBoolean(PlayersListFragment.IS_RADIANT_PLAYERS_LIST, false);
                    pls.setArguments(args);
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


    private void displayProgressBar(){
        if(mViewPagerAndTabs != null)
            mViewPagerAndTabs.setVisibility(View.GONE);
        if(mProgressBarContainer != null)
            mProgressBarContainer.setVisibility(View.VISIBLE);
        if(mErrorMessage != null)
            mErrorMessage.setVisibility(View.GONE);
    }

    private void displayContent(){
        if(mProgressBarContainer != null)
            mProgressBarContainer.setVisibility(View.GONE);
        if(mViewPagerAndTabs != null)
            mViewPagerAndTabs.setVisibility(View.VISIBLE);
        if(mErrorMessage != null)
            mErrorMessage.setVisibility(View.GONE);
    }

    private void displayErrorMessage(String errorMainMessage, String errorSecondMessage){
        if(mProgressBarContainer != null)
            mProgressBarContainer.setVisibility(View.GONE);
        if(mViewPagerAndTabs != null)
            mViewPagerAndTabs.setVisibility(View.GONE);
        if(mErrorMessage != null) {
            mErrorMessage.setVisibility(View.VISIBLE);
            TextView errorTextMain = (TextView) mErrorMessage.findViewById(R.id.error_text_main);
            TextView errorTextSec = (TextView) mErrorMessage.findViewById(R.id.error_text_second);
            errorTextMain.setText(errorMainMessage);
            errorTextSec.setText(errorSecondMessage);
        }
    }

}
