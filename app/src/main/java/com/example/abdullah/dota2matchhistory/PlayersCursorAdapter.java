package com.example.abdullah.dota2matchhistory;

import android.content.ContentResolver;
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


public class PlayersCursorAdapter extends CursorAdapter {
    private static final String LOG_TAG = MatchHistoryCursorAdapter.class.getSimpleName();

    private static final String itemsSelection = MatchesContract.ItemEntry.COLUMN_ITEM_ID + " = ? OR " +
            MatchesContract.ItemEntry.COLUMN_ITEM_ID + " = ? OR " +
            MatchesContract.ItemEntry.COLUMN_ITEM_ID + " = ? OR " +
            MatchesContract.ItemEntry.COLUMN_ITEM_ID + " = ? OR " +
            MatchesContract.ItemEntry.COLUMN_ITEM_ID + " = ? OR " +
            MatchesContract.ItemEntry.COLUMN_ITEM_ID + " = ? ";

    private static final String[] HERO_COLUMNS = {
            MatchesContract.HeroEntry.COLUMN_HERO_ID,
            MatchesContract.HeroEntry.COLUMN_NAME,
            MatchesContract.HeroEntry.COLUMN_LOCALIZED_NAME
    };

    private static final String[] ITEMS_COLUMNS = {
            MatchesContract.ItemEntry.COLUMN_ITEM_ID,
            MatchesContract.ItemEntry.COLUMN_NAME,
            MatchesContract.ItemEntry.COLUMN_LOCALIZED_NAME
    };

    // These indices are tied to HERO_COLUMNS.
    static final int COL_ID=0;
    static final int COL_NAME = 1;
    static final int COL_LOCALIZED_NAME = 2;

    static class ViewHolder{
        public final ImageView heroImageView;
        public final TextView heroNameTextView;
        public final TextView playerNameView;
        public final TextView levelView;
        public final TextView killsView;
        public final TextView deathsView;
        public final TextView assistsView;
        public final TextView goldView;
        public final TextView lastHitsView;
        public final TextView deniesView;
        public final TextView xpmView;
        public final TextView gpmView;
        public final ImageView[] itemImageViews;

        public ViewHolder(View view){
            heroImageView = (ImageView) view.findViewById(R.id.list_item_players_hero_image);
            heroNameTextView = (TextView) view.findViewById(R.id.list_item_players_hero_name);
            playerNameView = (TextView) view.findViewById(R.id.list_item_players_player_name);
            levelView = (TextView) view.findViewById(R.id.list_item_players_level);
            killsView = (TextView) view.findViewById(R.id.list_item_players_kills);
            deathsView = (TextView) view.findViewById(R.id.list_item_players_deaths);
            assistsView = (TextView) view.findViewById(R.id.list_item_players_assists);
            goldView = (TextView) view.findViewById(R.id.list_item_players_gold);
            lastHitsView = (TextView) view.findViewById(R.id.list_item_players_last_hits);
            deniesView = (TextView) view.findViewById(R.id.list_item_players_denies);
            xpmView = (TextView) view.findViewById(R.id.list_item_players_xpm);
            gpmView = (TextView) view.findViewById(R.id.list_item_players_gpm);
            ImageView item0View = (ImageView)view.findViewById(R.id.list_item_players_item_0);
            ImageView item1View = (ImageView)view.findViewById(R.id.list_item_players_item_1);
            ImageView item2View = (ImageView)view.findViewById(R.id.list_item_players_item_2);
            ImageView item3View = (ImageView)view.findViewById(R.id.list_item_players_item_3);
            ImageView item4View = (ImageView)view.findViewById(R.id.list_item_players_item_4);
            ImageView item5View = (ImageView)view.findViewById(R.id.list_item_players_item_5);
            itemImageViews = new ImageView[] {item0View, item1View, item2View, item3View, item4View, item5View};
        }
    }

