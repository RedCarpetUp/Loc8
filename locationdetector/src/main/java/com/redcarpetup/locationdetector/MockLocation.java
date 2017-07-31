package com.redcarpetup.locationdetector;

import android.content.Context;
import android.location.Location;

/**
 * Created by redcarpet on 7/24/17.
 */

public class MockLocation {
    private Context mContextx;

    MockLocation(Context context)
    {
     this.mContextx = context;
    }








    public Location getLocation()
    {
        LocationManagerUtils locationManager = new LocationManagerUtils(mContextx);
        Location location = locationManager.getLocation();
        return location;
    }

    public double getLatitude()
    {
        LocationManagerUtils locationManager = new LocationManagerUtils(mContextx);
        Location location = locationManager.getLocation();
        return location.getLatitude();
    }
    public double getLongitude()
    {
        LocationManagerUtils locationManager = new LocationManagerUtils(mContextx);
        Location location = locationManager.getLocation();
        return location.getLongitude();
    }

    public boolean isMockLocation()
    {
        LocationManagerUtils locationManager = new LocationManagerUtils(mContextx);
        Location location = locationManager.getLocation();
        return MockLocationUtils.isLocationFromMockProvider(mContextx,location);
    }



}
