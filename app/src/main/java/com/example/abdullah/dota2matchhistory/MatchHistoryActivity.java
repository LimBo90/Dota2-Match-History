package com.example.abdullah.dota2matchhistory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.example.abdullah.dota2matchhistory.sync.MatchHistorySyncAdapter;


public class MatchHistoryActivity extends AppCompatActivity
        implements MatchHistoryFragment.OnMatchSelectedListener {
    private static final String LOG_TAG = MatchHistoryActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history);
            MatchHistorySyncAdapter.syncImmediately(this);

        //sets the activity toolbar
        Toolbar toolBar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);

        if (findViewById(R.id.match_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.match_detail_container, new MatchDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;
        }

        MatchHistoryFragment ff = (MatchHistoryFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_match_history);
        ff.setTwoPane(mTwoPane);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_match_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_log_out) {
            Utility.removeUser(this);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            //Disables the menu button
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onMatchSelected(Uri matchUriWithMatchId) {
        Log.v(LOG_TAG, "onMatchSelected");
        if(mTwoPane){
            MatchDetailFragment detailFragment = new MatchDetailFragment();
            Bundle args = new Bundle();
            args.putParcelable(MatchDetailFragment.MATCH_DETAIL_URI, matchUriWithMatchId);
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.match_detail_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, MatchDetailActivity.class)
                    .setData(matchUriWithMatchId);
            startActivity(intent);
        }
    }
}
