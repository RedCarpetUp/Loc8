package com.redcarpetup.locationdetector;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.util.Log;

import com.redcarpetup.locationdetector.Callbacks.LocationCallback;
import com.redcarpetup.locationdetector.ProviderUtils.FusedLocationUtils;
import com.redcarpetup.locationdetector.ProviderUtils.LocationManagerUtils;
import com.redcarpetup.locationdetector.Utils.CommonUtils;
import com.redcarpetup.locationdetector.Utils.Constants;
import com.redcarpetup.locationdetector.Utils.PermissionUtils;
import com.redcarpetup.locationdetector.Utils.PrefManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

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
                        Location defaultLocation = getDefaultLocation();
                        locationCallback.onSuccess(defaultLocation);
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

    private Location getDefaultLocation()
    {
        if (CommonUtils.isPlayServiceAvailable(mContext))
        {
            FusedLocationUtils locationUtils = new FusedLocationUtils(mContext, new FusedLocationUtils.Callback() {
                @Override
                public void onLocationResult(Location location) {
                    fusedLocation = location;
                }
            });
        }
        else
        {
            LocationManagerUtils utils = new LocationManagerUtils(mContext);
            Location location = utils.getLocation();
            return location;
        }
        return fusedLocation;
    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception ", e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }

    public static boolean isLocationFromMockProvider(Context context, Location location) {
        boolean isMock = false;
        if (location != null) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= 18) {
                    isMock = location.isFromMockProvider();
                } else {
                    if (Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
                        return false;
                    else {
                        return true;
                    }
                }
            } catch (Exception e) {
            }
        }
        return isMock;
    }

}
