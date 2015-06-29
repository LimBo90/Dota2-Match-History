package com.example.abdullah.dota2matchhistory;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.abdullah.dota2matchhistory.sync.MatchHistorySyncAdapter;

/**
 * This activity logs the user into steam and fetches his steamID.
 */
public class LoginActivity extends AppCompatActivity {
    private final String LOG_TAG = LoginActivity.class.getSimpleName();

    private WebView mWebView;
    private LinearLayout mProgressBarContainer;

    private BroadcastReceiver mOnSyncFinishedListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startMatchHistoryActivity();
        }
    };

    private BroadcastReceiver mOnConnectionLostReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!Utility.isNetworkAvailable(context)){
                Toast toast = Toast.makeText(context, "connection lost", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    };

    private void startMatchHistoryActivity() {
        if(Utility.isNetworkAvailable(this)) {
            // Starts MatchhistoryActivity and finish this activity to remove it from backstack.
            Intent intent = new Intent(this, MatchHistoryActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, Utility.getLoggedUserID(this));
            setResult(RESULT_OK);   // To notify main activity that the login succeeded allowing it to finish.
            startActivity(intent);
            finish();
        }
    }



    // Constants for constructing openid url request
    private static final String REALM_PARAM = "dota.match.history";
    private static final String url = "https://steamcommunity.com/openid/login?" +
            "openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&" +
            "openid.identity=http://specs.openid.net/auth/2.0/identifier_select&" +
            "openid.mode=checkid_setup&" +
            "openid.ns=http://specs.openid.net/auth/2.0&" +
            "openid.realm=https://" + REALM_PARAM + "&" +
            "openid.return_to=https://" + REALM_PARAM + "/signin/";


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //to prevent going back to the MainActivity when the user press the back button inside the webView
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            // go back in only the web view
            mWebView.goBack();
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_MENU){
            //Disables the menu button
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mOnSyncFinishedListener,
                new IntentFilter(MatchHistorySyncAdapter.SYNC_FINISHED));

        this.registerReceiver(mOnConnectionLostReciever,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //sets the activity toolbar
        Toolbar toolBar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolBar);

        mProgressBarContainer = (LinearLayout)findViewById(R.id.web_view_progress_bar);
        mWebView = (WebView)findViewById(R.id.web_view);
        final Activity activity = this;

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBarContainer.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageStarted(WebView view, String url,
                                      Bitmap favicon) {
                mProgressBarContainer.setVisibility(View.VISIBLE);
                mWebView.setVisibility(View.GONE);
                setTitle(url);
                Uri Url = Uri.parse(url);

                if (Url.getAuthority().equals(REALM_PARAM.toLowerCase())) {
                    // That means that authentication is finished and the url contains user's id.
                    mWebView.stopLoading();

                    // Extracts user id.
                    Uri userAccountUrl = Uri.parse(Url.getQueryParameter("openid.identity"));
                    long userId = Long.valueOf(userAccountUrl.getLastPathSegment());

                    //add the current user to the shared preferences
                    Utility.addUser(activity, userId);
                    syncNow();
                }
            }
        });

        mWebView.setBackgroundColor(getResources().getColor(R.color.grey_900));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);

    }

    private void syncNow() {
        setContentView(R.layout.activity_login);
        mProgressBarContainer.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
        Log.v(LOG_TAG, "initalize sync adapter");
        MatchHistorySyncAdapter.initializeSyncAdapter(this);
        MatchHistorySyncAdapter.syncAllImmediately(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

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
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mOnSyncFinishedListener);
        this.unregisterReceiver(mOnConnectionLostReciever);
        super.onPause();
    }
}

