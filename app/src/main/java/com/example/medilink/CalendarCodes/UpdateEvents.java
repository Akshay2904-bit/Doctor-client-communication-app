package com.example.medilink.CalendarCodes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medilink.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UpdateEvents extends AppCompatActivity {

    TextView updateDISCRIPTION, updateEVENTNAME, updateSTART_TIME, updateEND_TIME, Update_DATE;
    Button Save_Update;
    String key = ""; // Key for the event
    DatabaseReference reference;
    private Calendar selectedDateTime;
    private String eventTime_from = "";
    private String eventTime_till = "";
    private String eventDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_events);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("USER_PHONE", null);

        reference = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(userPhone) // Unique identifier for the user
                .child("Events");

        Bundle bundle = getIntent().getExtras();

        Save_Update = findViewById(R.id.save_Updated_EventButton);
        updateDISCRIPTION = findViewById(R.id.Updated_Event_Description);
        updateEVENTNAME = findViewById(R.id.Updated_Event_Name);
        updateSTART_TIME = findViewById(R.id.Updated_Start);
        updateEND_TIME = findViewById(R.id.Updated_Finish);
        Update_DATE = findViewById(R.id.Updated_DateButton);
        selectedDateTime = Calendar.getInstance();

        if (bundle != null) {
            // Populate fields with event data
            updateDISCRIPTION.setText(bundle.getString("Description", ""));
            updateEVENTNAME.setText(bundle.getString("NameEvent", ""));
            updateSTART_TIME.setText(bundle.getString("Start_Time", ""));
            updateEND_TIME.setText(bundle.getString("End_Time", ""));
            Update_DATE.setText(bundle.getString("Date", ""));
            key = bundle.getString("Key", ""); // Retrieve the key
        }

        // Date Picker
        Update_DATE.setOnClickListener(v -> {
            int year = selectedDateTime.get(Calendar.YEAR);
            int month = selectedDateTime.get(Calendar.MONTH);
            int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    UpdateEvents.this,
                    (DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) -> {
                        selectedDateTime.set(selectedYear, selectedMonth, selectedDay);
                        eventDate = DateFormat.getDateInstance().format(selectedDateTime.getTime());
                        Update_DATE.setText(eventDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Time Picker for Start Time
        updateSTART_TIME.setOnClickListener(v -> {
            int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
            int minute = selectedDateTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    UpdateEvents.this,
                    (TimePicker timePicker, int selectedHour, int selectedMinute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        selectedDateTime.set(Calendar.MINUTE, selectedMinute);
                        eventTime_from = String.format("%02d:%02d", selectedHour, selectedMinute);
                        updateSTART_TIME.setText(eventTime_from);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });

        // Time Picker for End Time
        updateEND_TIME.setOnClickListener(v -> {
            int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
            int minute = selectedDateTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    UpdateEvents.this,
                    (TimePicker timePicker, int selectedHour, int selectedMinute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        selectedDateTime.set(Calendar.MINUTE, selectedMinute);
                        eventTime_till = String.format("%02d:%02d", selectedHour, selectedMinute);
                        updateEND_TIME.setText(eventTime_till);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });

        Save_Update.setOnClickListener(view -> uploadData());
    }

    public void uploadData() {
        String name = updateEVENTNAME.getText().toString();
        String description = updateDISCRIPTION.getText().toString();
        String date = Update_DATE.getText().toString();
        String startTime = updateSTART_TIME.getText().toString();
        String endTime = updateEND_TIME.getText().toString();

        // Check if fields are empty
        if (name.isEmpty() || description.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(UpdateEvents.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure key is not empty
        if (key == null || key.isEmpty()) {
            Toast.makeText(UpdateEvents.this, "Invalid event. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the user phone number from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("USER_PHONE", null);

        if (userPhone != null) {
            // Create an EventData object
            EventData eventData = new EventData(name, description,date, startTime, endTime);
            eventData.setKey(key);
            // Update event data in Firebase
            reference.child(key)
                    .setValue(eventData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateEvents.this, "Event updated successfully!", Toast.LENGTH_SHORT).show();
                            clearFields();
                            // Navigate back or finish the activity
                            finish();
                        } else {
                            Toast.makeText(UpdateEvents.this, "Error saving event", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void clearFields() {
        updateEVENTNAME.setText("");
        updateDISCRIPTION.setText("");
        Update_DATE.setText("Pick Date");
        updateSTART_TIME.setText("Pick Time from");
        updateEND_TIME.setText("Pick Time till");
    }
}
