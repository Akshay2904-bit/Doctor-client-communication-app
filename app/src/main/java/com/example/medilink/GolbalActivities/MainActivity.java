package com.example.medilink.GolbalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medilink.Doctorside.DoctorSide;
import com.example.medilink.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    Button enterMediLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        enterMediLink = findViewById(R.id.MediLink);

        enterMediLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AtomicReference<FirebaseAuth> auth = new AtomicReference<>(FirebaseAuth.getInstance());
                if (auth.get().getCurrentUser() != null) {
                    // User is already logged in
                    startActivity(new Intent(MainActivity.this, DoctorSide.class));
                    finish();
                }else {
                    startActivity(new Intent(MainActivity.this, LogInPage.class));
                    finish();

                }
            }
        });
    }

}