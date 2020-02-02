package com.example.cellphonetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cellphonetracker.Interfaces.ApiService;
import com.example.cellphonetracker.adapter.HistoryAdapter;
import com.example.cellphonetracker.serverCalls.NetworkClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    CardView cardSelectDate,cardSelectDateEnd;
    CardView cardSelectStartTime;
    CardView cardSelectEndTime;
    TextView txtdate,txtDateEnd;
    TextView txtStartTime;
    TextView txtEndTime;
    ImageView back;
    RecyclerView recyclerView;
    ScrollView scrollView;

    String strOpenTime,strEndTime,strDate_start,strDate_end;
    Button searchBtn,showAllHisBtn,showHisOfaDayBtn;
    ArrayList<HistoryResponse> historyResponseArrayList = new ArrayList<>();
    ArrayList<ModelHistory> modelHistories = new ArrayList<>();
    HistoryAdapter historyAdapter;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        cardSelectDate = findViewById(R.id.select_date_card);
        cardSelectDateEnd = findViewById(R.id.select_date_end);
        cardSelectStartTime = findViewById(R.id.select_start_time_card);
        cardSelectEndTime = findViewById(R.id.select_end_time_card);
        txtdate = findViewById(R.id.txt_date);
        txtDateEnd = findViewById(R.id.txt_date_end);
        txtEndTime = findViewById(R.id.txt_end_time);
        txtStartTime = findViewById(R.id.txt_start_time);
        searchBtn = findViewById(R.id.search_btn);
        back = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.recycler);
        scrollView = findViewById(R.id.select_view);
        showAllHisBtn = findViewById(R.id.all_history_btn);
        showHisOfaDayBtn = findViewById(R.id.show_history_of_day);

        hideHistoryOfADay();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading...");


        cardSelectStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimePicker("start");
            }
        });
        cardSelectDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate(false);
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
                chooseDate(true);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recyclerView.isShown()){
                   hideList();
                }else {
                    if(cardSelectDate.isShown()){
                        hideHistoryOfADay();
                    }else {
                        finish();
                    }

                }

            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(strDate_start==null ){
                    Toast.makeText(HistoryActivity.this,"Select all the fields",Toast.LENGTH_SHORT).show();
                }else {
//                    Intent intent = new Intent(HistoryActivity.this,MapsActivity.class);
//                    intent.putExtra("start",strDate_start+" "+strOpenTime+":00");
//                    intent.putExtra("end",strDate_end+" "+strEndTime+":00");
//                    intent.putExtra("device",getIntent().getStringExtra("device"));
//                    startActivity(intent);
                    String start = strDate_start+" 00:01:00";
                    String end = strDate_start+" 23:59:00";

                    getHistory(start,end);
                    //finish();
                }

            }
        });
        showAllHisBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllHistory();
            }
        });
        showHisOfaDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistoryOfADay();
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


    private void chooseDate(final boolean isStart) {
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
                        if(isStart){
                            strDate_start = dateString;
                            txtdate.setText(dateString); // set the date
                        }else {
                            strDate_end = dateString;
                            txtDateEnd.setText(dateString);
                        }



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

    void getHistory(String start,String end){
        progressDialog.show();
        recyclerView.removeAllViews();
        modelHistories.clear();
        final String deviceName = getIntent().getStringExtra("device");
        ApiService service = NetworkClient.getRetrofitClient().create(ApiService.class);
        Call<ArrayList<HistoryResponse>> call;
        call =service.getHistory(start,deviceName,end);
        call.enqueue(new Callback<ArrayList<HistoryResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<HistoryResponse>> call, Response<ArrayList<HistoryResponse>> response) {
                historyResponseArrayList = response.body();
                String time1="0",time2="0",modelStartTime = "0";

                for(int i=0;i<historyResponseArrayList.size();i++){
                    HistoryResponse model = historyResponseArrayList.get(i);
                    if(i==0){
                        time1 = model.getTime();
                        modelStartTime = time1;
                    }else {
                        time2 = model.getTime();
                        //Log.e("difference",time1+","+time2+","+String.valueOf(differenceTime(time1,time2)));
                        long difference = differenceTime(time1,time2);
                        if(difference>10){
                            ModelHistory modelHistory = new ModelHistory();
                            modelHistory.setStartTime(modelStartTime);
                            modelHistory.setEndTime(time1);
                            modelHistory.setDevice(deviceName);
                            modelHistories.add(modelHistory);
                            modelStartTime = time2;

                        }
                        time1 = model.getTime();

                    }

                    if(i==historyResponseArrayList.size()-1){
                        ModelHistory modelHistory = new ModelHistory();
                        modelHistory.setStartTime(modelStartTime);
                        modelHistory.setEndTime(time1);
                        modelHistory.setDevice(deviceName);
                        modelHistories.add(modelHistory);
                    }


                }
                //show list
                historyAdapter = new HistoryAdapter(HistoryActivity.this,modelHistories);
                recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                recyclerView.setAdapter(historyAdapter);
                showList();
                progressDialog.hide();




            }

            @Override
            public void onFailure(Call<ArrayList<HistoryResponse>> call, Throwable t) {
                progressDialog.hide();

            }
        });


    }
    void getAllHistory(){
        progressDialog.show();
        recyclerView.removeAllViews();
        modelHistories.clear();
        final String deviceName = getIntent().getStringExtra("device");
        ApiService service = NetworkClient.getRetrofitClient().create(ApiService.class);
        Call<ArrayList<HistoryResponse>> call;
        call =service.getAllHistory(deviceName);
        call.enqueue(new Callback<ArrayList<HistoryResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<HistoryResponse>> call, Response<ArrayList<HistoryResponse>> response) {
                historyResponseArrayList = response.body();
                String time1="0",time2="0",modelStartTime = "0";

                for(int i=0;i<historyResponseArrayList.size();i++){
                    HistoryResponse model = historyResponseArrayList.get(i);
                    if(i==0){
                        time1 = model.getTime();
                        modelStartTime = time1;
                    }else {
                        time2 = model.getTime();
                        //Log.e("difference",time1+","+time2+","+String.valueOf(differenceTime(time1,time2)));
                        long difference = differenceTime(time1,time2);
                        if(difference>10){
                            ModelHistory modelHistory = new ModelHistory();
                            modelHistory.setStartTime(modelStartTime);
                            modelHistory.setEndTime(time1);
                            modelHistory.setDevice(deviceName);
                            modelHistories.add(modelHistory);
                            modelStartTime = time2;

                        }
                        time1 = model.getTime();

                    }

                    if(i==historyResponseArrayList.size()-1){
                        ModelHistory modelHistory = new ModelHistory();
                        modelHistory.setStartTime(modelStartTime);
                        modelHistory.setEndTime(time1);
                        modelHistory.setDevice(deviceName);
                        modelHistories.add(modelHistory);
                    }


                }
                Collections.reverse(modelHistories);
                //show list
                historyAdapter = new HistoryAdapter(HistoryActivity.this,modelHistories);
                recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                recyclerView.setAdapter(historyAdapter);
                showList();
                progressDialog.hide();




            }

            @Override
            public void onFailure(Call<ArrayList<HistoryResponse>> call, Throwable t) {
                progressDialog.hide();

            }
        });


    }

    long differenceTime(String time1,String time2){
        long difference = 0;
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        Date date1,date2;
        try {
            date1 = inputFormat.parse(time1);
            date2 = inputFormat.parse(time2);
            long milisec1=date1.getTime();
            long milisec2 = date2.getTime();
            long min1 = (milisec1/1000)/60;
            long min2 = (milisec2/1000)/60;

            Log.e("time",String.valueOf(milisec1)+","+String.valueOf(milisec2));


            difference = Math.abs(min2-min1);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return  difference;
    }

    void showList(){
        scrollView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

    }
    void hideList(){
        scrollView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    void showHistoryOfADay(){
        cardSelectDate.setVisibility(View.VISIBLE);
        searchBtn.setVisibility(View.VISIBLE);
        showHisOfaDayBtn.setVisibility(View.GONE);
        showAllHisBtn.setVisibility(View.GONE);

    }
    void hideHistoryOfADay(){
        cardSelectDate.setVisibility(View.GONE);
        searchBtn.setVisibility(View.GONE);
        showHisOfaDayBtn.setVisibility(View.VISIBLE);
        showAllHisBtn.setVisibility(View.VISIBLE);

    }
}
