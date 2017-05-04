package org.taitasciore.android.hospitalk;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by roberto on 29/04/17.
 */

public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final float ZOOM = 15f;

    private double lat;
    private double lon;

    public static MapFragment newInstance(double lat, double lon) {
        MapFragment f = new MapFragment();
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lon", lon);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        lat = getArguments().getDouble("lat");
        lon = getArguments().getDouble("lon");
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("debug", "map ready");
        LatLng coords = new LatLng(lat, lon);
        MarkerOptions marker = new MarkerOptions().position(coords);
        googleMap.addMarker(marker);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coords, ZOOM);
        googleMap.moveCamera(cameraUpdate);
    }
}
