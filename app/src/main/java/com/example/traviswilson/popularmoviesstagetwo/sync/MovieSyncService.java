package com.example.traviswilson.popularmoviesstagetwo.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by traviswilson on 12/13/16.
 */
public class MovieSyncService extends Service {
    private static final Object syncAdapterLock = new Object(); //used to make the creation of a syncadaper
    //thread safe, as lots of syncs may stack up leading to issues with multithreading
    private static MovieSyncAdapter movieSyncAdapter = null;

    @Override
    public void onCreate(){
        synchronized (syncAdapterLock){
            if(movieSyncAdapter == null){
                movieSyncAdapter = new MovieSyncAdapter(getApplicationContext(),true );
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent){
        return movieSyncAdapter.getSyncAdapterBinder();
    }
}
