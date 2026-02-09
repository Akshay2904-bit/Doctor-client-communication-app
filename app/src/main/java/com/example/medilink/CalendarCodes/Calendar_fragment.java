package com.example.medilink.CalendarCodes;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medilink.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.recyclerview.widget.ConcatAdapter;

import java.util.ArrayList;
import java.util.List;

public class Calendar_fragment extends Fragment {


    SearchView client_search;
    ImageButton add_Event, add_appointment;
    RecyclerView recyclerView;
    List<EventData> dataList;
    EventAdapter eventAdapter;
    AppointmentAdapter appointment_Adapter;
    List<AppointmentData> dataList_appointments;
    ConcatAdapter concatAdapter; // New variable for combining adapters






    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_fragment, container, false);

        // Initialize add_Event
        add_Event = view.findViewById(R.id.addEvent);
        add_appointment = view.findViewById(R.id.set_appointment);

        CalendarView calendarView = view.findViewById(R.id.calendarView);

        recyclerView = view.findViewById(R.id.recycler_view);
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

        add_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an instance of the fragment
                Adding_Appointment addingAppointmentFragment = new Adding_Appointment();

                // Use FragmentManager to replace the fragment
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.Calendar_event, addingAppointmentFragment) // Replace with your container ID
                        .addToBackStack(null) // Optional: Add this transaction to the back stack
                        .commit();
            }


        });

        // Initialize the SearchView and CalendarView with correct IDs
        client_search = view.findViewById(R.id.client_search); // Correct ID for SearchView
        client_search.clearFocus();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Format the selected date
                String selectedDate = dayOfMonth + " " + getMonthName(month) + " " + year;

                Log.d("Selected Date", selectedDate);

                // Filter Firebase data for the selected date
                filterEventsByDate(selectedDate);
                filterAppointmentsByDate(selectedDate);

            }
        });




        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1); // 1 column
        recyclerView.setLayoutManager(gridLayoutManager);



        // Initialize event list and adapter
        dataList = new ArrayList<>();
        dataList_appointments = new ArrayList<>();

        eventAdapter = new EventAdapter(requireContext(), dataList);
        appointment_Adapter = new AppointmentAdapter(requireContext(), dataList_appointments);

        concatAdapter = new ConcatAdapter(eventAdapter, appointment_Adapter);
        recyclerView.setAdapter(concatAdapter);

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

        DatabaseReference database_ref_Appointments = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(userPhone)
                .child("Appointments");


        // Attach the listener to the Firebase reference
        ValueEventListener eventListener = database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                handleDataUpdate(snapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("Firebase", "Database error: " + error.getMessage());


            }
        });
        // Attach the listener to the Firebase reference
        ValueEventListener eventListener_2 = database_ref_Appointments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                handleDataUpdate_2(snapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log error and dismiss dialog
                Log.e("Firebase", "Database error: " + error.getMessage());


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

                Log.d("EventAdapter", "Snapshot size: " + snapshot.getChildrenCount());

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    EventData eventData = itemSnapshot.getValue(EventData.class);

                    if (eventData != null) {
                        String firebaseEventDate = eventData.getEventDate(); // e.g., "19-Dec-2024"
                        String normalizedSelectedDate = normalizeDate(selectedDate.replace(" ", "-")); // Normalize the selected date

                        Log.d("EventAdapter", "Firebase Event Date: " + firebaseEventDate);
                        Log.d("EventAdapter", "Normalized Selected Date: " + normalizedSelectedDate);

                        // Check if the event date matches the selected date
                        if (normalizeDate(firebaseEventDate).equals(normalizedSelectedDate)) {
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
    private String normalizeDate(String date) {
        // Split the date into day, month, and year parts
        String[] dateParts = date.split("-");

        // Add leading zero to the day if necessary
        String day = dateParts[0].length() == 1 ? "0" + dateParts[0] : dateParts[0];
        String month = dateParts[1]; // Month stays as is (e.g., "Jan")
        String year = dateParts[2]; // Year stays as is (e.g., "2025")

        // Return the formatted date
        return day + "-" + month + "-" + year;
    }

    private void filterAppointmentsByDate(String selectedDate) {
        DatabaseReference database_ref = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("USER_PHONE", null))
                .child("Appointments");

        database_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList_appointments.clear(); // Clear old data

                Log.d("AppointmentAdapter", "Appointments Snapshot size: " + snapshot.getChildrenCount());

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    AppointmentData appointmentData = itemSnapshot.getValue(AppointmentData.class);

                    if (appointmentData != null) {
                        String firebaseAppointmentDate = appointmentData.getEventDate();
                        String normalizedSelectedDate = normalizeDate(selectedDate.replace(" ", "-")); // Normalize the selected date

                        Log.d("AppointmentAdapter", "Firebase Appointment Date: " + firebaseAppointmentDate);
                        Log.d("AppointmentAdapter", "Normalized Selected Date: " + normalizedSelectedDate);

                        // Check if the appointment date matches the selected date
                        if (normalizeDate(firebaseAppointmentDate).equals(normalizedSelectedDate)) {
                            dataList_appointments.add(appointmentData);
                        }
                    } else {
                        Log.w("AppointmentAdapter", "Null data found for snapshot: " + itemSnapshot.getKey());
                    }
                }

                Log.d("AppointmentAdapter", "Filtered Appointment Data list size: " + dataList_appointments.size());
                appointment_Adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });
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

    private void handleDataUpdate_2(DataSnapshot snapshot) {


        // Check if new data is present in the snapshot
        if (snapshot.exists()) {
            // Log for debugging
            //Log.d("Firebase", "Snapshot exists, children count: " + snapshot.getChildrenCount());

            // Clear the list and update it only if new data is present
            dataList_appointments.clear();


            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                AppointmentData appointmentData = itemSnapshot.getValue(AppointmentData.class);

                if (appointmentData != null) {

                    // Correctly set the key
                    dataList_appointments.add(appointmentData); // Add the same object to the list

                } else {
                    Log.w("Firebase", "Null data found for snapshot: " + itemSnapshot.getKey());
                }
            }

            // Log for debugging
            Log.d("Firebase", "Data list updated, size: " + dataList.size());

            // Notify the adapter to update the RecyclerView
            appointment_Adapter.notifyDataSetChanged();
        } else {
            // Log if the snapshot doesn't have any data
            Log.d("Firebase", "Snapshot is empty.");
            dataList_appointments.clear();
            appointment_Adapter.notifyDataSetChanged();
        }
    }



}


