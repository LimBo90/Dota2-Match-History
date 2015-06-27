package com.example.abdullah.dota2matchhistory.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class MatchHistoryAuthenticatorService extends Service{
// Instance field that stores the authenticator object
private MatchHistoryAuthenticator mAuthenticator;

        @Override
        public void onCreate() {
            // Create a new authenticator object
            mAuthenticator = new MatchHistoryAuthenticator(this);
        }

        /*
         * When the system binds to this Service to make the RPC call
         * return the authenticator's IBinder.
         */
        @Override
        public IBinder onBind(Intent intent) {
            return mAuthenticator.getIBinder();
        }
}
