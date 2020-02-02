package com.example.cellphonetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.PreferenceClass;

import java.util.List;
import java.util.Locale;

public class NotificationSeetingActivity extends AppCompatActivity {
    TextView location_txt;
    EditText radious_edt;
    SwitchCompat notificationSwitch;
    PreferenceClass preferenceClass;
    Button saveBtn;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_seeting);
        location_txt = findViewById(R.id.tv_location_settings);
        radious_edt = findViewById(R.id.tv_radis);
        notificationSwitch = findViewById(R.id.switch_notification);
        saveBtn = findViewById(R.id.save_btn);
        back = findViewById(R.id.back);
        preferenceClass = new PreferenceClass(this);
        Log.e("noti",String.valueOf(preferenceClass.getNotificationOn()));
        if(preferenceClass.getNotificationOn()){
            notificationSwitch.setChecked(true);
        }else {
            notificationSwitch.setChecked(false);
        }
        radious_edt.setText(String.valueOf(preferenceClass.getRadious()));

        GpsTracker gpsTracker = new GpsTracker(this);
        String address = getCompleteAddressString(gpsTracker.getLat(),gpsTracker.getLon());
        location_txt.setText(address);

        notificationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notificationSwitch.isChecked()){
                    preferenceClass.saveNotificationOn(true);
                }else {
                    preferenceClass.saveNotificationOn(false);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String radius = radious_edt.getText().toString();
                if(radius.isEmpty()){

                }else {
                    preferenceClass.saveRadious(Integer.parseInt(radius));
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });





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
}
