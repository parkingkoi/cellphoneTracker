package com.example.cellphonetracker;

import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.cellphonetracker.Interfaces.ApiService;
import com.example.cellphonetracker.serverCalls.NetworkClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jaredrummler.android.device.DeviceName;

import java.time.Instant;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent data = new Intent();
        String start = data.getStringExtra("start");
        String end = data.getStringExtra("end");
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
       // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        String start = getIntent().getStringExtra("start");
        String end = getIntent().getStringExtra("end");
        String deviceName = DeviceName.getDeviceName();
        Log.e("date",deviceName+start+end);

        showHistory(start,end);
    }

    void showHistory(String start ,String end){

        progressDialog.show();
        String deviceName = getIntent().getStringExtra("device");
        ApiService service = NetworkClient.getRetrofitClient().create(ApiService.class);
        Call<ArrayList<HistoryResponse>> call;
        call = service.getHistory(start,deviceName,end);
        call.enqueue(new Callback<ArrayList<HistoryResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<HistoryResponse>> call, Response<ArrayList<HistoryResponse>> response) {
                progressDialog.cancel();
                Log.e("respobse",String.valueOf(response.body()));

                ArrayList<HistoryResponse> histories = response.body();
                ArrayList<LatLng> points = new ArrayList<>();
                PolylineOptions options = new PolylineOptions().width(8).color(Color.RED).geodesic(true);
                if(histories.size()>=0){
                    for(int i =0;i< histories.size();i++){
                        HistoryResponse historyResponse = histories.get(i);
                        Double lat = Double.parseDouble(historyResponse.getLat());
                        Double lng = Double.parseDouble(historyResponse.getLng());
                        points.add(new LatLng(lat,lng));
                        options.add(new LatLng(lat,lng));


                        if(i==0){
                            moveCamera(new LatLng(lat,lng));
                            addMarkerStart(new LatLng(lat,lng));
                        }
                        if(i==histories.size()-1){
                            addMarkerEnd(new LatLng(lat,lng));
                        }
                    }

                    mMap.addPolyline(options);
                }else {

                }



// create new PolylineOptions from all points
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(points)
                        .color(Color.RED)
                        .width(3f);

// add polyline to MapboxMap object



            }

            @Override
            public void onFailure(Call<ArrayList<HistoryResponse>> call, Throwable t) {
                Log.e("error",t.toString());
                progressDialog.cancel();

            }
        });
    }

    void moveCamera (LatLng latLng){
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng) // Sets the new camera position
                .zoom(17) // Sets the zoom
                .bearing(180) // Rotate the camera
                .tilt(30) // Set the camera tilt
                .build(); // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position));
    }

    void addMarkerStart(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_new));
        mMap.addMarker(markerOptions);

    }
    void addMarkerEnd(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_new_red));
        mMap.addMarker(markerOptions);

    }


}
