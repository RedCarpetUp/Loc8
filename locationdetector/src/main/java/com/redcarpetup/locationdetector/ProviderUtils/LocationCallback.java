package com.redcarpetup.locationdetector.ProviderUtils;

import android.location.Location;

/**
 * Created by redcarpet on 7/31/17.
 */

public interface LocationCallback {
     void onError(String error);
     void onSuccess(Location location);
}
