package com.celes.garbogo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterWorkerRV extends RecyclerView.Adapter<AdapterWorkerRV.MyViewHolder> {
    Context context;
    ArrayList<Complaint> list;

    public AdapterWorkerRV(Context context, ArrayList<Complaint> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycle_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Complaint complaint = list.get(position);
        holder.sub.setText(complaint.subject);
        String st = complaint.status;
        holder.stat.setText(st);
        if(st.equals("completed")) holder.stat.setTextColor(Color.RED);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView sub, stat;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sub = itemView.findViewById(R.id.subRV);
            stat = itemView.findViewById(R.id.statusRV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Complaint complaint = list.get(getAdapterPosition());
                    String complaintID=complaint.compID;
                    Intent intent = new Intent(v.getContext(),workerViewComplaint.class);
                    intent.putExtra("comID",complaintID);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

}
