import { AppRegistry, NativeEventEmitter, NativeModules } from 'react-native';

const { Geofencing } = NativeModules;

const geofenceEventEmitter = new NativeEventEmitter(Geofencing);

const HeadlessGeofenceEventTask = async (event) => {
  console.log(event);
  geofenceEventEmitter.emit(event.event, event.triggeredIds);
};

AppRegistry.registerHeadlessTask('GeofenceEvent', () => HeadlessGeofenceEventTask);

export const Events = {
  EXIT: 'onExit',
  ENTER: 'onEnter',
  DWELL: 'onDwell',
};

export default {
  onEvent: (event, callback) => {
    if (typeof callback !== 'function') {
      console.error('callback function must be provided');
    }
    if (!Object.values(Events).find(e => e === event)) {
      console.error('invalid event');
    }
    return geofenceEventEmitter.addListener(event, callback);
  },

  addGeofence: (geofenceObject) => {
    Geofencing.addGeofence(geofenceObject);
  },

  addGeofences: (geofenceObjects) => {
    Geofencing.addGeofences(geofenceObjects);
  },

  removeGeofence: (id) => {
    Geofencing.removeGeofence(id);
  },

  removeGeofences: (ids) => {
    Geofencing.removeGeofences(ids);
  },

  removeAllGeofences: () => {
    return new Promise((resolve, reject) => {
        Geofencing.removeAllGeofences(resolve, reject);
    });
  },
};

export const GeofenceKeys = {
  ID: 'id',
  LATITUDE: 'lat',
  LONGITUDE: 'long',
  RADIUS: 'radius',
  TRANSITION_TYPES: 'transitionTypes',
  EXPIRATION_DURATION_MILLIS: 'expirationDuration',
  LOITERING_DELAY_MS: 'loiteringDelay',
  INITIAL_TRIGGERS: 'initialTriggers',
};