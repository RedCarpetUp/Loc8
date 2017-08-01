package com.redcarpetup.locationdetector.ProviderUtils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * Created by redcarpet on 8/1/17.
 */

public class FusedLocationUti implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private Context mContext;
    private FusedCallback callback;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    public FusedLocationUti(Context context, FusedCallback fusedCallback) {
        this.mContext = context;
        this.callback = fusedCallback;
        connectApiClient();


        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void connectApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation != null) {
            callback.onSuccess(mCurrentLocation);
            stopLocationUpdates();
        } else {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Firing onConnectionSuspended..............................................");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Firing onConnectionFailed..............................................");

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (mCurrentLocation != null) {
            callback.onSuccess(mCurrentLocation);
            startLocationUpdates();
        } else {
            startLocationUpdates();
        }
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        reset();
    }

    public void reset() {
        mCurrentLocation = null;
        mGoogleApiClient.disconnect();
    }
}
