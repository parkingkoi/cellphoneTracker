package com.example.cellphonetracker.adapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cellphonetracker.ChangeNameActivity;
import com.example.cellphonetracker.HistoryActivity;
import com.example.cellphonetracker.Interfaces.ApiService;
import com.example.cellphonetracker.Model;
import com.example.cellphonetracker.R;
import com.example.cellphonetracker.serverCalls.NetworkClient;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.MyViewHolder> {

    Context context;
    ArrayList<Model> models;
    GoogleMap map;
    clickDevice listener;
    clickItem itemListener;

    public DeviceAdapter(Context context,ArrayList<Model> models,GoogleMap map,clickDevice listener,clickItem itemListener) {
        this.context = context;
        this.models = models;
        this.map = map;
        this.listener = listener;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_recycler,parent,false);
        return  new  MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final Model model = models.get(position);
        holder.name.setText(model.getDevice());
        if(model.getSelected().equals("1")){
            holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(model.getSelected().equals("1")){
                    de_selected(model.getDevice(),position);
                    listener.onSearchSelected(position,false);
                }else {
                    selected(model.getDevice(),position);
                    listener.onSearchSelected(position,true);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        FrameLayout button2;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.titleText);
            checkBox = itemView.findViewById(R.id.ck_box);
        itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemListener.onItemSelected(models.get(getAdapterPosition()));

//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setMessage("Are you  want to edit or see history?")
//                            .setCancelable(false)
//                            .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Model model = models.get(getAdapterPosition());
//                                    Intent intent = new Intent(context,ChangeNameActivity.class);
//                                    intent.putExtra("device",model.getDevice());
//                                    context.startActivity(intent);
//                                    dialog.cancel();
//
//                                }
//                            })
//
//                            .setNegativeButton("Show History", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    Model model = models.get(getAdapterPosition());
//                                    Intent intent = new Intent(context, HistoryActivity.class);
//                                    intent.putExtra("device",model.getDevice());
//                                    context.startActivity(intent);
//                                    dialog.cancel();
//
//                                }
//                            })
//                            .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//
//                                }
//                            });
//
//
//                    AlertDialog alert = builder.create();
//                    alert.show();

                }
            });


           // itemView.
        }


    }

    void  selected(String device, final int position){
        ApiService service = NetworkClient.getRetrofitClient().create(ApiService.class);
        Call<String> call;
        call = service.select(device);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {


                models.get(position).setSelected("1");
                notifyDataSetChanged();





            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("error",t.toString());
                Toast.makeText(context,"Can't select this ,maybe internet problem",Toast.LENGTH_SHORT).show();

            }
        });

    }

    void  de_selected(String device, final int position){
        ApiService service = NetworkClient.getRetrofitClient().create(ApiService.class);
        Call<String> call;
        call = service.de_select(device);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                models.get(position).setSelected("0");
                notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("error",t.toString());
                Toast.makeText(context,"Can't Deselect this ,maybe internet problem",Toast.LENGTH_SHORT).show();

            }
        });

    }


    public interface clickDevice{
        void onSearchSelected(int position,boolean isSelected);
    }

    public interface clickItem{
        void onItemSelected(Model model);
    }





}
