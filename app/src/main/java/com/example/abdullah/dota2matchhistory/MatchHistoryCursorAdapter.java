package com.example.abdullah.dota2matchhistory;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abdullah.dota2matchhistory.Data.MatchesContract;

public class MatchHistoryCursorAdapter extends CursorAdapter {

    private static final String LOG_TAG = MatchHistoryCursorAdapter.class.getSimpleName();

    private static final String[] HERO_COLUMNS = {
            MatchesContract.HeroEntry.COLUMN_HERO_ID,
            MatchesContract.HeroEntry.COLUMN_NAME,
            MatchesContract.HeroEntry.COLUMN_LOCALIZED_NAME
    };

    // These indices are tied to HERO_COLUMNS.
    static final int COL_HERO_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_LOCALIZED_NAME = 2;

    static class ViewHolder{
        public final ImageView heroImageView;
        public final TextView heroTextView;
        public final TextView lobbyTypeView;
        public final TextView DateView;
        public final TextView TimeView;

        public ViewHolder(View view){
            heroImageView = (ImageView)view.findViewById(R.id.match_list_hero_image_view);
            heroTextView = (TextView)view.findViewById(R.id.match_list_hero_name_textview);
            lobbyTypeView = (TextView)view.findViewById(R.id.match_list_lobby_type_textview);
            DateView = (TextView)view.findViewById(R.id.match_list_date_textview);
            TimeView = (TextView)view.findViewById(R.id.match_list_time_textview);
        }
    }

    public MatchHistoryCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_match, parent, false);
        view.setTag(new ViewHolder(view));
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        int userHeroID = cursor.getInt(MatchHistoryFragment.COL_USER_HERO);
        String heroName;
        String heroLocalizedName;


        Cursor hero = context.getContentResolver().query(MatchesContract.HeroEntry.buildHeroUriWithHeroID(userHeroID),
                HERO_COLUMNS,
                null,
                null,
                null);

        if(!hero.moveToFirst()){
            Log.e(LOG_TAG, "empty cursor returned from database Hero query");
        }


        heroName = hero.getString(COL_NAME);

        int heroImageResId = context.getResources().getIdentifier(heroName, null, "drawable");
        viewHolder.heroImageView.setImageResource(heroImageResId);

        heroLocalizedName = hero.getString(COL_LOCALIZED_NAME);
        viewHolder.heroTextView.setText(heroLocalizedName);

        long startTime = cursor.getLong(MatchHistoryFragment.COL_START_TIME);

        int lobbyType = cursor.getInt(MatchHistoryFragment.COL_LOBBY_TYPE);
        viewHolder.lobbyTypeView.setText(Utility.getLobbyType(lobbyType));

        Log.v(LOG_TAG, "heroName = " + heroName);
        Log.v(LOG_TAG, "heroLocaliedName = " + heroLocalizedName);


    }
}
