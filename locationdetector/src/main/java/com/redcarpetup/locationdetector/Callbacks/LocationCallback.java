package com.redcarpetup.locationdetector.Callbacks;

import android.location.Location;

/**
 * Created by redcarpet on 7/31/17.
 */

public interface LocationCallback {
    public void onError(String error);
    public void onSuccess(Location location);
}
