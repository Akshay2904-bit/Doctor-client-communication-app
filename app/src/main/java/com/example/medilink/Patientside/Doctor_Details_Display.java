package com.example.medilink.Patientside;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.medilink.CalendarCodes.Calendar_fragment;
import com.example.medilink.CalendarCodes.Event_details;
import com.example.medilink.Doctorside.Client_data_display;
import com.example.medilink.R;
import com.github.clans.fab.FloatingActionButton;

import java.util.Calendar;

public class Doctor_Details_Display extends AppCompatActivity {

    TextView DOCTOR_Name;
    TextView DOCTOR_PHONE;
    ImageView DOCTOR_PFP;
    String DOCTOR_KEY;
    Button Feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_details_display);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DOCTOR_Name = findViewById(R.id.detailName);
        DOCTOR_PHONE = findViewById(R.id.detailPhone);
        DOCTOR_PFP = findViewById(R.id.detailImage);
        Feedback = findViewById(R.id.send_Feedback);
        FloatingActionButton messageChatButton = findViewById(R.id.message_doctor);
        FloatingActionButton calendar_button = findViewById(R.id.Calendar_button);

        // Retrieve the passed data from Intent
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            // Set text fields from the bundle
            DOCTOR_Name.setText(bundle.getString("NAME"));
            DOCTOR_PHONE.setText(bundle.getString("PHONE"));
            DOCTOR_KEY = bundle.getString("KEY");

            String imagePath = getIntent().getStringExtra("Image");

            if (imagePath != null) {
                // Load the Bitmap from the file path
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                // Display the Bitmap in the ImageView
                DOCTOR_PFP.setImageBitmap(bitmap);

                Log.d("DEBUG", "Received image path: " + imagePath);
            } else {
                Log.d("DEBUG", "Image path is null");
            }
        } else {
            Log.d("DEBUG", "Bundle is null");
        }


        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentCalendar = new Intent(Doctor_Details_Display.this, calendar_readOnly.class);
                intentCalendar.putExtra("KEY",DOCTOR_KEY);
                startActivity(intentCalendar);

            }
        });

        messageChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the DatePickerDialog
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Doctor_Details_Display.this, (view, year1, month1, dayOfMonth) -> {
                    // Date selected
                    String selectedDate = (month1 + 1) + "/" + dayOfMonth + "/" + year1;

                    // Open the TimePickerDialog
                    TimePickerDialog timePickerDialog = new TimePickerDialog(Doctor_Details_Display.this, (view1, hourOfDay, minute) -> {
                        // Time selected
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);

                        // Create the message
                        String message = "I hope this message finds you well. " +
                                "I would like to schedule an appointment with you on " + selectedDate +
                                " at " + selectedTime + ". Please let me know if this time works for you or if there's an alternative that suits your schedule better.\n\n" +
                                "Thank you, and I look forward to your confirmation.";

                        // Get doctor's phone number
                        String doctorPhone = DOCTOR_PHONE.getText().toString();

                        if (doctorPhone == null || doctorPhone.isEmpty()) {
                            Toast.makeText(Doctor_Details_Display.this, "Doctor's phone number is missing", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Replace '+' if present in phone number with URL-encoded value for WhatsApp
                        doctorPhone = doctorPhone.replace("+", "%2B");

                        // Construct WhatsApp intent
                        Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
                        whatsappIntent.setData(Uri.parse("https://wa.me/" + doctorPhone + "?text=" + Uri.encode(message)));

                        try {
                            // Start the WhatsApp intent
                            startActivity(whatsappIntent);
                        } catch (Exception e) {
                            // Handle the case where WhatsApp is not installed
                            Toast.makeText(Doctor_Details_Display.this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
                        }
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

                    timePickerDialog.show();
                }, year, month, day);

                datePickerDialog.show();
            }
        });




        // Set click listener for WhatsApp button
        Feedback.setOnClickListener(v -> {
            String Doc_Phone = DOCTOR_PHONE.getText().toString(); // Get client's phone number

            if (Doc_Phone == null || Doc_Phone.isEmpty()) {
                Toast.makeText(Doctor_Details_Display.this, "Client phone number is missing", Toast.LENGTH_SHORT).show();
                return;
            }

            // Replace '+' if present in phone number with URL-encoded value for WhatsApp
            Doc_Phone = Doc_Phone.replace("+", "%2B");

            // Create WhatsApp intent
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
            whatsappIntent.setData(
                    Uri.parse("https://wa.me/" + Doc_Phone)); // WhatsApp URL scheme

            try {
                // Start the WhatsApp intent
                startActivity(whatsappIntent);
            } catch (Exception e) {
                // Handle the case where WhatsApp is not installed
                Toast.makeText(Doctor_Details_Display.this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
            }
        });


    }
}