package com.sergio.googlemaps_v1;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.test.android.googlemapsversao1.R;

import java.util.ArrayList;
import java.util.List;


/*
No Android 8.0 (nível de API 26) e superior, se uma aplicação estiver
em execução
 em segundo plano durante a monitorização de uma 'cerca' geográfica,
  o dispositivo
  responderá a eventos de rastreamento geográfico a cada dois minutos.
 */
public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = "IntentService";

    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    public GeofenceTransitionsIntentService() {
        super("TAG");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Toast.makeText(this, "Create:", Toast.LENGTH_SHORT).show();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if(geofencingEvent.hasError()){
            Log.e(TAG, "GeofencingEvent Error: " +
            geofencingEvent.getErrorCode());
            Toast.makeText(this, "Error:", Toast.LENGTH_SHORT).show();
            return;
        }

        String description = getGeofenceTransitionDetails(geofencingEvent);
        sendNotification(description);

        //obtem o tipo de transicao e o id do geofences que desencadeou a transicao.
        int geofenceTransiition = geofencingEvent.getGeofenceTransition();

        //Testar o tipo de retorno(entrada,saida)
        if (geofenceTransiition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransiition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            /*Obtenha  as geofences que foram adicionadas. UM unico evento pode desencadear multiplas geofences.
            * Iterando sobre a lista podemos obter as caracteristicas
            * associadas a cada geoifences*/

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            //Send notification and log the transition details
            //sendNotification(geofenceTransitionDetails);
            //Log.i(TAG, geofenceTransitionDetails);

            Toast.makeText(this, "dentro", Toast.LENGTH_SHORT).show();
        }else {
            //Log the error
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransiition));
        }
    }
}
