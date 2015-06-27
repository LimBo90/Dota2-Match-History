package com.example.abdullah.dota2matchhistory;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * This activity launches the MatchHistory activity directly if there's already user logged in, else
 * it views a button for login.
 *
 */

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_CODE = 12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FetchMatches fetMatchesTask = new FetchMatches();

        if(Utility.isUserLoggedIn(this)){
            Intent intent = new Intent(this, MatchHistoryActivity.class);
            startActivity(intent);
            finish();
        }else{
            setContentView(R.layout.activity_main);
            //sets the activity toolbar
            Toolbar toolBar = (Toolbar)findViewById(R.id.tool_bar);
            setSupportActionBar(toolBar);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            // Destroys this activity when login is successful removing it from the backstack to prevent user from
            // getting back to this screen after logging in.
            finish();
            Log.v(LOG_TAG, "MainActivity finished");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    /**
     * The function that's called when the user clicks the login button. It launches LoginActivity.
     * @param v The login button the user pressed (not used)
     */
    public void startLogin(View v){
        startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE);
    }
}
