package com.example.medilink.GolbalActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medilink.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();

        TextView forgot_password;
        ImageButton img1;
        EditText edEmail, edPassword, edPhone ;
        Button fLog;
        img1 = findViewById(R.id.backbutt);

        img1.setOnClickListener(view -> startActivity(new Intent(LogInPage.this, StartupPage.class)));

        edEmail = findViewById(R.id.email);
        edPassword = findViewById(R.id.passwrd);
        fLog = findViewById(R.id.flongin);
        edPhone = findViewById(R.id.phone_no);
        forgot_password = findViewById(R.id.forgot_password_button_loginPage);

        forgot_password.setOnClickListener(v -> startActivity(new Intent(LogInPage.this, Forgot_password_page.class)));

        fLog.setOnClickListener(v -> {
            String email = edEmail.getText().toString().trim();
            String password = edPassword.getText().toString().trim();
            String phoneNo = edPhone.getText().toString().trim();

            if (phoneNo != null){
                Log.d("DEBUG", "phone no"+phoneNo);
            }
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Log in Unsuccessful", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase Authentication login
            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        // Login Successful
                        Toast.makeText(LogInPage.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Save user data in SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USER_EMAIL", email);
                        editor.putString("USER_PASS", password);
                        editor.putString("USER_PHONE",phoneNo);

                        editor.apply();

                        // Redirect to HomePage
                        startActivity(new Intent(LogInPage.this, HomePage.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Login Failed
                        Toast.makeText(LogInPage.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
