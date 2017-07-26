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
        LocationManager locationManager = new LocationManager(mContextx);
        Location location = locationManager.getLocation();
        return location;
    }



}
