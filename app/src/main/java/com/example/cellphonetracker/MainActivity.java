package com.example.cellphonetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;

import com.example.cellphonetracker.Interfaces.ApiService;
import com.example.cellphonetracker.adapter.DeviceAdapter;
import com.example.cellphonetracker.serverCalls.NetworkClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import io.sulek.ssml.SSMLLinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    DeviceAdapter adapter;
    private GoogleMap mMap;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Model> models = new ArrayList<>();
    ProgressDialog progressDialog;
    Thread thread;
    List<Marker> lstMarcadores =  new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading..");
        progressDialog.setCancelable(false);

        recyclerView = findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(getApplication());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DeviceAdapter(MainActivity.this, models, mMap, new DeviceAdapter.clickDevice() {
            @Override
            public void onSearchSelected(int position, boolean isSelected) {
                Log.e("click", String.valueOf(position));
                if(isSelected){
                    lstMarcadores.get(position).setVisible(true);
                }else {
                    lstMarcadores.get(position).setVisible(false);
                }
                lstMarcadores.get(position).showInfoWindow();

            }
        }, new DeviceAdapter.clickItem() {
            @Override
            public void onItemSelected(Model model) {
                Double lat = Double.valueOf(model.getLat());
                Double lng = Double.valueOf(model.getLng());
                moveCameraTo(new LatLng(lat,lng));
            }
        });

        recyclerView.setAdapter(adapter);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        FragmentManager fm = getSupportFragmentManager();
                        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
                        if (fragment == null) {
                            fragment = new SupportMapFragment();
                            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
                        }
                        fragment.getMapAsync(MainActivity.this);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();



    }

    void getAllUser(){
        //progressDialog.show();
        models.clear();
        mMap.clear();
        ApiService service = NetworkClient.getRetrofitClient().create(ApiService.class);
        Call<ArrayList<Model>> call;
        call = service.getAllUsers();
        call.enqueue(new Callback<ArrayList<Model>>() {
            @Override
            public void onResponse(Call<ArrayList<Model>> call, Response<ArrayList<Model>> response) {
                Log.e("pappu",String.valueOf(response.body()));
                models.addAll(response.body());
                adapter.notifyDataSetChanged();
                //progressDialog.cancel();
                for(int i = 0;i<models.size();i++){
                    Double lat = Double.valueOf(models.get(i).getLat());
                    Double lng = Double.valueOf(models.get(i).getLng());
                    if(models.get(i).getSelected().equals("1")){
                        addMarker(new LatLng(lat,lng),models.get(i).getDevice(),true);
                    }else {
                        addMarker(new LatLng(lat,lng),models.get(i).getDevice(),false);
                    }

                    if(i==2){
                        //moveCamera(new LatLng(lat,lng));
                    }


                }
                //lstMarcadores.get(0).showInfoWindow();
                //lstMarcadores.get(1).showInfoWindow();
              //  lstMarcadores.get(2).showInfoWindow();
                //lstMarcadores.get(3).showInfoWindow();
                //lstMarcadores.get(4).showInfoWindow();

            }

            @Override
            public void onFailure(Call<ArrayList<Model>> call, Throwable t) {
                Log.e("pappu",t.toString());
               // progressDialog.cancel();

            }
        });
    }
    void getAllUserUpdate(){
       // mMap.clear();
        //progressDialog.show();
        ApiService service = NetworkClient.getRetrofitClient().create(ApiService.class);
        Call<ArrayList<Model>> call;
        call = service.getAllUsers();
        call.enqueue(new Callback<ArrayList<Model>>() {
            @Override
            public void onResponse(Call<ArrayList<Model>> call, Response<ArrayList<Model>> response) {
                Log.e("pappu",String.valueOf(response.body()));
                for(int i = 0;i<response.body().size();i++){

                    Double lat = Double.valueOf(response.body().get(i).getLat());
                    Double lng = Double.valueOf(response.body().get(i).getLng());
                    LatLng newLatLng = new LatLng(lat,lng);
                    //addMarker(newLatLng,response.body().get(i).getDevice(),true);
                   Marker marker = lstMarcadores.get(i);
                   MarkerAnimation.animateMarkerToGB(marker,newLatLng, new LatLngInterpolator.Spherical());


                }





            }

            @Override
            public void onFailure(Call<ArrayList<Model>> call, Throwable t) {
                Log.e("pappu",t.toString());
                // progressDialog.cancel();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMap!=null){
            if ( thread.getState() == Thread.State.NEW )
                //then we have a brand new thread not started yet, lets start it
                thread.start();
            else{

            }
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        GpsTracker tracker = new GpsTracker(MainActivity.this);
        Double lat = tracker.getLat();
        Double lng = tracker.getLon();
        moveCamera(new LatLng(lat,lng));
        getAllUser();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    while (!thread.isInterrupted()) {
                        Log.e("thrade","run");
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                getAllUserUpdate();
                            }
                        });
                    }
                } catch (InterruptedException e) {

                }

            }
        });
        thread.start();



    }

    void moveCamera(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(12)                   // Sets the zoom
                //.bearing(90)                // Sets the orientation of the camera to east
                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    void moveCameraTo(LatLng latLng){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(16)                   // Sets the zoom
                //.bearing(90)                // Sets the orientation of the camera to east
                //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    void addMarker(LatLng latLng,String name,boolean isVisiable){

        Marker marker;
        marker = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .title(name)
                );
        lstMarcadores.add(marker);
        marker.setTag(latLng);
        if(isVisiable){
            marker.setVisible(true);
        }else {
            marker.setVisible(false);
        }


    }

}
