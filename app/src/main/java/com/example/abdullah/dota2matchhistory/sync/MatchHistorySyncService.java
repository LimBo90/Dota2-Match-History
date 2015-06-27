package com.example.abdullah.dota2matchhistory.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MatchHistorySyncService extends Service{
private static final Object sSyncAdapterLock = new Object();
private static MatchHistorySyncAdapter sMatchHistorySyncAdapter = null;

        @Override
        public void onCreate() {
            Log.d("MatchHistorySyncService", "onCreate - MatchHistorySyncService");
            synchronized (sSyncAdapterLock) {
                if (sMatchHistorySyncAdapter == null) {
                    sMatchHistorySyncAdapter = new MatchHistorySyncAdapter(getApplicationContext(), true);
                }
            }
        }

        @Override
        public IBinder onBind(Intent intent) {
            return sMatchHistorySyncAdapter.getSyncAdapterBinder();
        }
}
