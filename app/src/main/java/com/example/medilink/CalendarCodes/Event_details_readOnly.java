package com.example.medilink.CalendarCodes;

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

public class Event_details_readOnly extends AppCompatActivity {


    TextView event_Name, eventTime_Start, eventTime_Finish, eventDescription, event_Date;
    String key ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_details_readonly);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("DEBUG", "onCreateView called");

        // Initialize views using the inflated layout
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

    }


}