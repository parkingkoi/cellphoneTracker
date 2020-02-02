package com.example.cellphonetracker.Interfaces;

import com.example.cellphonetracker.HistoryResponse;
import com.example.cellphonetracker.Model;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("add_data.php")
    Call<String> addData(@Field("device_id") String device_id,
                         @Field("noice_value") String value,
                         @Field("location") String location,
                         @Field("lat") String lat,
                         @Field("lng") String lng);

    @FormUrlEncoded
    @POST("add-trackerdata.php")
    Call<String> addData(@Field("device_id") String device_id,
                         @Field("lat") String lat,
                         @Field("lng") String lng);

    @GET("get-all-name.php")
    Call<ArrayList<Model>> getAllUsers();

    @FormUrlEncoded
    @POST("change-name.php")
    Call<String> updateName(@Field("name") String name,@Field("device_id") String device);

    @FormUrlEncoded
    @POST("get-history.php")
    Call<ArrayList<HistoryResponse>> getHistory(@Field("s_time") String s_time,
                                                @Field("name") String name,
                                                @Field("e_time") String e_time);

    @FormUrlEncoded
    @POST("get-all-history.php")
    Call<ArrayList<HistoryResponse>> getAllHistory(@Field("name") String name);

    @FormUrlEncoded
    @POST("selected.php")
    Call<String> select(@Field("device_id") String device);

    @FormUrlEncoded
    @POST("de-selected.php")
    Call<String> de_select(@Field("device_id") String device);




}
