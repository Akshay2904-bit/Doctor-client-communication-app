package com.example.medilink.CalendarCodes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medilink.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Event_details extends AppCompatActivity {


    TextView event_Name, eventTime_Start, eventTime_Finish, eventDescription, event_Date;
    Context context;
    FloatingActionButton delete_Button, edit_Button;
    String key ="";
    EventAdapter adapter;
    private List<EventData> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("DEBUG", "onCreateView called");

        // Initialize views using the inflated layout
        delete_Button = findViewById(R.id.delete_button);
        edit_Button = findViewById(R.id.Edit_button);
        event_Name = findViewById(R.id.Event_detail_name);
        eventTime_Start = findViewById(R.id.Event_detail_Start);
        eventTime_Finish = findViewById(R.id.Event_detail_End);
        eventDescription =  findViewById(R.id.Event_detail_Discription);
        event_Date = findViewById(R.id.Event_date);



        // Retrieve the passed data from Intent
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            // Set text fields from the bundle
            event_Name.setText(bundle.getString("NameEvent"));
            eventTime_Start.setText(bundle.getString("Start_Time"));
            eventTime_Finish.setText(bundle.getString("End_Time"));
            eventDescription.setText(bundle.getString("Description"));
            event_Date.setText(bundle.getString("Date"));
            key = bundle.getString("Key");

            //key = bundle.getString("Key");
        }


        edit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Event_details.this, UpdateEvents.class)
                        .putExtra("NameEvent", event_Name.getText().toString())
                        .putExtra("Start_Time", eventTime_Start.getText().toString())
                        .putExtra("End_Time", eventTime_Finish.getText().toString())
                        .putExtra("Description", eventDescription.getText().toString())
                        .putExtra("Date", event_Date.getText().toString())
                        .putExtra("Key",key);
                Log.d("DEBUG", "Event key: " + key );
                startActivity(intent);

                finish();
            }
        });

        delete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String userPhone = sharedPreferences.getString("USER_PHONE", null);

                key = bundle.getString("Key");

                if (userPhone != null && key != null && !key.isEmpty()) {
                    final DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference("UserList")
                            .child(userPhone) // Unique identifier for the user
                            .child("Events"); // Path to the events node

                    // Delete the event with the provided key
                    reference.child(key).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Event_details.this, "Event removed successfully", Toast.LENGTH_LONG).show();
                            finish(); // Close the current activity after deletion
                        } else {
                            Toast.makeText(Event_details.this, "Failed to remove event: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(Event_details.this, "Invalid key or user data. Cannot remove event.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


}