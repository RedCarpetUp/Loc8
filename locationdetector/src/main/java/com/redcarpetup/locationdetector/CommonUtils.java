package com.redcarpetup.locationdetector;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by redcarpet on 7/27/17.
 */

public class CommonUtils {

    // Checking is Google play service available

    public static boolean idPlayServiceAvailable(Context context) {
        boolean idPlayService;
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (status == ConnectionResult.SUCCESS) {
            idPlayService = true;
        } else {
            idPlayService = false;
        }
        return idPlayService;
    }
}
