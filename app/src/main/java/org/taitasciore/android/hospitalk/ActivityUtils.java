package org.taitasciore.android.hospitalk;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

/**
 * Created by roberto on 18/04/17.
 */

public final class ActivityUtils {

    public static final int REQUEST_LOCATION = 001;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 002;

    private static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private static void requestPermission(Activity context, String permission, int requestCode) {
        ActivityCompat.requestPermissions(context,
                new String[]{permission},
                requestCode);
    }

    public static boolean isLocationPermissionGranted(Context context) {
        return isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static void requestLocationPermission(Activity context) {
        requestPermission(context, Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_LOCATION);
    }

    public static boolean isWriteExternalStoragePermissionGranted(Activity context) {
        return isPermissionGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static void requestWriteExternalStoragePermission(Activity context) {
        requestPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    public static boolean isGoogleApiAvailable(Activity context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        return apiAvailability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    public static ProgressDialog showProgressDialog(Activity context) {
        return ProgressDialog.show(context, "", "Espere un momento...", true, false);
    }
}
