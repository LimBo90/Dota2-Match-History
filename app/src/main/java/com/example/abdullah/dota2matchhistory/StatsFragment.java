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

import org.w3c.dom.Text;

public class StatsFragment extends Fragment {
    private static final String LOG_TAG = StatsFragment.class.getSimpleName();

    Cursor mMatchDetailsCurosr;

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
        View rootView = inflater.inflate(R.layout.fragment_stats, null);
        ViewHolder viewHolder = new ViewHolder(rootView);
        //binding match details from cursor
        mMatchDetailsCurosr.moveToFirst();

        viewHolder.matchIDView.setText(
               "" + mMatchDetailsCurosr.getLong(MatchDetailFragment.COL_MATCH_ID));

        int duration = mMatchDetailsCurosr.getInt(MatchDetailFragment.COL_DURATION);
        viewHolder.durationView.setText(Utility.getFormattedDuration(duration));

        viewHolder.gameModeView.setText(Utility.getGameMode(
                mMatchDetailsCurosr.getInt(MatchDetailFragment.COL_GAME_MODE)));

        long startTime = mMatchDetailsCurosr.getLong(MatchDetailFragment.COL_START_TIME);
        viewHolder.dateView.setText(Utility.getDateString(startTime));
        viewHolder.timeView.setText(Utility.getTimeString(startTime));

        viewHolder.lobbyTypeView.setText(Utility.getLobbyType(
                mMatchDetailsCurosr.getInt(MatchDetailFragment.COL_LOBBY_TYPE)));

        int radiantWin = mMatchDetailsCurosr.getInt(MatchDetailFragment.COL_RADIANT_WIN);

        if(radiantWin == 0) {
            viewHolder.winningTeam.setText(getString(R.string.dire_win));
            viewHolder.winningTeam.setTextColor(getResources().getColor(R.color.dire));
        }else{
            viewHolder.winningTeam.setText(getString(R.string.radiant_win));
            viewHolder.winningTeam.setTextColor(getResources().getColor(R.color.radiant));
        }

        return  rootView;
    }

    @Override
    public void onPause() {
        Log.v(LOG_TAG, "onPause");
        super.onPause();
        mMatchDetailsCurosr.close();
    }

    public void setMatchDetailsCursor(Cursor matchDetailsCursor) {
        mMatchDetailsCurosr = matchDetailsCursor;
    }
}
