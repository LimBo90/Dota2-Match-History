package com.example.abdullah.dota2matchhistory;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

/**
 * This activity logs the user into steam and fetches his steamID.
 */
public class LoginActivity extends AppCompatActivity {
    private final String LOG_TAG = LoginActivity.class.getSimpleName();

    private WebView mWebView;
    private LinearLayout mProgressBarContainer ;

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

            mWebView.goBack(); // go back in only the web view
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
                    String userId = userAccountUrl.getLastPathSegment();

                    //add the current user to the shared preferences
                    Utility.addUser(activity, userId);

                    // Starts MatchhistoryActivity and finish this activity to remove it from backstack.
                    Intent intent = new Intent(activity, MatchHistoryActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT, userId);
                    setResult(RESULT_OK);   // To notify main activity that the login succeeded allowing it to finish.
                    startActivity(intent);
                    finish();
                }
            }
        });

        mWebView.setBackgroundColor(getResources().getColor(R.color.grey_900));
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
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




 }

