package com.geofencing;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

import androidx.annotation.Nullable;

public class GeofencingEventHeadlessTaskService extends HeadlessJsTaskService {
    @Nullable
    protected HeadlessJsTaskConfig getTaskConfig(Intent intent) {
        System.out.println("task config");
        Bundle extras = intent.getExtras();
        System.out.println(extras.toString());
        return new HeadlessJsTaskConfig(
                "GeofenceEvent",
                extras != null ? Arguments.fromBundle(extras) : null,
                5000,
                true);
    }
}
