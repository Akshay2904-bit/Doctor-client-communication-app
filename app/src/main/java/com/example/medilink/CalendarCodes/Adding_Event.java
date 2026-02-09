package com.example.medilink.CalendarCodes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.medilink.Doctorside.ClientData;
import com.example.medilink.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;



public class Adding_Event extends Fragment {
    private EditText eventName, eventDescription;
    private Button pickDate, pickTime_from, pickTime_till, saveEvent;
    //private ImageView eventImage;
    private Calendar selectedDateTime;
    private String eventTime_from = "";
    private String eventTime_till = "";
    private String eventDate = "";

    private HashMap<String, Boolean> eventDates;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adding_event, container, false);

        eventDates = new HashMap<>();

        eventName = view.findViewById(R.id.eventName);
        eventDescription = view.findViewById(R.id.eventDescription);
        pickDate = view.findViewById(R.id.pickDateButton);
        pickTime_from = view.findViewById(R.id.pickTimeButton1);
        pickTime_till = view.findViewById(R.id.pickTimeButton2);
        saveEvent = view.findViewById(R.id.saveEventButton);
        //eventImage = view.findViewById(R.id.eventImage);

        selectedDateTime = Calendar.getInstance();




        // Date Picker
        pickDate.setOnClickListener(v -> {
            int year = selectedDateTime.get(Calendar.YEAR);
            int month = selectedDateTime.get(Calendar.MONTH);
            int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) -> {
                        selectedDateTime.set(selectedYear, selectedMonth, selectedDay);
                        eventDate = DateFormat.getDateInstance().format(selectedDateTime.getTime());
                        pickDate.setText(eventDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Time Picker
        pickTime_from.setOnClickListener(v -> {
            int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
            int minute = selectedDateTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (TimePicker timePicker, int selectedHour, int selectedMinute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        selectedDateTime.set(Calendar.MINUTE, selectedMinute);
                        eventTime_from = String.format("%02d:%02d", selectedHour, selectedMinute);
                        pickTime_from.setText(eventTime_from);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });

        pickTime_till.setOnClickListener(v -> {
            int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
            int minute = selectedDateTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (TimePicker timePicker, int selectedHour, int selectedMinute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        selectedDateTime.set(Calendar.MINUTE, selectedMinute);
                        eventTime_till = String.format("%02d:%02d", selectedHour, selectedMinute);
                        pickTime_till.setText(eventTime_till);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });

        // Save Event
        saveEvent.setOnClickListener(v -> saveEventToFirebase());

        return view;
    }



    private void saveEventToFirebase() {
        String name = eventName.getText().toString();
        String description = eventDescription.getText().toString();

        if (name.isEmpty() || description.isEmpty() || eventDate.isEmpty() || eventTime_from.isEmpty() || eventTime_till.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", requireContext().MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("USER_PHONE", null);

        if (userPhone != null) {
            String uniqueKey = FirebaseDatabase.getInstance()
                    .getReference("UserList")
                    .child(userPhone)
                    .child("Events")
                    .push()
                    .getKey();

            // Prepare client data
            EventData eventData = new EventData(name, description, eventDate, eventTime_from, eventTime_till);
            eventData.setKey(uniqueKey);  // Set the key here

            FirebaseDatabase.getInstance()
                    .getReference("UserList")
                    .child(userPhone)
                    .child("Events")
                    .child(uniqueKey)
                    .setValue(eventData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Event saved successfully!", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(requireContext(), "Error saving event", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }



    private void clearFields() {
        eventName.setText("");
        eventDescription.setText("");
        pickDate.setText("Pick Date");
        pickTime_from.setText("Pick Time from");
        pickTime_till.setText("Pick Time till");
    }
}