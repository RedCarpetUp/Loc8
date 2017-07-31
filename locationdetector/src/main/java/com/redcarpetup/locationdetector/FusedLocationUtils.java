package com.redcarpetup.locationdetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
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
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(PRIORITY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    public void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    public void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        reset();
    }

    /**
     * Callback that fires when the location changes.
     */
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
            //Toast.makeText(mContext, "latitude: "+mCurrentLocation.getLatitude()+" Longitude: "+mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        }

    }

    public Location getLocation(int maxtries) {
        if (numTries >= maxtries)
            return mCurrentLocation;
        else
            return null;
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastKnownLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
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

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        if(!(mContext instanceof Activity)){
            return; //only show dialog if called from activity.
        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. " +
                "This app uses GPS. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
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