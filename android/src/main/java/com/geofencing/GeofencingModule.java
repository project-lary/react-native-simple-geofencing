package com.geofencing;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;

import androidx.core.app.ActivityCompat;

public class GeofencingModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public static final String TAG = "GeofencingModule";
    public static final String ON_ENTER = "onEnter";
    public static final String ON_EXIT = "onExit";
    public static final String ON_DWELL = "onDwell";

    private GeofencingClient geofencingClient;
    private PendingIntent pendingIntent;

    public GeofencingModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.geofencingClient = LocationServices.getGeofencingClient(reactContext);
        this.reactContext = reactContext;
    }

    @ReactMethod
    public void addGeofences(ReadableArray geofences) {
        for (int i = 0; i < geofences.size(); i++) {
            addGeofence(geofences.getMap(i));
        }
    }

    @ReactMethod
    public void addGeofence(ReadableMap geofenceObject) {
        int permission = ActivityCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            permission = requestPermissions();
        }

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "PERM: Access fine location is not permitted.");
            return;
        }

        Geofence.Builder builder = new Geofence.Builder()
                .setRequestId(geofenceObject.getString("id"))
                .setCircularRegion(
                        geofenceObject.getDouble("lat"),
                        geofenceObject.getDouble("long"),
                        geofenceObject.getInt("radius")
                );

        long expirationDuration = geofenceObject.hasKey("expirationDuration") ?
                geofenceObject.getInt("expirationDuration") : Geofence.NEVER_EXPIRE;
        builder.setExpirationDuration(expirationDuration);

        int transitionTypes = getTransitions(geofenceObject.getArray("transitionTypes"));

        builder.setTransitionTypes(transitionTypes);

        if ((transitionTypes & Geofence.GEOFENCE_TRANSITION_DWELL) > 0) {
            Log.d(TAG, "setting loitering delay");
            builder.setLoiteringDelay(geofenceObject.getInt("loiteringDelay"));
        }

        Geofence gf = builder.build();

        int initialTriggers = 0;

        if (geofenceObject.hasKey("initialTriggers")) {
            initialTriggers = getTransitions(geofenceObject.getArray("initialTriggers"));
        }

        geofencingClient.addGeofences(getGeofencingRequest(gf, initialTriggers), getPendingIntent())
                .addOnSuccessListener(aVoid -> Log.i(TAG, "geofence successfully added"))
                .addOnFailureListener(e -> Log.e(TAG, "geofence could not be added: " + e.getMessage()));

    }

    private int requestPermissions() {
        ActivityCompat.requestPermissions(getReactApplicationContext().getCurrentActivity(),
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                }, 1);

        return ActivityCompat.checkSelfPermission(getReactApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private PendingIntent getPendingIntent() {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(reactContext, GeofencingBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(reactContext, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private int getTransitions(ReadableArray transitions) {
        int transitionTypes = 0;
        for (int i = 0; i < transitions.size() ; i++) {
            switch (transitions.getString(i)) {
                case ON_ENTER: {
                    transitionTypes |= Geofence.GEOFENCE_TRANSITION_ENTER;
                    break;
                }
                case ON_DWELL: {
                    transitionTypes |= Geofence.GEOFENCE_TRANSITION_DWELL;
                    break;
                }
                case ON_EXIT: {
                    transitionTypes |= Geofence.GEOFENCE_TRANSITION_EXIT;
                    break;
                }
                default: {
                    Log.e(TAG, "unknown transition type: " + transitions.getString(i));
                    break;
                }
            }
        }
        Log.d(TAG, "transition types: " + transitionTypes);
        return transitionTypes;
    }

    private GeofencingRequest getGeofencingRequest(Geofence gf, int initialTriggers) {
        return new GeofencingRequest.Builder()
                .setInitialTrigger(initialTriggers)
                .addGeofence(gf)
                .build();
    }


    @ReactMethod
    public void removeGeofence(String geofenceId) {
        geofencingClient.removeGeofences(Collections.singletonList(geofenceId));

    }

    @ReactMethod
    public void removeGeofences(ReadableArray geofenceIds) {
        ArrayList<String> ids = new ArrayList<>(geofenceIds.size());
        for (int i = 0; i < geofenceIds.size(); i++) {
            ids.add(geofenceIds.getString(i));
        }
        geofencingClient.removeGeofences(ids);
    }

    @ReactMethod
    public void removeAllGeofences(Callback onSuccess, Callback onError) {
        geofencingClient.removeGeofences(getPendingIntent())
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "removeAllGeofences: geofence successfully deleted");
                    onSuccess.invoke();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "removeAllGeofences: geofences could not be deleted: " + e.getMessage());
                    onError.invoke(e.getMessage());
                });
    }

    @Override
    public String getName() {
        return "Geofencing";
    }
}
