package com.example.abdullah.dota2matchhistory;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * This activity is empty with no UI, it checks if there's a user currently logged in if so it
 * launches the matches history directly. If no user is currently logged in it launcehes the LoginActivity.
 *
 */

public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utility.isUserLoggedIn(this)){
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, Utility.getLoggedUserID(this));
            startActivity(intent);
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
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
