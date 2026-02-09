package com.example.medilink.CalendarCodes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medilink.R;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
    private Context context;
    private List<AppointmentData> dataList;
    private FragmentManager fragmentManager;

    public AppointmentAdapter(Context context, List<AppointmentData> dataList) {
        this.dataList = dataList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_file_appointment_card, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentData data = dataList.get(position);

        holder.appointmentName.setText(data.getClientName());
        holder.appointmentStartTime.setText(data.getEventTime_Start());
        holder.appointmentEndTime.setText(data.getEventTime_Finish());

        Log.d("DEBUG", "Appointment in List: " + data.getClientName() + ", " + data.getClient_phone());

        holder.appointmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUG", "CLICKED");

                Intent intentAppointment = new Intent(context, Appointment_Details.class);

                Bundle bundle = new Bundle();
                intentAppointment.putExtra("NameClient", data.getClientName());
                intentAppointment.putExtra("Start_Time", data.getEventTime_Start());
                intentAppointment.putExtra("End_Time", data.getEventTime_Finish());
                intentAppointment.putExtra("Date", data.getEventDate());
                intentAppointment.putExtra("Phone", data.getClient_phone());
                intentAppointment.putExtra("Key", data.getKey());

                Log.d("DEBUG", "Appointment key: " + data.getKey());

                context.startActivity(intentAppointment);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d("AppointmentAdapter", "Data list size: " + dataList.size());
        return dataList.size();
    }

    public void searchClientList(ArrayList<AppointmentData> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView appointmentName;
        TextView appointmentStartTime;
        TextView appointmentEndTime;
        CardView appointmentCard;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentName = itemView.findViewById(R.id.rec_client_name);
            appointmentStartTime = itemView.findViewById(R.id.rec_appo_time_Start);
            appointmentEndTime = itemView.findViewById(R.id.rec_appo_time_End);
            appointmentCard = itemView.findViewById(R.id.recycler_Appointment_card);
        }
    }
}
