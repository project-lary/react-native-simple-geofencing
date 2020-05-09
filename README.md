# react-native-geofencing

## Getting started

`$ npm install react-native-geofencing --save`

### Mostly automatic installation

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
}
// adding a geofence
Geofencing.addGeofence(geofenceObject);

// adding multiple geofences at once
Geofencing.addGeofences([//array of geofence objects])

```
