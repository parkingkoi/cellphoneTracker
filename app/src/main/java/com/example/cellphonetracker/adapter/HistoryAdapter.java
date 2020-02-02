package com.example.cellphonetracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cellphonetracker.MapsActivity;
import com.example.cellphonetracker.ModelHistory;
import com.example.cellphonetracker.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>{
    Context context;
    ArrayList<ModelHistory> arrayList;

    public HistoryAdapter(Context context, ArrayList<ModelHistory> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }





    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_history,parent,false);
        return  new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelHistory modelHistory = arrayList.get(position);
        holder.start.setText("Login Time: "+modelHistory.getStartTime());
        holder.end.setText("Logout Time: "+modelHistory.getEndTime());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView start,end;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            start = itemView.findViewById(R.id.login_time);
            end = itemView.findViewById(R.id.logout_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ModelHistory modelHistory = arrayList.get(getAdapterPosition());

                    Intent intent = new Intent(context, MapsActivity.class);
                    intent.putExtra("start",modelHistory.getStartTime());
                    intent.putExtra("end",modelHistory.getEndTime());
                    intent.putExtra("device",modelHistory.getDevice());
                    context.startActivity(intent);

                }
            });
        }
    }

}
