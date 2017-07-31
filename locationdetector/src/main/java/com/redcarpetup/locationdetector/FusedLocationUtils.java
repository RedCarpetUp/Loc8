package com.redcarpetup.locationdetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

/**
 * Created by redcarpet on 7/27/17.
 */

public class FusedLocationUtils implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final static String TAG = "FUSED LOCATION";
    public Callback mCallback = null;
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

    public FusedLocationUtils(Context c, Callback callback) {
        this.mContext = c;
        this.mCallback = callback;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }

    public void getCurrentLocation(int maxTries) {
        this.maxTries = maxTries;
        chooseNetworkGps();
        buildGoogleApiClient();
        lastKnownLocation = false;
        inProgress = true;
        if (canGetLocation())
            mGoogleApiClient.connect();
    }


    public void getLastKnownLocation(long diffTime, float minAccuracy) {
        this.diffTime = diffTime;
        this.minAccuracy = minAccuracy;
        chooseNetworkGps();
        buildGoogleApiClient();
        lastKnownLocation = true;
        inProgress = true;
        if (canGetLocation())
            mGoogleApiClient.connect();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(PRIORITY);
    }


    public void startLocationUpdates() {
        if (PermissionUtils.checkLocationPermission(mContext))
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
        else
        {
            Log.i(TAG, "No location permission");
        }

    }


    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        reset();
    }


    @Override
    public void onLocationChanged(Location location) {
        numTries++;
        if (mCurrentLocation == null)
            mCurrentLocation = location;
        else if (mCurrentLocation.getAccuracy() > location.getAccuracy()) {
            mCurrentLocation = location;
        }
        if (numTries >= maxTries) {
            mCallback.onLocationResult(mCurrentLocation);
            stopLocationUpdates();
        } else {
            chooseNetworkGps();
        }

    }

    public Location getLocation(int maxtries) {
        if (numTries >= maxtries)
            return mCurrentLocation;
        else
            return null;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (PermissionUtils.checkLocationPermission(mContext))
        {
            if (lastKnownLocation) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mCurrentLocation != null && (mCurrentLocation.getTime() - Calendar.getInstance().getTime().getTime()) < diffTime
                        && mCurrentLocation.getAccuracy() <= minAccuracy) {
                    mCallback.onLocationResult(mCurrentLocation);
                    reset();
                } else {
                    startLocationUpdates();
                }
            } else {
                startLocationUpdates();
            }
        }
        else
        {
            Log.i(TAG, "No location permission");

        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    public void showSettingsAlert() {
        if (!(mContext instanceof Activity)) {
            return; //only show dialog if called from activity.
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS settings");
        alertDialog.setMessage("GPS is not enabled. " +
                "This app uses GPS. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void reset() {
        numTries = 0;
        mCurrentLocation = null;
        inProgress = false;
        lastKnownLocation = false;
        mGoogleApiClient.disconnect();
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

    public boolean isInProgress() {
        return inProgress;
    }

    interface Callback {
        public void onLocationResult(Location location);
    }


}