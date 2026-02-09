package com.example.medilink.Patientside;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medilink.CalendarCodes.AppointmentAdapter;
import com.example.medilink.CalendarCodes.AppointmentAdapter_readOnly;
import com.example.medilink.CalendarCodes.AppointmentData;
import com.example.medilink.CalendarCodes.EventAdapter;
import com.example.medilink.CalendarCodes.EventAdapter_readOnly;
import com.example.medilink.CalendarCodes.EventData;
import com.example.medilink.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class calendar_readOnly extends AppCompatActivity {

    private static String DOCTOR_KEY = "";
    SearchView event_search;
    List<EventData> dataList;
    RecyclerView recyclerView;
    EventAdapter_readOnly eventAdapter;
    AppointmentAdapter_readOnly appointment_Adapter;
    List<AppointmentData> dataList_appointments;
    ConcatAdapter concatAdapter; // New variable for combining adapters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar_read_only);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize add_Event
        CalendarView calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        // Initialize the SearchView and CalendarView with correct IDs
        event_search = findViewById(R.id.Event_search);
        event_search.clearFocus();

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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(calendar_readOnly.this, 1); // 1 column
        recyclerView.setLayoutManager(gridLayoutManager);

        // Initialize event list and adapter
        dataList = new ArrayList<>();
        dataList_appointments = new ArrayList<>();

        eventAdapter = new EventAdapter_readOnly(calendar_readOnly.this, dataList);
        appointment_Adapter = new AppointmentAdapter_readOnly(calendar_readOnly.this, dataList_appointments);

        concatAdapter = new ConcatAdapter(eventAdapter, appointment_Adapter);
        recyclerView.setAdapter(concatAdapter);

        // Retrieve the passed data from Intent
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            DOCTOR_KEY = bundle.getString("KEY");
            Log.d("DEBUG", "KEY FOR CALENDAR: " + DOCTOR_KEY);
        }

        // Firebase database reference
        DatabaseReference database_ref = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(DOCTOR_KEY)
                .child("Events"); // Unique identifier for the user

        // Firebase database reference
        DatabaseReference database_ref_Appointments = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(DOCTOR_KEY)
                .child("Appointments");

        // Attach the listener to the Firebase reference
        database_ref.addValueEventListener(new ValueEventListener() {
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
        database_ref_Appointments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                handleDataUpdate_2(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });
    }

    private String getMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return months[month];
    }

    private void filterEventsByDate(String selectedDate) {
        DatabaseReference database_ref = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(DOCTOR_KEY)
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
                .child(DOCTOR_KEY)
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
            if (snapshot.exists()) {
                dataList.clear(); // Clear old data

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    EventData eventData = itemSnapshot.getValue(EventData.class);

                    if (eventData != null) {
                        dataList.add(eventData); // Add new event data
                    } else {
                        Log.w("Firebase", "Null data found for snapshot: " + itemSnapshot.getKey());
                    }
                }

                Log.d("Firebase", "Data list updated, size: " + dataList.size());
                eventAdapter.notifyDataSetChanged(); // Update the RecyclerView
            } else {
                Log.d("Firebase", "Snapshot is empty.");
                dataList.clear();
                eventAdapter.notifyDataSetChanged(); // Refresh with empty list
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

