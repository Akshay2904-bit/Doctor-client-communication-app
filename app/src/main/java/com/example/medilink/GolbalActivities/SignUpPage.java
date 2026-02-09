package com.example.medilink.GolbalActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medilink.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class SignUpPage extends AppCompatActivity {

    String profile_photo64;
    EditText edUsername, edPassword, edemail, edconfirmpswd, edPhoneno;
    Button fsignup;
    ImageView profile_photo;
    ImageButton img2;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    CardView cardView;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        img2 = findViewById(R.id.backbutt2);
        img2.setOnClickListener(view -> startActivity(new Intent(SignUpPage.this, StartupPage.class)));

        edUsername = findViewById(R.id.Username);
        edPassword = findViewById(R.id.passwrd);
        edemail = findViewById(R.id.emaild_signUp);
        fsignup = findViewById(R.id.fSignup);
        edconfirmpswd = findViewById(R.id.Conpswd);
        edPhoneno = findViewById(R.id.signUpPhone);
        profile_photo = findViewById(R.id.profile_photo_of_user);
        cardView = findViewById(R.id.cardView);
        progressBar = findViewById(R.id.progressBar);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            startCrop(selectedImageUri);
                            profile_photo64 = ImageUtils.convertImageToBase64(selectedImageUri, SignUpPage.this);
                            if (profile_photo64 == null) {
                                Toast.makeText(SignUpPage.this, "Failed to convert image to Base64. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        profile_photo.setOnClickListener(v -> {
            Intent photoPicker = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPicker.setType("image/*");
            imagePickerLauncher.launch(photoPicker);
        });

        fsignup.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.VISIBLE);

            String username = edUsername.getText().toString().trim();
            String email = edemail.getText().toString().trim();
            String password = edPassword.getText().toString().trim();
            String conPassword = edconfirmpswd.getText().toString().trim();
            String Phone_no = edPhoneno.getText().toString().trim();
            String Profile_pic = profile_photo64;

            if (!isValidEmail(email)) {
                edemail.setError("Please enter a valid email address.");
                hideProgressBar();
                return;
            }

            if (!isValidPassword(password)) {
                edPassword.setError("Password must be at least 8 characters, contain upper and lower case letters, a digit, and a special character.");
                hideProgressBar();
                return;
            }

            if (!password.equals(conPassword)) {
                edconfirmpswd.setError("Passwords do not match. Please confirm your password.");
                hideProgressBar();
                return;
            }

            if (username.isEmpty() || !isValidUsername(username)) {
                edUsername.setError("Username must be 3-16 characters long and can contain only letters, numbers, and underscores.");
                hideProgressBar();
                return;
            }

            if (Phone_no.isEmpty()) {
                edPhoneno.setError("Phone number cannot be empty.");
                hideProgressBar();
                return;
            }

            if (Profile_pic == null || Profile_pic.isEmpty()) {
                Toast.makeText(SignUpPage.this, "Please select a profile picture.", Toast.LENGTH_SHORT).show();
                hideProgressBar();
                return;
            }

            UserData userData = new UserData(Profile_pic, username, email, Phone_no);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference("UserList")
                                    .child(Phone_no).child("UserDetails").push().setValue(userData)
                                    .addOnCompleteListener(databaseTask -> {
                                        if (databaseTask.isSuccessful()) {
                                            Toast.makeText(SignUpPage.this, "User has been successfully registered", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(SignUpPage.this, "Failed to save user data. Please try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUpPage.this, "Failed to register. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        hideProgressBar();
                    });
        });
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                profile_photo.setImageURI(resultUri);
                profile_photo64 = ImageUtils.convertImageToBase64(resultUri, SignUpPage.this);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class ImageUtils {
        public static String convertImageToBase64(Uri imageUri, Context context) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri));
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                return Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void startCrop(@NonNull Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionQuality(80);
        options.setCircleDimmedLayer(true);
        options.setShowCropGrid(true);
        options.setShowCropFrame(true);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.Lavendar));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.Lavendar));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.Lavendar));

        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(500, 500)
                .withOptions(options)
                .start(this);
    }

    public static boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        boolean hasUppercase = false, hasLowercase = false, hasDigit = false, hasSpecialChar = false;
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUppercase = true;
            else if (Character.isLowerCase(ch)) hasLowercase = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else if (isSpecialCharacter(ch)) hasSpecialChar = true;
        }
        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }

    public static boolean isValidUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 16) return false;
        return username.matches("^[a-zA-Z0-9_]+$");
    }

    public static boolean isSpecialCharacter(char ch) {
        return "!@#$%^&*()-+_".contains(Character.toString(ch));
    }

    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
