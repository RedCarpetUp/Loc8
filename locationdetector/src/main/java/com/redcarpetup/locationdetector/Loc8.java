package com.redcarpetup.locationdetector;

import android.content.Context;
import android.location.Location;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by redcarpet on 7/31/17.
 */

public class Loc8 {

    public Context mContext;
    public static Loc8 loc8;
    public LocationCallback locationCallback;
    public Location fusedLocation = null;
    public static final int DEFAULT = 0;
    public static final int LOCATION_MANAGER = 1;
    public static final int LOCATION_API = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            DEFAULT,
            LOCATION_MANAGER,
            LOCATION_API,
    })
    public @interface ProviderType {
    }


    public Loc8(Context context) {
        mContext = context;
    }

    public static Loc8 getInstance(Context context, @ProviderType int dara) {
        if (loc8 == null) {
            loc8 = new Loc8(context);
        }
        PrefManager.setIntegerPreference(context, Constants.ProviderType, dara);
        return loc8;
    }

    public void getLocation(LocationCallback locationCallback) {
        this.locationCallback = locationCallback;
        if (PermissionUtils.checkLocationPermission(mContext)) {
            if (CommonUtils.isLocationEnabled(mContext)) {
                switch (PrefManager.getIntegerPreference(mContext, Constants.ProviderType, 0)) {
                    case 0:
                        break;
                    case 1:
                        Location providerLocation = getLocationFromProvider();
                        locationCallback.onSuccess(providerLocation);
                        break;
                    case 2:
                        Location fusedLocation = getLocationFromLocationApi();
                        locationCallback.onSuccess(fusedLocation);
                        break;
                }
            } else {
                locationCallback.onError(Constants.LOCATION_NOT_ENABLED);
            }

        } else {
            locationCallback.onError(Constants.NO_LOCATION_PEMISSION);
        }

    }

    private Location getLocationFromProvider() {
        LocationManagerUtils utils = new LocationManagerUtils(mContext);
        Location location = utils.getLocation();
        return location;
    }

    private Location getLocationFromLocationApi() {
        FusedLocationUtils locationUtils = new FusedLocationUtils(mContext, new FusedLocationUtils.Callback() {
            @Override
            public void onLocationResult(Location location) {
                fusedLocation = location;
            }
        });
        return fusedLocation;
    }


}
