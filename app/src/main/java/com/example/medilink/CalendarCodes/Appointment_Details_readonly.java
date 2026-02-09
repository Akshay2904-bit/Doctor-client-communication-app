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

public class Appointment_Details_readonly extends AppCompatActivity {


    TextView client_name, eventTime_Start, eventTime_Finish, client_phone, event_Date;
    Context context;
    FloatingActionButton delete_Button, edit_Button;
    String key ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_details_readonly);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("DEBUG", "onCreateView called");

        // Initialize views using the inflated layout
        client_name = findViewById(R.id.client_name_detail);
        eventTime_Start = findViewById(R.id.Event_detail_Start);
        eventTime_Finish = findViewById(R.id.Event_detail_End);
        event_Date = findViewById(R.id.appointment_date_detail);

    }


}