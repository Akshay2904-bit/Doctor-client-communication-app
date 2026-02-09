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

public class EventAdapter_readOnly extends RecyclerView.Adapter<EventAdapter_readOnly.EventViewHolder> {
    private Context context;
    private  List<EventData> dataList;
    private FragmentManager fragmentManager;




    public EventAdapter_readOnly(Context context, List<EventData> dataList) {
        this.dataList = dataList;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }



    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_file_event_card, parent, false);

        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventData data = dataList.get(position);

        holder.Event_name_on_holder.setText(data.getEventName());
        holder.Event_Start_time_on_holder.setText(data.getEventTime_Start());
        holder.Event_End_time_on_holder.setText(data.getEventTime_Finish());

        Log.d("DEBUG", "Event in List: " + data.getEventName() + ", " + data.getEventDate());

        holder.EventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("DEBUG", "CLICKED");

                Intent intentEvent = new Intent(context, Event_details_readOnly.class);


                Bundle bundle = new Bundle();

                intentEvent.putExtra("NameEvent", data.getEventName());
                intentEvent.putExtra("Start_Time", data.getEventTime_Start());
                intentEvent.putExtra("End_Time", data.getEventTime_Finish());
                intentEvent.putExtra("Description", data.getEventDescription());
                intentEvent.putExtra("Date",data.getEventDate());
                intentEvent.putExtra("Key",data.getKey());

                Log.d("DEBUG", "Event key: " + data.getKey() );

                // Start the new activity
                context.startActivity(intentEvent);


            }

        });

    }

    @Override
    public int getItemCount() {
        Log.d("EventAdapter", "Data list size: " + dataList.size());

        return dataList.size();

    }

    public void searchClientList(ArrayList<EventData> searchList){

        dataList = searchList;
        notifyDataSetChanged();

    }

     class EventViewHolder extends RecyclerView.ViewHolder {

        TextView Event_name_on_holder;
        TextView Event_Start_time_on_holder;
        TextView Event_End_time_on_holder;
        CardView EventCard;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the views from the layout using findViewById
            Event_name_on_holder = itemView.findViewById(R.id.rec_Event_name);
            Event_Start_time_on_holder = itemView.findViewById(R.id.rec_Event_time_Start);
            Event_End_time_on_holder = itemView.findViewById(R.id.rec_Event_time_End);
            EventCard = itemView.findViewById(R.id.recycler_Event_card);
        }




    }


}
