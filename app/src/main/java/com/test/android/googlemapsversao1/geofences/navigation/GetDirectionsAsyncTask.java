package com.test.android.googlemapsversao1.geofences.navigation;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.test.android.googlemapsversao1.geofences.NavigationActivity;

import org.w3c.dom.Document;

import java.util.Map;

public class GetDirectionsAsyncTask extends AsyncTask<Map<String, String>, Object, Document> {

    public static final String USER_CURRENT_LAT = "user_current_lat";
    public static final String USER_CURRENT_LONG = "user_current_long";
    public static final String DESTINATION_LAT= "destination_lat";
    public static final String DESTINATION_LONG= "destination_long";
    public static final String DIRECTIONS_MODE= "directions_mode";
    private NavigationActivity activity;
    private Exception exception;
    private ProgressDialog progressDialog;

    //Classe que lida com o tratamento do XML response
    GMapV2Direction md;

    public int value;

    public GetDirectionsAsyncTask(NavigationActivity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected Document doInBackground(Map<String, String>... maps) {
        return null;
    }

    public void onPreExecute() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Calculating directions");
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Document result) {
        String distanceText = md.getDistanceText(result);
        String durationText = md.getDurationText(result);
        progressDialog.dismiss();
        if(exception == null) {
            activity.handleGetDirectionsResult(md.getDirections(result), distanceText, durationText);
        }else{
            processException();
        }
    }

    protected Document doInBackground(Map<String, String>... params){
        Map<String, String> paramMap = params[0];
        try{
            LatLng fromPosition = new LatLng(Double.valueOf(paramMap.get(USER_CURRENT_LAT)),
                    Double.valueOf(paramMap.get(USER_CURRENT_LONG)));
            LatLng toPosition = new LatLng(Double.valueOf(paramMap.get(DESTINATION_LAT)),
                    Double.valueOf(paramMap.get(DESTINATION_LONG)));
            md = new GMapV2Directions();
            Document doc = md.getDocument(fromPosition,
                    toPosition, paramMap.get(DIRECTIONS_MODE));
            //ArrayList<LatLng> directionPoints = md.getDirection(doc);
            //value = md.getDistanceValue(doc);
            return doc;
        }catch (Exception e){
            exception = e;
            return null;
        }
    }

    private void processException() {
        Toast.makeText(activity, "Error retriving data", 3000).show();
    }
}
