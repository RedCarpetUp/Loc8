package com.redcarpetup.locationdetector.ProviderUtils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.redcarpetup.locationdetector.Utils.PermissionUtils;

import java.util.Calendar;

/**
 * Created by redcarpet on 7/27/17.
 */

public class FusedLocationUtils implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final static String TAG = "FUSED LOCATION UTILS";
    public FusedCallback mCallback = null;
    protected LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private Location mCurrentLocation = null;
    private int PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    private boolean inProgress = false;
    private int numTries = 0;
    private long diffTime = 5000;
    private float minAccuracy = 35;
    private int maxTries = 1;
    private boolean lastKnownLocation = false;

    public FusedLocationUtils(Context c, FusedCallback callback) {
        this.mContext = c;
        this.mCallback = callback;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(PRIORITY);
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (canGetLocation())
            mGoogleApiClient.connect();

    }


    public void startLocationUpdates() {
        if (PermissionUtils.checkLocationPermission(mContext)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }

    }

    public void onLocationChanged(Location location) {
        numTries++;
        if (mCurrentLocation == null)
            mCurrentLocation = location;
        else if (mCurrentLocation.getAccuracy() > location.getAccuracy()) {
            mCurrentLocation = location;
        }
        if (numTries >= maxTries) {
            mCallback.onSuccess(mCurrentLocation);
        } else {
            chooseNetworkGps();
        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (lastKnownLocation) {
            if(PermissionUtils.checkLocationPermission(mContext)) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mCurrentLocation != null && (mCurrentLocation.getTime() - Calendar.getInstance().getTime().getTime()) < diffTime
                        && mCurrentLocation.getAccuracy() <= minAccuracy) {
                    mCallback.onSuccess(mCurrentLocation);
                } else {
                    startLocationUpdates();
                }
            }
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        mCallback.onFail("Connection failed");
        Log.i(TAG, "Connection failed:= " + result.getErrorCode());
    }

    public boolean canGetLocation() {
        return isNetworkEnabled() || isGPSEnabled();
    }

    public boolean isNetworkEnabled() {
        return ((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public boolean isGPSEnabled() {
        return ((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void chooseNetworkGps() {
        if (isGPSEnabled()) {
            PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
        } else if (isNetworkEnabled()) {
            PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        } else {
            PRIORITY = LocationRequest.PRIORITY_NO_POWER;
        }
    }

}