package com.example.cellphonetracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.cellphonetracker.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomMarkerInfoWindowView implements GoogleMap.InfoWindowAdapter {
    private final View markerItemView;
    Context context;

    public CustomMarkerInfoWindowView(Context context) {
        this.context = context;

        markerItemView = LayoutInflater.from(context).inflate(R.layout.marker_info_window, null);
    }

    private void rendowWindowText(Marker marker, View view){


    }

    @Override
    public View getInfoWindow(Marker marker) { // 2
        rendowWindowText(marker, markerItemView);
        return markerItemView;  // 4
    }


    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowText(marker, markerItemView);
        return markerItemView;
    }
}
