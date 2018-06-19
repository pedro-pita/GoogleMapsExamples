package com.test.android.googlemapsversao1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapsActivity_2 extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    /*Define a configuracao do googleMapOptions para um GoogleMap.
     * Essas opcoespodem ser usadas ao adicionar um mapa por meio de programacao na aplicacao (em oposicao a via XML).*/
    private GoogleMapOptions options;
    static final LatLng PORTO = new LatLng(41.14, -8.63);
    private Marker markerPovoa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_2);
        options = new GoogleMapOptions();
        /*compassEnable -Define se a bússola deve estar ativada.
         * rotateGesturesEnabled - Define se os gestos de rotacao devem ser ativados.
         * tiltGesturesEnable - Define se os gestos de inclinacao devem ser ativados.*/
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE).compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(mMap.MAP_TYPE_TERRAIN);

        /*Ativar o layer do trafego, exibir informacoes sobre o transito visualmente no mapa
         * a rua aparece a vermelho se estiver um engarrafamento*/
        mMap.setTrafficEnabled(true);
        //Definie a posicao da camara
        CameraPosition position = new CameraPosition.Builder().target(PORTO).zoom(12).build();

        /*Metodos de deslocamento de camera:
         * animateCamera: deslocamento com animacao
         *
         * moveCamera: deslocamento sem animacao*/
        //Mover a camara com animacao para a posicao definida
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));

        //Alterar o tipo de mapa na proximidade
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //Atualizar a localiizacao atual do dispositivo
        //mMap.setMyLocationEnabled(true);

        //Controlos graficos, gestos de deslocamento sobre o mapa
        //Desativar os controlos do zoom
        mMap.getUiSettings().setZoomControlsEnabled(false);
        //Desativar a bussola
        mMap.getUiSettings().setCompassEnabled(false);
        //Desativar a rotacao do mapa
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        //Marcadores . Adicionar atraves do metodo addMarker(MarkerOptions)
        LatLng funchal = new LatLng(32.659656, -16.923113);
        //Criacao e adicao do marcador no mapa7
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(funchal);
        markerOptions.title("Mesmo no centro do funchal!!!");
        mMap.addMarker(markerOptions);
        mMap.addMarker(new MarkerOptions().position(PORTO).title("Marker in OPORTO"));

        //Criacao pela classe Marker
        Marker markerBraga = mMap.addMarker(
                new MarkerOptions().position(
                        new LatLng(41.548922, -8.428255)).title("Braga"));
        /*
         * Propriedades dos marcadores
         * Position: posicao no mapa(lat, long), unica prop. obrigatoria
         * Alpha: opacidade
         * Title: Texto contido no marcador aol toque
         * Snippet: Texto adicional exibido abaixo do titulo
         * Icon: bitmap exibido no lugar da imagem padrao
         * Draggable: permite o deslocamento do marcador , por defeito esta imovel
         * Visible: visibilidade do marcador*/

        Marker markerBarcelos = mMap.addMarker(
                new MarkerOptions().position(new LatLng(41.529630, -8.618231))
                        .title("Melhor terra de portugal").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        //Mudar imagem do marcador
        markerPovoa = mMap.addMarker(
                new MarkerOptions().position(new LatLng(41.381080, -8.761150))
                        .title("Povoa de Varzim")
                        .snippet("Boa praia")
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

        //Remocao de um marcador - metodo -> remove()
        markerBraga.remove();

        //Limpar todos os marcadores
        //mMap.clear();

        /*mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                markerPovoa.setPosition(latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });*/

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.d("Marker", "onMarkerDragStart" + marker.getPosition().latitude + "..." + marker.getPosition().longitude);
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d("Marker", "onMarkerDragStart");
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }
        });

        //Definir um listener para o clique em marcadores(marker)
        mMap.setOnMapClickListener(this);
        //Exemplo 2
        mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
                //Flip the r, g and b components of the circle's
                //stroke color.
                Log.d("Circle", "onCircleClick");
                circle.remove();
            }
        });

    }

    /*
     * Exemplo 1 - Toast com notificacao de posicao*/
    /*
    @Override
    public boolean onMarkerClick(final Marker marker) {
        Toast.makeText(this, marker.getTitle()+ "\n pos: "+marker.getPosition(), Toast.LENGTH_SHORT).show();
        //Retorna falsesignifica que o evento nao foi consumido
        return false;
    }*/
    /*Exempl 2 - Circulo sobre marcador*/
    /*
     * CircleOptions:
     * .radius(100) - Distancia em metros(raio)
     * .center(marker.getPosition()) - Centrar em funcao de ue ponto
     * .fillColor(Color.BLUE) - cor de preenchimenteo
     * .strokeColor(Color.BLACK) - cor de borda
     * .strokeWidth(5)); - espessura da borda
     *
     * Representação hexadecimal das cores
     * 0x -> representa hexadecimal code
     * segundo parametro -> a percetagem de transparencia 55%.
     * Para 100% de transparencia, definir 00
     * Para 0% transparency (ie, opaque), definir ff
     * 6 caracteres (00ff00) que definem a cor no formato RGB
     * */

    @Override
    public boolean onMarkerClick(Marker marker) {
        /*mMap.addCircle(new CircleOptions().radius(100).center(marker.getPosition())
            .fillColor(Color.BLUE).strokeColor(Color.BLACK).strokeWidth(5));
        Toast.makeText(this, marker.getTitle()+"\n pos: "+marker.getPosition(),Toast.LENGTH_SHORT).show();*/

        Circle circle = mMap.addCircle(new CircleOptions().center(marker.getPosition()).radius(1000)
                .strokeColor(0xffFF00FF).fillColor(Color.BLUE).clickable(true));

        //Instancia em novo objeto Poligono e adiciona
        //pontos para definir um retangulo(o 1 ponto deve coincidir com o ultimo)

        PolygonOptions rectOptions = new PolygonOptions().add(new LatLng(41.38, -8.76),
                new LatLng(41.58, -8.76),
                new LatLng(41.58, -8.96),
                new LatLng(41.38, -8.96),
                new LatLng(41.38, -8.76));

        //Cappturar o poligono
        Polygon polygon = mMap.addPolygon(rectOptions);

        /*Sobreposicoes - As sobreposicoes sao orientadas contra o solo,
        logo contra a superficie da terra e nao do ecra(como os marcadores)*/

        /*Sobreposicoes Ground overlay:
         * fornece uma unica imagem para uma localizacao fixa no mapa*/
        GroundOverlay groundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                .position(new LatLng(41.38, -8.76), 100, 100));

        //Retorno false significa que o evento nao foi consumido.
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {


    }
}
