package com.example.cellphonetracker;

/**
 * Created by root on 12/3/17.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


public class GpsTracker extends Service implements LocationListener {

    private final Context mContext;

    boolean isGPSEnable=false;
    boolean isNetworkEnable=false;
    boolean canGetLocation=false;
    Location location;
    double lat=23.810332;
    double lon=90.4125181;
    private static final long min_dist_for_update=10;
    private static final long min_time_for_update=1000*10*1;
    protected LocationManager locationManager;


    public GpsTracker(Context context){
        this.mContext=context;
        getlocation();
    }
    public Location getlocation(){
        try {
            locationManager= (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnable= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnable=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if(!isGPSEnable && !isNetworkEnable){

            }else {
                this.canGetLocation=true;
                if (isNetworkEnable){
                    if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                        return null;
                    }
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,min_time_for_update,min_dist_for_update,this);
                if(locationManager!=null){
                    location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){
                        lat=location.getLatitude();
                        lon=location.getLongitude();
                    }

                }


                if (isGPSEnable){
                    if(location==null){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,min_time_for_update,min_dist_for_update,this);
                        if (locationManager!=null){
                            location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location!=null){
                                lat=location.getLatitude();
                                lon=location.getLongitude();

                            }
                        }

                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return location;
    }

    public void stopUsingGPS(){
        if (locationManager!=null){
            if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                return;
            }
            locationManager.removeUpdates(GpsTracker.this);
        }
    }

    public double getLat(){
        if (location!=null){
            lat=location.getLatitude();
        }
        return lat;
    }

    public double getLon(){
        if (location!=null){
            lon=location.getLongitude();
        }
        return lon;
    }


    public boolean CanGetLocation(){
        return this.canGetLocation;
    }

    public void showSettingAlert(){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
        //Setting Dialog Title
        alertDialog.setTitle("GPS is seeting");
        //seeting Dialog Message
        alertDialog.setMessage("Gps is not enable do you want to go to" +
                " setting to enable it");
        //On pressing Setting button
        alertDialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        //on pressing cancle button
        alertDialog.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        //showing Alert Dialog
        alertDialog.show();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

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


}
