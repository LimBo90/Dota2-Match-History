package com.example.abdullah.dota2matchhistory;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

/**
 * This activity logs the user into steam and fetches his steamID.
 */
public class LoginActivity extends ActionBarActivity {

    private final String LOG_TAG = LoginActivity.class.getSimpleName();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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


    /**
     * The function that's called when the user clicks the login button. It launches a webview with
     * steam's openID login url. After successful login it saves the user's steamID and launches HomeActivity.
     * @param v The login button the user pressed (not used)
     */
    public void startLogin(View v){

        final WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);

        final Activity activity = this;

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                activity.setProgress(progress * 1000);
            }
        });

        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageStarted(WebView view, String url,
                                      Bitmap favicon) {
                setTitle(url);
                Uri Url = Uri.parse(url);

                if(Url.getAuthority().equals(REALM_PARAM.toLowerCase())){
                    // That means that authentication is finished and the url contains user's id.
                    webView.stopLoading();

                    // Extracts user id.
                    Uri userAccountUrl = Uri.parse(Url.getQueryParameter("openid.identity"));
                    String userId = userAccountUrl.getLastPathSegment();

                    // Starts HomeActivity and finishes LoginActivity.
                    Intent intent = new Intent(activity, HomeActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT, userId);

                    Utility.addUser(activity, userId);
                    startActivity(intent);
                    finish();
                }
            }

        });
        setContentView(webView);
        webView.loadUrl(url);
    }

 }

