package com.test.android.googlemapsversao1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class MapsActivity_4 extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    //milisegundos
    private static final int MILLISECONDS_PER_SECOND = 1000;

    //Frequencia de atualizacao em seg
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    //Frequencia de atualizacao em milisegundos
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    //Frequencia de atualizacao rapida , em seg
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;

    //Frequencia de atualizacao rapida em milisegundos
    private static final int FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    /*Incluir no manifesto as seguintes permissoes:
     * Permite que a API determine o local  mais preciso possivel a partir dos provedores
     * de localizacao disponiveis, incluindo o Sistema de Posicionamento Global (GPS),
     * bem como os dados de WiFi e dados moveis
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     * Permite que a API use dados de WiFi ou dados moveis
     * (ou ambos) para determinar a localizacao do dispositivo.
     * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
     *
     * No build.gradle(Modulo:app)
     * implementation 'com.google.android.gms:play-services-location:15.0.1'
     * implementation 'com.google.android.gms:play-services-places:15.0.1'*/

    /*FusedLocationProviderClient:
     * Algoritmo de localizacao baseado nos fornecedores de localizacao GPS e de rede
     * (usando sensores para definir se o dispositivo esta em mopvimento)
     * Uma vez que analisa os dados internos do dispositivo de localizacao, GPS,
     * rede wi-fi e rede movel, apresenta os dados com muita precisao.*/

    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap myMap;
    LocationRequest locationRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_2);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createLocationRequest();
    }

    private Location mLastLocation;//ultima localizacao
    private Marker mCurrLocationMarker;//marcador atual
    /**
     * Usando para receber notificacoes de FusedLocationProviderApi
     * quando existe alteracao do local do dispositivo(movimento) foi alterado
     * ou nao npode mais ser determinado.*/

    LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The Last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + "" + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                //Toast.makeText(getApplicationContext(), "->"+location.getLatitude(), Toast.LENGTH_SHORT).show();
                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Estás aqui!!!");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = myMap.addMarker(markerOptions);

                onLocationChanged(locationResult.getLastLocation());

                //move map camera
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

            }
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        if (checkPermissions()) {
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
            myMap.setMyLocationEnabled(true);
        }
        //myMap.setMyLocationEnable(true);
        createLocationRequest();
    }

    /*
     * Modos de funcionamento baseado em prioridade
     * PRIORIDADE      INT. DE ATUALIZACAO           CONSUMO(BATERIA)           PRECISAO
     * HIGH_ACCURACY            5s                        7,25%                     +/- 10 metros
     * BALANCED_POWER           20s                        0,6%                     +/- 40 metros
     * NO_POWER                  -                       pequenas                    1600 metros
     *
     * LocationRequest:
     * Um objeto de dados que contem parametros de qualidade de servicos para solicitacoes/atualizacoes
     * na gestao da localizacao do dispositivo.*/

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //1 metro
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
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            getAddressForLocation(getApplicationContext(), location);

                            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraPosition position = new CameraPosition.Builder().target(point).zoom(12).build();
                            myMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                            Toast.makeText(getApplicationContext(),getAddressForLocation(getApplicationContext(), location), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper());
    }
    /*Classe Geocoder - A Geocodificacao de enderecos é o processo automatico de interpretacao
     de referencias relacionadas com a morada e a sua conversao em codigo.
        Address - Uma classe que representa um endereci, ou seja, um conjunto de strings que descreve um local.
     */

    public static String getAddressForLocation(Context context, Location location){
        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
            if(addresses != null && addresses.size() > 0){
                return getStreetNameForAddress(addresses.get(0));
            } else {
                return null;
            }

        } catch (IOException e){
            return null;
        }
    }

    public static String getStreetNameForAddress(Address address) {
        String streetName = address.getAddressLine(0);
        if(streetName == null)  {
            streetName = address.getThoroughfare();
        }

        return streetName;
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " + Double.toString(location.getLatitude())+","+
                Double.toString(location.getLongitude());
        Toast.makeText(getApplicationContext(), getAddressForLocation(getApplicationContext(), location), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private boolean  checkPermissions() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PackageManager.PERMISSION_GRANTED);
    }
}
