package com.test.android.googlemapsversao1.geofences;

import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.test.android.googlemapsversao1.R;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback {

    /*
    * private static final LatLng AMSTERDAM = new LatLng(52.37, 4.89);
    * private static final LatLng PARIS = new LatLng(52.37, 4.89);*/

    /**
     * Para fazer requisicoes http devemos incluir(usaremos na class GMapV2Direction)
     * build.gradle
     * android{
     *     useLibrary 'org.apache.http.legacy'
     * }*/

    private GoogleMap map;
    private SupportMapFragment fragment;
    private LatLngBounds latlngBounds;
    private Button bNavigation, bNavigationWalking;
    private Polyline newPolyline;
    private boolean isTravelingTo = false;
    private int width, height;

    private LatLng device;
    private LatLng client;

    TextView distanceText, durationText;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        /**Reaproveitar espaco das notificacoes*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_navigation);

        /***Intent intent = getIntent();
         Bundle dados = intent.getExtras();
         double latReturned = dados.getDouble("lat");
         double longReturned = dados.getDouble("long");

         device = new LatLng(latReturned,longReturned);

         double latClientReturned = dados.getDouble("latClient");
         double longClientReturned = dados.getDouble("longClient");
         client = new LatLng(latClientReturned,longClientReturned);

         String nameClientReturned = dados.getString("nameClient");*/

        //obter dimensoes da tela
        getScreenDimensions();
        double latReturned = 48.856132;
        double longReturned = 4.895439;
        device = new LatLng(latReturned, longReturned);
        double latClientReturned = 49.856132;
        double longClientReturned = 4.895439;
        client = new LatLng(latClientReturned, longClientReturned);
        String nameClientReturned = "sergio";

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        distanceText = (TextView) findViewById(R.id.distanceText);
        durationText = (TextView) findViewById(R.id.durationText);

        bNavigation = (Button) findViewById(R.id.bNavigation);
        bNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se o mapeamento estiver marcado e pretender
                //alterar entre modo 'a pe' ou 'conducao'
                if (!isTravelingTo) {
                    isTravelingTo = true;
                    //findDirections(AMSTERDAM.latitude,
                    // AMSTERDAM.longitude, PARIS.latitude, PARIS.longitude,
                    // GMapV2Dirextion.MODE_DRIVING);
                    findDirections(device.latitude, device.longitude, client.latitude, client.longitude,
                            GMapV2Direction.MODE_DRIVING);

                } else {
                    isTravelingTo = false;
                    findDirections(device.latitude, device.longitude, client.latitude, client.longitude,
                            GMapV2Direction.MODE_DRIVING);
                }
            }
        });

        bNavigationWalking = (Button) findViewById(R.id.bNavigationWalking);
        bNavigationWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se o mapeamento estiver marcado e pretender
                //alterar entre modo 'a pe' ou 'conducao'
                if (!isTravelingTo) {
                    isTravelingTo = true;
                    //findDirections(AMSTERDAM.latitude,
                    // AMSTERDAM.longitude, PARIS.latitude, PARIS.longitude,
                    // GMapV2Dirextion.MODE_DRIVING);
                    findDirections(device.latitude, device.longitude, client.latitude, client.longitude,
                            GMapV2Direction.MODE_DRIVING);

                } else {
                    isTravelingTo = false;
                    findDirections(device.latitude, device.longitude, client.latitude, client.longitude,
                            GMapV2Direction.MODE_DRIVING);
                }
            }
        });
    }

        //Capturar o resultado da direcao
        public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints, String value, String time){
            //Linha que representara o caminho
            PolylineOptions rectLine = newPolylineOptions().width(5).color(Color.RED);

            //Criacao do caminho
            for (int i = 0; i < directionPoints.size(); i++){
                rectLine.add(directionsPoints.get(i));
            }
            if(newPolyline != null) {
                newPolyline.remove();
            }
            //Add ao mapa
            newPolyline = map.addPolyline(rectLine);
            if (isTravelingTo) {
                latlngBounds = createLatLngBoundsObject(device, client);
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, width, height, 150));
            }
            //Alteracao do texto
            distanceText.setText(value);
            durationText.setText(time);
            Toast.makeText(NavigationActivity.this, "Distance"+value+"\n Time:"+time, Toast.LENGTH_SHORT).show();

        }

        public void handleGetDistanceValue(String value, String text){
            Toast.makeText(NavigationActivity.this, "Distance:"+text, Toast.LENGTH_SHORT).show();
        }

        public void handleGetDurationValues(String value, String text){
            Toast.makeText(NavigationActivity.this, "Time to destination:", Toast.LENGTH_SHORT).show();
        }

        //obte dimensoes da tela
        private void getScreenDimensions() {
            Display display = getWindowManager().getDefaultDisplay();
            width = display.getWidth();
            height = display.getHeight();
            Toast.makeText(NavigationActivity.this, "->"+width, Toast.LENGTH_SHORT).show();

        }

        //Criacao de limites(Bounds)
        private LatLngBounds createLatLngBoundsObject(LatLng firstLocation, LatLng secondLocation){
            if(firstLocation != null && secondLocation != null){
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(firstLocation).include(secondLocation);

                return builder.build();
            }
            return null;
        }

        //Metodo responsavel por lancr a tarefa de capturar do caminho
        public void findDirection(double fromPositionDoubleLat,double fromPositionDoubleLong
        , double toPositionDoubleLat, double toPositionDoubleLong, String mode) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT,String.valueOf(fromPositionDoubleLat));
            map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG,String.valueOf(fromPositionDoubleLong));
            map.put(GetDirectionsAsyncTask.DESTINATION_LAT,String.valueOf(fromPositionDoubleLat));
            map.put(GetDirectionsAsyncTask.DESTINATION_long,String.valueOf(fromPositionDoubleLong));
            map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE,mode);

            GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
            asyncTask.execute(map);
        }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }
}
