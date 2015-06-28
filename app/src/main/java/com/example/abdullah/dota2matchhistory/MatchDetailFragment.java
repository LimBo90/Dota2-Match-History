package com.example.abdullah.dota2matchhistory;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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


    private long mMatchID = 0;
    public TextView tv = null;
    private LinearLayout mProgressBarContainer ;
    private Boolean isFetchingMatch = false;

    private BroadcastReceiver mOnMatchFetchedReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(LOG_TAG, "match details fetched");
            isFetchingMatch = false;
            if(tv != null){
                tv.setText("matchID = " + mMatchID);
            }
            if(mProgressBarContainer != null){
                mProgressBarContainer.setVisibility(View.GONE);
                tv.setVisibility(View.VISIBLE);
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
            if(getArguments() != null){
                mMatchID = MatchesContract.MatchesEntry.getMatchIDFromUri((Uri)getArguments().getParcelable(MATCH_DETAIL_URI));
                Intent intent = new Intent(getActivity(), MatchDetailsService.class);
                intent.putExtra(MatchDetailsService.MATCH_ID_KEY, mMatchID);
                getActivity().startService(intent);
                isFetchingMatch = true;
                Log.v(LOG_TAG, "service called");
            }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_match_detail, container, false);
        tv = (TextView)rootView.findViewById(R.id.fragment_match_details_textview);
        mProgressBarContainer = (LinearLayout)rootView.findViewById(R.id.fragment_detail_progress_bar);


        if(isFetchingMatch){
            mProgressBarContainer.setVisibility(View.VISIBLE);
            Log.v(LOG_TAG , "progres bar visible");
            tv.setVisibility(View.GONE);
        }else{
            tv.setVisibility(View.VISIBLE);
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
}
