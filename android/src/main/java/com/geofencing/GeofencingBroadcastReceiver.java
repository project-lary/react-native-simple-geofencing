package com.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

public class GeofencingBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GfBroadcastReceiver";
    public static final String ON_ENTER = "onEnter";
    public static final String ON_EXIT = "onExit";
    public static final String ON_DWELL = "onDwell";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Broadcasting geofence event");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            // Not user friendly message; todo: implement user friendly error message
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        Log.i(TAG, "Geofence event was detected: " + geofenceTransition);

        // Get the geofences that were triggered. A single event can trigger multiple geofences.
        List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
        ArrayList<String> ids = new ArrayList<>(triggeringGeofences.size());
        triggeringGeofences.forEach( gf -> {
            ids.add(gf.getRequestId());
        });

        String eventName = "";
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                eventName = ON_ENTER;
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                eventName = ON_EXIT;
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                eventName = ON_DWELL;
                break;
            default:
                Log.e(TAG, "unknown geofence transition");
        }

        sendEvent(context, eventName, ids);

    }

    private void sendEvent(Context context, String eventName, ArrayList<String> allTriggeredIds) {
        final Intent intent = new Intent();
        intent.putExtra("event", eventName);
        intent.putExtra("triggeredIds", allTriggeredIds);

        Bundle bundle = new Bundle();
        bundle.putString("event", eventName);
        bundle.putStringArrayList("triggeredIds", allTriggeredIds);

        Intent headlessGeofenceIntent = new Intent(context, GeofencingEventHeadlessTaskService.class);

        headlessGeofenceIntent.putExtras(bundle);
        context.startService(headlessGeofenceIntent);
        HeadlessJsTaskService.acquireWakeLockNow(context);
    }
}
