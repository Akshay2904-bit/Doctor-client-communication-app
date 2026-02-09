package com.example.medilink.GolbalActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medilink.R;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_password_page extends AppCompatActivity {

    private EditText Email_forgot_password;
    private Button Send_reset_password_link;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView back_to_login_page;

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        back_to_login_page = findViewById(R.id.backToLoginTextView);
        Email_forgot_password = findViewById(R.id.emailEditText_forgot_password);
        Send_reset_password_link = findViewById(R.id.resetPasswordButton_forgot_password);

        back_to_login_page.setOnClickListener(v -> startActivity(new Intent(Forgot_password_page.this, LogInPage.class)));

        Send_reset_password_link.setOnClickListener(v -> {
            String email = Email_forgot_password.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Email_forgot_password.setError("Email is required");
                Email_forgot_password.requestFocus();
                return;
            }

            // Send password reset email
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Forgot_password_page.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error occurred";
                    Toast.makeText(Forgot_password_page.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
