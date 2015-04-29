package com.example.abdullah.dota2matchhistory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends ActionBarActivity {
    EditText meditText;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        Button login = (Button) findViewById(R.id.login_button);
        meditText = (EditText) findViewById(R.id.login_edit_text);

        login.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = meditText.getText().toString();
                if(!username.equals("")) {
                    FetchSteamID fetchSteamID = new FetchSteamID(mContext);
                    fetchSteamID.execute(username);
                }else {
                    Toast toast = Toast.makeText(mContext,
                            "Please enter your steam username",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public  class FetchSteamID extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = FetchSteamID.class.getSimpleName();
        private final Context mContext;
        private ProgressDialog progress;

        public FetchSteamID(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            final String OWM_STEAM_ID = "steamid";

            String username = params[0];
            String userInfoJsonStr = getUserInfoJsonStr(username);
            String steamID = null;
            try {
                // Extracting steam ID from json
                JSONObject userInfoJson = new JSONObject(userInfoJsonStr);
                steamID = userInfoJson.getString(OWM_STEAM_ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return steamID;
        }


        @Override
        protected void onPostExecute(String steamID) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
            if(steamID != null){
                Intent intent = new Intent(mContext, UserMatchHistoryActivity.class);
                intent.putExtra(intent.EXTRA_TEXT, steamID);
                startActivity(intent);
//                finish();
            }

        }


        private String getUserInfoJsonStr(String username) {
            HttpURLConnection urlConnection = null;
            String UserInfoJsonStr = null;

            try {
                // Constructs the url for getting user steamID
                // https://steamcommunity.com/login/getrsakey?username=<username>
                final String USER_INFO_URL =
                        "https://steamcommunity.com/login/getrsakey?";
                final String QUERY_PARAM = "username";

                Uri builtUri = Uri.parse(USER_INFO_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, username)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                UserInfoJsonStr = inputStreamToJsonStr(inputStream);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return UserInfoJsonStr;
        }


        private String inputStreamToJsonStr(InputStream inputStream) {
            BufferedReader reader = null;
            StringBuffer buffer = new StringBuffer();
            String result;
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
                return buffer.toString();
            }
        }
    }
}
