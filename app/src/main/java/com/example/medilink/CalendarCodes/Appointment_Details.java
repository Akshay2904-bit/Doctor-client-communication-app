package com.example.medilink.CalendarCodes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

public class Appointment_Details extends AppCompatActivity {


    TextView client_name, eventTime_Start, eventTime_Finish, client_phone, event_Date;
    Context context;
    FloatingActionButton delete_Button, edit_Button;
    String key ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("DEBUG", "onCreateView called");

        // Initialize views using the inflated layout
        delete_Button = findViewById(R.id.delete_button);
        edit_Button = findViewById(R.id.Edit_button);
        client_name = findViewById(R.id.client_name_detail);
        eventTime_Start = findViewById(R.id.Event_detail_Start);
        eventTime_Finish = findViewById(R.id.Event_detail_End);
        client_phone =  findViewById(R.id.client_phone_detail);
        event_Date = findViewById(R.id.appointment_date_detail);



        // Retrieve the passed data from Intent
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            // Set text fields from the bundle
            client_name.setText(bundle.getString("NameClient"));
            eventTime_Start.setText(bundle.getString("Start_Time"));
            eventTime_Finish.setText(bundle.getString("End_Time"));
            client_phone.setText(bundle.getString("Phone"));
            event_Date.setText(bundle.getString("Date"));
            key = bundle.getString("Key");

            //key = bundle.getString("Key");
        }


        edit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Appointment_Details.this, UpdateAppointment.class)
                        .putExtra("NameClient", client_name.getText().toString())
                        .putExtra("Start_Time", eventTime_Start.getText().toString())
                        .putExtra("End_Time", eventTime_Finish.getText().toString())
                        .putExtra("Phone", client_phone.getText().toString())
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
                            .child("Appointments"); // Path to the events node

                    // Delete the event with the provided key
                    reference.child(key).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Appointment_Details.this, "Event removed successfully", Toast.LENGTH_LONG).show();

                            // Now send the apology message via WhatsApp
                            String clientPhone = client_phone.getText().toString();
                            String patientName = client_name.getText().toString();
                            String dateTime = event_Date.getText().toString() + " " + eventTime_Start.getText().toString();
                            String apologyMessage = "Dear " + patientName + ",\n\n" +
                                    "I hope this message finds you well. I regret to inform you that due to unforeseen circumstances, I must cancel our upcoming appointment scheduled for " + dateTime + ". " +
                                    "I understand how important your appointment is, and I sincerely apologize for any inconvenience this may cause.\n\n" +
                                    "Please contact my office at your earliest convenience to reschedule your appointment. I am happy to find a new time that works best for you, and I am committed to providing you with the care you deserve.\n\n" +
                                    "Thank you for your understanding, and once again, I apologize for any disruption this has caused.\n\nBest regards";

                            // WhatsApp API URL Scheme to send the message
                            String url = "https://wa.me/" + clientPhone + "?text=" + Uri.encode(apologyMessage);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);

                            finish(); // Close the current activity after deletion
                        } else {
                            Toast.makeText(Appointment_Details.this, "Failed to remove event: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(Appointment_Details.this, "Invalid key or user data. Cannot remove event.", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }


}