# react-native-geofencing
Mirrors the Google Geofencing API for react-native use.
https://developer.android.com/training/location/geofencing.html#RequestGeofences

## Only implemented and tested for ANDROID right now

## Getting started

`$ npm install react-native-geofencing --save`
or
`$ yarn add react-native-geofencing`

### for react-native < v60.0.0

`$ react-native link react-native-geofencing`

## Usage
```javascript
import Geofencing, { Events, GeofenceKeys } from 'react-native-geofencing';

// GeofenceObject
const geofenceObject = {
  id: "id", // string
  lat: 47.123, // double
  long: 11.1234, // double
  radius: 200, // int, unity: meters
  transitionTypes: [Events.Enter, Events.Dwell], // array of events, what kind of transitions to trigger
  loiteringDelay: 20000, // only required when having Events.Dwell in transitionTypes, time in MS until dwell event should      trigger
  initialTriggers: [Events.Enter, Events.Exit], // array of events, triggers when starting inside/outside of a geofence
  expirationDuration: 100000, // optional: expiration duration of geofence in MS
};
// adding a geofence for monitoring
Geofencing.addGeofence(geofenceObject);

// adding multiple geofences at once
Geofencing.addGeofences([array of geofence objects]);

// remove geofence from monitoring
Geofencing.removeGeofence(geofenceId);

// remove multiple geofences at once
Geofencing.removeGeofeces([array of geofence ids]);

// remove all geofences
Geofencing.removeAllGeofences(); // return a Promise

// add event listener for executing a callback when a certain event is triggered
// a single event can trigger multiple geofences
Geofencing.addListener(Events.Enter, (ids) => {
  console.log("Entering event has been triggered by geofences with following: ", ids);
  // do whatever you want to do when receiving the triggered geofence ids
});

// remove event listener for Dwell with callback function eventDwell
Geofencing.removeListener(Events.Dwell, eventDwell);

```
