package com.example.abdullah.dota2matchhistory;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abdullah.dota2matchhistory.Data.MatchesContract;

import org.w3c.dom.Text;

public class StatsFragment extends Fragment {
    private static final String LOG_TAG = StatsFragment.class.getSimpleName();
    Cursor mStatsCursor;

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



    static class ViewHolder{
        public final TextView matchIDView;
        public final TextView durationView;
        public final TextView dateView;
        public final TextView timeView;
        public final TextView gameModeView;
        public final TextView lobbyTypeView;
        public final TextView winningTeam;

        public ViewHolder(View view){
            matchIDView = (TextView)view.findViewById(R.id.stats_fragment_match_id);
            durationView =(TextView) view.findViewById(R.id.fragment_stats_duration);
            gameModeView =(TextView) view.findViewById(R.id.fragment_stats_game_mode);
            dateView =(TextView) view.findViewById(R.id.stats_fragment_date);
            timeView =(TextView) view.findViewById(R.id.stats_fragment_time);
            lobbyTypeView =(TextView) view.findViewById(R.id.fragment_stats_lobby_type);
            winningTeam =(TextView) view.findViewById(R.id.stats_fragment_winning_team);
        }
    }

    public void StatsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_stats, null);
        ViewHolder viewHolder = new ViewHolder(rootView);
        long matchID = getArguments().getLong(MatchDetailFragment.MATCH_ID_KEY);

        mStatsCursor = getActivity().getContentResolver().query(
                MatchesContract.MatchesEntry.buildMatchUriWithMatchID(matchID),
                MATCHES_COLUMNS,
                null,
                null,
                null);

        //binding match details from cursor
        mStatsCursor.moveToFirst();


        viewHolder.matchIDView.setText(
               "" + mStatsCursor.getLong(COL_MATCH_ID));

        int duration = mStatsCursor.getInt(COL_DURATION);
        viewHolder.durationView.setText(Utility.getFormattedDuration(duration));

        viewHolder.gameModeView.setText(Utility.getGameMode(
                mStatsCursor.getInt(COL_GAME_MODE)));

        long startTime = mStatsCursor.getLong(COL_START_TIME);
        viewHolder.dateView.setText(Utility.getDateString(startTime));
        viewHolder.timeView.setText(Utility.getTimeString(startTime));

        viewHolder.lobbyTypeView.setText(Utility.getLobbyType(
                mStatsCursor.getInt(COL_LOBBY_TYPE)));

        int radiantWin = mStatsCursor.getInt(COL_RADIANT_WIN);

        if(radiantWin == 0) {
            viewHolder.winningTeam.setText(getString(R.string.dire_win));
            viewHolder.winningTeam.setTextColor(getResources().getColor(R.color.dire));
        }else{
            viewHolder.winningTeam.setText(getString(R.string.radiant_win));
            viewHolder.winningTeam.setTextColor(getResources().getColor(R.color.radiant));
        }

        //matchDetailsCursor.close();
        return  rootView;
    }

    @Override
    public void onDetach() {
        if(mStatsCursor != null)
            mStatsCursor.close();
        super.onDetach();
    }
}
