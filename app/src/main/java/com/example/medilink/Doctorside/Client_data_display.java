package com.example.medilink.Doctorside;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medilink.Patientside.Doctor_Details_Display;
import com.example.medilink.Patientside.calendar_readOnly;
import com.example.medilink.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Client_data_display extends AppCompatActivity {

    TextView detaildisease, detailname, detailage, detailgender, detailphone;
    ImageView detailimage;
    Context context;
    FloatingActionButton delete_Button, edit_Button;
    String key ="";
    MyAdapter adapter;
    String event_key;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_data_display);

        // Apply padding for system UI (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        detailname = findViewById(R.id.detailName);
        detaildisease = findViewById(R.id.detailDisease);
        detailimage = findViewById(R.id.detailImage);
        detailage = findViewById(R.id.detailAge);
        detailgender = findViewById(R.id.detailGender);
        detailphone = findViewById(R.id.detailPhone);
        delete_Button = findViewById(R.id.delete_button);
        edit_Button = findViewById(R.id.Edit_button);
        FloatingActionButton messageChatButton = findViewById(R.id.message_chat);
        FloatingActionButton calendar_button = findViewById(R.id.Calendar_button);




        // Retrieve the passed data from Intent
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            // Set text fields from the bundle
            detaildisease.setText(bundle.getString("Disease"));
            detailname.setText(bundle.getString("Name"));
            detailage.setText(bundle.getString("Age"));
            detailgender.setText(bundle.getString("Gender"));
            detailphone.setText(bundle.getString("PhoneNo"));


            event_key = bundle.getString("PhoneNo");
            key = bundle.getString("Key");

            String imagePath = getIntent().getStringExtra("Image");

            if (imagePath != null) {
                // Load the Bitmap from the file path
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                // Display the Bitmap in the ImageView
                detailimage.setImageBitmap(bitmap);

                //Log.d("DEBUG", "Received image path: " + imagePath);
            } else {
                //Log.d("DEBUG", "Image path is null");
            }
        } else {
            //Log.d("DEBUG", "Bundle is null");
        }

        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentCalendar = new Intent(Client_data_display.this, calendar_read_only_patients.class);
                intentCalendar.putExtra("KEY",event_key);
                startActivity(intentCalendar);

            }
        });


        // Set click listener for WhatsApp button
        messageChatButton.setOnClickListener(v -> {
            String clientPhone = detailphone.getText().toString(); // Get client's phone number

            if (clientPhone == null || clientPhone.isEmpty()) {
                Toast.makeText(Client_data_display.this, "Client phone number is missing", Toast.LENGTH_SHORT).show();
                return;
            }

            // Replace '+' if present in phone number with URL-encoded value for WhatsApp
            clientPhone = clientPhone.replace("+", "%2B");

            // Create WhatsApp intent
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
            whatsappIntent.setData(
                    Uri.parse("https://wa.me/" + clientPhone)); // WhatsApp URL scheme

            try {
                // Start the WhatsApp intent
                startActivity(whatsappIntent);
            } catch (Exception e) {
                // Handle the case where WhatsApp is not installed
                Toast.makeText(Client_data_display.this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
            }
        });
        String clientPhone = detailphone.getText().toString();
        delete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String userPhone = sharedPreferences.getString("USER_PHONE", null);
                //String clientPn = sharedPreferences.getString("CLIENT_PHONE", null);


                assert userPhone != null;
                //assert clientPn != null;
                DatabaseReference reference = FirebaseDatabase.getInstance()
                        .getReference("UserList")
                        .child(userPhone) // Unique identifier for the user
                        .child("Clients");

                DatabaseReference reference2 = FirebaseDatabase.getInstance()
                        .getReference("UserList")
                        .child(clientPhone) // Unique identifier for the user
                        .child("Doctors");

                if (key != null && !key.isEmpty()) {

                    reference.child(key).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Client_data_display.this, "Client removed from the list", Toast.LENGTH_LONG).show();

                            finish();
                        } else {
                            Toast.makeText(Client_data_display.this, "Failed to remove client: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    reference2.child(userPhone).removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Client_data_display.this, "Doctor removed from the list", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(Client_data_display.this, "Failed to remove doctor: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });



                } else {
                    Toast.makeText(Client_data_display.this, "Invalid key. Cannot remove client.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        edit_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String imagePath = getIntent().getStringExtra("Image");

                Intent intent = new Intent(Client_data_display.this, UpdateClient.class)
                    .putExtra("Name", detailname.getText().toString())
                    .putExtra("PhoneNo", detailphone.getText().toString())
                    .putExtra("Age", detailage.getText().toString())
                    .putExtra("Gender", detailgender.getText().toString())
                    .putExtra("Disease", detaildisease.getText().toString())
                    .putExtra("Key",key)
                        .putExtra("Image", imagePath);
                startActivity(intent);

                finish();
            }
        });


    }


}