    public PlayersCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_players, parent, false);
        if(view == null){
            Log.v(LOG_TAG, "view = null");
        }
        view.setTag(new ViewHolder(view));
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        int heroID = cursor.getInt(PlayersListFragment.COL_PLAER_HERO_ID);
        String heroName;
        String heroLocalizedName;


        Cursor hero = context.getContentResolver().query(MatchesContract.HeroEntry.buildHeroUriWithHeroID(heroID),
                HERO_COLUMNS,
                null,
                null,
                null);

        if(!hero.moveToFirst()){
            Log.e(LOG_TAG, "empty cursor returned from database Hero query");
        }

        heroName = hero.getString(COL_NAME);
        heroLocalizedName = hero.getString(COL_LOCALIZED_NAME);
        hero.close();
        //setting hero name and image
        int heroImageResId = context.getResources().getIdentifier(heroName, "drawable", context.getPackageName());
        if(viewHolder.heroImageView == null){
            Log.v(LOG_TAG, "heroImageView = null");
        }
        viewHolder.heroImageView.setImageResource(heroImageResId);
        viewHolder.heroNameTextView.setText(heroLocalizedName);

        viewHolder.playerNameView.setText("" + cursor.getString(PlayersListFragment.COL_PLAER_NAME));
        viewHolder.levelView.setText("" + cursor.getInt(PlayersListFragment.COL_PLAYER_LEVEL));
        viewHolder.killsView.setText("" + cursor.getInt(PlayersListFragment.COL_PLAER_KILLS));
        viewHolder.deathsView.setText("" + cursor.getInt(PlayersListFragment.COL_PLAER_DEATHS));
        viewHolder.assistsView.setText("" + cursor.getInt(PlayersListFragment.COL_PLAER_ASSISTS));
        viewHolder.goldView.setText("" + cursor.getInt(PlayersListFragment.COL_PLAER_GOLD));
        viewHolder.lastHitsView.setText("" + cursor.getInt(PlayersListFragment.COL_PLAER_LAST_HITS));
        viewHolder.deniesView.setText("" + cursor.getInt(PlayersListFragment.COL_PLAYER_DENIES));
        viewHolder.xpmView.setText("" + cursor.getInt(PlayersListFragment.COL_PLAER_XPM));
        viewHolder.gpmView.setText("" + cursor.getInt(PlayersListFragment.COL_PLAER_GPM));

        int item0 = cursor.getInt(PlayersListFragment.COL_PLAYER_ITEM_0);
        int item1 = cursor.getInt(PlayersListFragment.COL_PLAYER_ITEM_1);
        int item2 = cursor.getInt(PlayersListFragment.COL_PLAYER_ITEM_2);
        int item3 = cursor.getInt(PlayersListFragment.COL_PLAYER_ITEM_3);
        int item4 = cursor.getInt(PlayersListFragment.COL_PLAYER_ITEM_4);
        int item5 = cursor.getInt(PlayersListFragment.COL_PLAYER_ITEM_5);
        int[] playerItems = new int[]{item0, item1, item2, item3, item4, item5};
        String[] itemsSelectionArgs = new String[]{String.valueOf(item0),
                String.valueOf(item1),
                String.valueOf(item2),
                String.valueOf(item3),
                String.valueOf(item4),
                String.valueOf(item5)};
        ImageView[] itemsImageView = viewHolder.itemImageViews;
        /*
        Cursor items = context.getContentResolver().query(
                MatchesContract.ItemEntry.CONTENT_URI,
                ITEMS_COLUMNS,
                itemsSelection,
                itemsSelectionArgs,
                null,
                null);
        if(!items.moveToFirst())
            Log.e(LOG_TAG, "empty cursor returned from items query");


        for(int i=0; i<itemsImageView.length; i++){
            items.moveToFirst();
                if(items.getInt(COL_ID) == playerItems[i]){
                    int itemResId = context.getResources().getIdentifier(items.getString(COL_NAME), "drawable", context.getPackageName());
                    itemsImageView[i].setImageResource(itemResId);
                }

            }while(items.moveToNext());
            items.close();
    */
        ContentResolver contentResolver = context.getContentResolver();

        for(int i=0 ; i<itemsImageView.length; i++){
            if(playerItems[i] == 0){
                itemsImageView[i].setVisibility(View.INVISIBLE);
                continue;
            }

            Cursor itemCursor = contentResolver.query(
                    MatchesContract.ItemEntry.buildItemUriWithItemID(playerItems[i]),
                    ITEMS_COLUMNS,
                    null,
                    null,
                    null
            );
            if(!itemCursor.moveToFirst()){
                Log.e(LOG_TAG, "empty item cursor returned from database");
            }
            String itemName = itemCursor.getString(COL_NAME);
            int itemImageResId = context.getResources().getIdentifier(itemName, "drawable", context.getPackageName());
            viewHolder.itemImageViews[i].setImageResource(itemImageResId);
            itemCursor.close();
        }





    }
}
