package com.example.cellphonetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emredavarci.noty.Noty;
import com.example.PreferenceClass;
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
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    ImageView back,noti_setting;
    LinearLayout c_m_layout;
    TextView name,address;
    LinearLayout call_btn,history_btn;
    String call_number,str_device;
    PreferenceClass preferenceClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferenceClass = new PreferenceClass(this);
        back = findViewById(R.id.back_btn);
        noti_setting = findViewById(R.id.notification_setting);
        c_m_layout = findViewById(R.id.click_marker_layout);
        name = findViewById(R.id.name);
        address = findViewById(R.id.address);
        call_btn = findViewById(R.id.call_btn);
        history_btn = findViewById(R.id.history_btn);
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClickMarkerLayout(false);
            }
        });

        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callClick();
            }
        });

        history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                intent.putExtra("device",str_device);
                startActivity(intent);

            }
        });

        noti_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,NotificationSeetingActivity.class));
            }
        });





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
                        addMarker(new LatLng(lat,lng),models.get(i).getDevice(),true,models.get(i));
                    }else {
                        addMarker(new LatLng(lat,lng),models.get(i).getDevice(),false,models.get(i));
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
                if(models.size()!=response.body().size()){
                    models.clear();
                    mMap.clear();
                    models.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    //progressDialog.cancel();
                    for(int i = 0;i<models.size();i++){
                        Double lat = Double.valueOf(models.get(i).getLat());
                        Double lng = Double.valueOf(models.get(i).getLng());
                        if(models.get(i).getSelected().equals("1")){
                            addMarker(new LatLng(lat,lng),models.get(i).getDevice(),true,models.get(i));
                        }else {
                            addMarker(new LatLng(lat,lng),models.get(i).getDevice(),false,models.get(i));
                        }

                        if(i==2){
                            //moveCamera(new LatLng(lat,lng));
                        }


                    }

                }else {
                    GpsTracker gpsTracker = new GpsTracker(MainActivity.this);
                    for(int i = 0;i<response.body().size();i++){

                        Double lat = Double.valueOf(response.body().get(i).getLat());
                        Double lng = Double.valueOf(response.body().get(i).getLng());
                        LatLng newLatLng = new LatLng(lat,lng);
                        Marker marker = lstMarcadores.get(i);
                        Model model = (Model) marker.getTag();

                        //addMarker(newLatLng,response.body().get(i).getDevice(),true);
                        if(model!=null){
                            Double myLat = Double.valueOf(model.getLat());
                            Double myLon = Double.valueOf(model.getLng());
                            marker.setTag(response.body().get(i));

                            if(distance(myLat,myLon,lat,lng)>5){

                                MarkerAnimation.animateMarkerToGB(marker,newLatLng, new LatLngInterpolator.Spherical());
                            }

                            if(preferenceClass.getNotificationOn()){
                                Double gpsLat = gpsTracker.getLat();
                                Double gpsLng = gpsTracker.getLon();

                                Double radius = Double.valueOf(preferenceClass.getRadious());
                                Log.e("distance",String.valueOf(distance(gpsLat,gpsLng,lat,lng))+"+Old:"+String.valueOf(preferenceClass.getRadious()));
                                if(distance(gpsLat,gpsLng,lat,lng)>radius){
                                    sendNotification(model.getDevice());
                                }
                            }


                        }




                    }
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
       showClickMarkerLayout(true);
       marker.showInfoWindow();
       Model model = (Model) marker.getTag();
       name.setText("Name :"+model.getDevice());
       Double lat = Double.valueOf(model.getLat());
       Double lng = Double.valueOf(model.getLng());
       address.setText("Address: "+getCompleteAddressString(lat,lng));
       call_number = model.getMobile();
       str_device = model.getDevice();
       return true;
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
       // googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

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

    void addMarker(LatLng latLng,String name,boolean isVisiable,Model model){

        Marker marker;
        marker = mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .title(name)
                );

        marker.setTag(model);
        lstMarcadores.add(marker);
        if(isVisiable){
            marker.setVisible(true);
        }else {
            marker.setVisible(false);
        }


    }

    void showClickMarkerLayout(boolean b){
        if(b){
         recyclerView.setVisibility(View.GONE);
         back.setVisibility(View.VISIBLE);
         c_m_layout.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            back.setVisibility(View.GONE);
            c_m_layout.setVisibility(View.GONE);

        }
    }

    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    void callClick(){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", call_number, null));
        startActivity(intent);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void sendNotification(String device) {
        Context ctx = MainActivity.this;
        StyleableToast.makeText(ctx, device+" is out of range", Toast.LENGTH_LONG, R.style.mytoast).show();
//        RelativeLayout root = findViewById(R.id.root);
//        Noty.init(ctx, device+"is out of range", root,
//                Noty.WarningStyle.ACTION)
//                .setActionText("OK")
//                .setWarningBoxBgColor("#ff5c33")
//                .setWarningTappedColor("#ff704d")
//                .setWarningBoxPosition(Noty.WarningPos.CENTER)
//                .setAnimation(Noty.RevealAnim.FADE_IN, Noty.DismissAnim.BACK_TO_BOTTOM, 400,400)
//                .show();
//
//        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx);
//
//        b.setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.drawable.push_notification_settings)
//                .setTicker("Hearty365")
//                .setContentTitle("Default notification")
//                .setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
//                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
//                .setContentInfo("Info");
//
//
//        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(1, b.build());
    }

}
