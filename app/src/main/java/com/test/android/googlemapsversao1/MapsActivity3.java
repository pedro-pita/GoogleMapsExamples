package com.test.android.googlemapsversao1;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Map;

public class MapsActivity3 extends FragmentActivity implements OnMapReadyCallback {

    /*
     * GEOFENCING - Representa uma area geografica circular definida
     * atraves de dois parametros: a localizacao e um raio. Esta area é monitorada por
     * forma a ser possivel gerar alertas sempre que exista iteracao com as fronteiras.*/

    private GoogleMap myMap;

    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList;

    private PendingIntent mGeofencePendingIntent;

    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest locationRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapFragment.getMapAsync(this);

        //Empty list for string geofences
        mGeofenceList = new ArrayList<>();

        mGeofencingClient = LocationServices.getGeofencingClient(this);
        mGeofencePendingIntent = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;

        popularGeofenceList();

        //Associar os geofences ao PendingIntent
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGeofencingClient.addGeofences(getGeofencingRequest(), getmGeofencePendingIntent()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Add", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Erro", Toast.LENGTH_SHORT).show();
            }
        });


        myMap.setMyLocationEnabled(true);
    }
    /*
     * As classes GeofencingRequest e GeofencingRequestBuilder definem um especificacao dos geofence
     * a serem monitoradas e para definir como os eventos de geofence relacionados sao lancados
     * INITIAL_TRIGGER_ENTER indica que o seriço de delimintacao geografica deve acionar uma
     * notificacao GEOFENCE_TRANSITION_ENTER quando a geofence for adicionada e se o dispositivos
     * já estiver dentro dessa area geografica.
     * GEOOFENCE_TRANSITION_DWELL: atraso na transicao a notificacao depois de o dispositivo entrar no
     * geofence é lancada com um determinado atraso.*/

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    /*É necessario definir um PendingIntent que indica um IntentService, que ficará responsavel
     * por lidar com as transicoes registadas no objeto Geofences*/

    private PendingIntent getmGeofencePendingIntent() {
        Toast.makeText(this, "getGeofencePendingIntent:", Toast.LENGTH_SHORT).show();
        //Reutilizar o PemdimgIntent seja tiver a ser usada.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        /*FLAG_UPDATE_CURRENT usamos a constante para termos a mesma intencao
        pendente que a devolvida pelos metoodos addGeofences() e remoceGeofences()*/

        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return mGeofencePendingIntent;
    }

    /*
     * Adicionar um novo objeto Geofence é feita usando o construtor GeofencingRequest.Builder
     * com os seguintes metodos:
     * build() - Criar o objeto*/

    private void popularGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    /*Definir um ID da solicitacao).
                     * Ideal para remover uma geofence especifica*/
                    .setRequestId(entry.getKey())
                    //Definir a regiao circular da geofence
                    /*A area é definida pela lat e long do local de interesse,
                     * e um raio medidoo em metros que permite ajustar o quao perto o utilizador precisa
                     * de se aproximar do local antes de ser detectada  a geofence.*/
                    .setCircularRegion(entry.getValue().latitude, entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS)
                    /**
                     * Definir a duracao da expiracao da GEOFENCE
                     * Esta GEOFENCE fica automaticamente removida apos esse periodo*/
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    /** Definir os tipos de transicao de interesse
                     * Alertas sao gerados apenas +para esta transicao*/
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    //cRIACAO DA GEOFENCE
                    .build());
            LatLng latLng = new LatLng(entry.getValue().latitude, entry.getValue().longitude);
            myMap.addCircle(new CircleOptions()
                    .radius(Constants.GEOFENCE_RADIUS_IN_METERS)
                    .center(latLng)
                    .fillColor(Color.BLUE)
                    .strokeColor(Color.BLACK)
                    .strokeWidth(2));

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mGeofencingClient.removeGeofences(getmGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Geofences removed
                        //A sercas estao a ser removidas
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Failed to remove geofences
                //...
            }
        });
    }

    /**
     * locationRequest.setInterval()- define a taxa em milisegundos em que a aplicacao
     * prefere receber as atualizacoes de localizacao
     * locationRequest.setFastestInterval()- define o intervalo maximo em termos de rapidez de atualizacao*/

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //1 metros
        locationRequest.setSmallestDisplacement(1);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraPosition position = new CameraPosition.Builder()
                            .target(point).zoom(12).build();
                    myMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                    Toast.makeText(getApplicationContext(), point.toString(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}

/**
 * Para melhores resultados, o raio minimo da geofences deve ser definido entre 100 - 150 metros.
 * Quando o Wi-Fi esta disponivel, a precisao da localizacao é geralmente entre 20 e 50 metros.
 * Quando a localizacao interna esta disponivel(GPS), a faixa de precisao pode ser tao pequea quanto 5 metros.
 * A menos que saiba que a lozalizacao interna esta disponivel dentro da geofences, assuma que a preisao
 * da localizacao do Wi-Fi é cerca de 50 metros.*/
