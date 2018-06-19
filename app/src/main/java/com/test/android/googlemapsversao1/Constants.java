package com.test.android.googlemapsversao1;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

final class Constants {

    private Constants() {

    }

    private static final String PACKAGE_NAME =  "com.google.android.gms.location.Geofence";

    /*Usado para definir um tempo de experiracao para uma GEOFENCE.
    * Apos esse periodo, os Serviços de localizacao param de controlo da GEOFENCE*/

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    static final float GEOFENCE_RADIUS_IN_METERS = 20; //20 metros

    /*
    * Mapa para armazenar informacoes sobea uma determinada area
    * */

    static final HashMap<String, LatLng> LANDMARKS = new HashMap<>();

    static {
        //Area Portugal
        LANDMARKS.put("Funchal", new LatLng(32.659480, -16.923312));
        LANDMARKS.put("Porto", new LatLng(41.14, -8.63));
        LANDMARKS.put("Funchal 2", new LatLng(32.659656, -16.923113));
    }
}
