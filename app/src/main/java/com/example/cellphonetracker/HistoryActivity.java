package com.example.cellphonetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HistoryActivity extends AppCompatActivity {

    CardView cardSelectDate;
    CardView cardSelectStartTime;
    CardView cardSelectEndTime;
    TextView txtdate;
    TextView txtStartTime;
    TextView txtEndTime;
    ImageView back;

    String strOpenTime,strEndTime,strDate;
    Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        cardSelectDate = findViewById(R.id.select_date_card);
        cardSelectStartTime = findViewById(R.id.select_start_time_card);
        cardSelectEndTime = findViewById(R.id.select_end_time_card);
        txtdate = findViewById(R.id.txt_date);
        txtEndTime = findViewById(R.id.txt_end_time);
        txtStartTime = findViewById(R.id.txt_start_time);
        searchBtn = findViewById(R.id.search_btn);
        back = findViewById(R.id.back_btn);

        cardSelectStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker("start");
            }
        });
        cardSelectEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker("end");
            }
        });
        cardSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(strDate==null || strEndTime==null || strOpenTime ==null){
                    Toast.makeText(HistoryActivity.this,"Select all the fields",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(HistoryActivity.this,MapsActivity.class);
                    intent.putExtra("start",strDate+" "+strOpenTime+":00");
                    intent.putExtra("end",strDate+" "+strEndTime+":00");
                    intent.putExtra("device",getIntent().getStringExtra("device"));
                    startActivity(intent);
                    //finish();
                }

            }
        });
    }


    private void openTimePicker(final String chk){

        // Get Current Time
        final Calendar c = Calendar.getInstance();
//        mHour = c.get(Calendar.HOUR_OF_DAY);
//        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (chk.equals("start")){
                            if (hourOfDay<12){
                                strOpenTime = String.valueOf(hourOfDay)+":"+String.valueOf(minute)+" AM";
                            }else if (hourOfDay==12){
                                strOpenTime = String.valueOf(hourOfDay)+":"+String.valueOf(minute)+" PM";
                            }else {
                                strOpenTime = String.valueOf(hourOfDay-12)+":"+String.valueOf(minute)+" PM";
                            }


                            txtStartTime.setText(strOpenTime);
                            strOpenTime = String.valueOf(hourOfDay)+":"+String.valueOf(minute);

                        }else {
                            if (hourOfDay<12){
                                strEndTime = String.valueOf(hourOfDay)+":"+String.valueOf(minute)+" AM";
                            }else if (hourOfDay==12){
                                strEndTime = String.valueOf(hourOfDay)+":"+String.valueOf(minute)+" PM";
                            }else {
                                strEndTime = String.valueOf(hourOfDay-12)+":"+String.valueOf(minute)+" PM";
                            }


                            txtEndTime.setText(strEndTime);
                            strEndTime = String.valueOf(hourOfDay)+":"+String.valueOf(minute);
                        }


                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }


    private void chooseDate() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker =
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(final DatePicker view, final int year, final int month,
                                          final int dayOfMonth) {

                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        calendar.set(year, month, dayOfMonth);
                        String dateString = sdf.format(calendar.getTime());
                        strDate = dateString;

                        txtdate.setText(dateString); // set the date
                    }
                }, year, month, day); // set date picker to current date

        datePicker.show();

        datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                dialog.dismiss();
            }
        });
    }
}
