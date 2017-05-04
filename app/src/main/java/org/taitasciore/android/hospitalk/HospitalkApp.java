package org.taitasciore.android.hospitalk;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.common.api.GoogleApiClient;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by roberto on 18/04/17.
 */

public class HospitalkApp extends Application {

    private GoogleApiClient googleApiClient;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("OpenSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }
}
