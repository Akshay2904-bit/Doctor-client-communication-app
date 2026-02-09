package com.example.medilink.CalendarCodes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.SearchView;

import com.example.medilink.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CalendarPatient extends Fragment {



    SearchView event_search;

    ImageButton add_Event;
    List<EventData> dataList;
    RecyclerView recyclerView;
    EventAdapter eventAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_patient, container, false);

        // Initialize add_Event
        add_Event = view.findViewById(R.id.addEvent);

        CalendarView calendarView = view.findViewById(R.id.calendarView);

        recyclerView = view.findViewById(R.id.recycler_view_for_events);
        recyclerView.setHasFixedSize(true);

        add_Event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an instance of the fragment
                Adding_Event addingEventFragment = new Adding_Event();

                // Use FragmentManager to replace the fragment
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.Calendar_event, addingEventFragment) // Replace with your container ID
                        .addToBackStack(null) // Optional: Add this transaction to the back stack
                        .commit();
            }


        });

        // Initialize the SearchView and CalendarView with correct IDs
        event_search = view.findViewById(R.id.Event_search); // Correct ID for SearchView
        event_search.clearFocus();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Format the selected date
                String selectedDate = dayOfMonth + " " + getMonthName(month) + " " + year;

                Log.d("Selected Date", selectedDate);

                // Filter Firebase data for the selected date
                filterEventsByDate(selectedDate);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1); // 1 column
        recyclerView.setLayoutManager(gridLayoutManager);



        // Initialize event list and adapter
        dataList = new ArrayList<>();
        eventAdapter = new EventAdapter(requireContext(), dataList);
        recyclerView.setAdapter(eventAdapter);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("USER_PHONE", null);
        if (userPhone != null) {
            // Toast.makeText(requireContext(), "User Phone: " + userPhone, Toast.LENGTH_SHORT).show();
        }


        // Firebase database reference
        DatabaseReference database_ref = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(userPhone)
                .child("Events"); // Unique identifier for the user


        // Attach the listener to the Firebase reference
        ValueEventListener eventListener = database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                handleDataUpdate(snapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log error and dismiss dialog
                Log.e("Firebase", "Database error: " + error.getMessage());
                //cardView.setVisibility(View.INVISIBLE);
                //progressBar.setVisibility(View.INVISIBLE);

            }
        });

        return view;
    }
    private String getMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month];
    }


    private void filterEventsByDate(String selectedDate) {
        DatabaseReference database_ref = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("USER_PHONE", null))
                .child("Events");

        database_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear(); // Clear old data

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    EventData eventData = itemSnapshot.getValue(EventData.class);

                    if (eventData != null) {
                        // Normalize event date and selected date
                        String firebaseEventDate = eventData.getEventDate(); // e.g., "19-Dec-2024"
                        String normalizedSelectedDate = selectedDate.replace(" ", "-"); // e.g., "19-Dec-2024"

                        if (firebaseEventDate.equals(normalizedSelectedDate)) {
                            dataList.add(eventData);
                        }
                    }
                }

                Log.d("EventAdapter", "Filtered Data list size: " + dataList.size());
                eventAdapter.notifyDataSetChanged(); // Refresh RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });
    }



    private void searchListClient(String selectedDate) {
        ArrayList<EventData> searchList = new ArrayList<>();

        for (EventData eventData : dataList) {
            if (eventData.getEventDate().equalsIgnoreCase(selectedDate)) {
                searchList.add(eventData);
            }
        }

        // Update the adapter with the filtered list
        eventAdapter.searchClientList(searchList);
    }


    private void handleDataUpdate(DataSnapshot snapshot) {


        // Check if new data is present in the snapshot
        if (snapshot.exists()) {
            // Log for debugging
            //Log.d("Firebase", "Snapshot exists, children count: " + snapshot.getChildrenCount());

            // Clear the list and update it only if new data is present
            dataList.clear();


            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                EventData eventData = itemSnapshot.getValue(EventData.class);

                if (eventData != null) {

                    // Correctly set the key
                    dataList.add(eventData); // Add the same object to the list

                } else {
                    Log.w("Firebase", "Null data found for snapshot: " + itemSnapshot.getKey());
                }
            }

            // Log for debugging
            Log.d("Firebase", "Data list updated, size: " + dataList.size());

            // Notify the adapter to update the RecyclerView
            eventAdapter.notifyDataSetChanged();
        } else {
            // Log if the snapshot doesn't have any data
            Log.d("Firebase", "Snapshot is empty.");
            dataList.clear();
            eventAdapter.notifyDataSetChanged();
        }
    }


}