package com.redcarpetup.locationdetector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by redcarpet on 7/27/17.
 */

public class FusedLocationUtils extends Service imGooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
