package com.example.traviswilson.popularmoviesstagetwo.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by traviswilson on 12/13/16.
 */
public class MovieAuthenticatorService extends Service {
    private MovieAuthenticator movieAuthenticator;

    @Override
    public void onCreate() {
        //create a new Authenticator object
        movieAuthenticator = new MovieAuthenticator(this);
    }
    @Override
    public IBinder onBind(Intent intent){
        return movieAuthenticator.getIBinder();
    }
}
