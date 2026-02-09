package com.example.medilink.Doctorside;

import static com.example.medilink.Doctorside.MyAdapter.decodeBase64ToBitmap;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.medilink.Patientside.DoctorData;
import com.example.medilink.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yalantis.ucrop.UCrop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import android.Manifest;


public class AddingClient extends AppCompatActivity {

    CardView cardView;
    ProgressBar progressBar;
    ImageView client_Image;

    EditText Doc_name;

    EditText client_name;

    EditText client_age;
    EditText client_gender;
    EditText client_des;
    EditText client_phone;
    Button save_client;
    final int PICK_IMAGE_REQUEST = 1;
    String clientImage64;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    String userPhoto;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adding_client);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });


        client_Image = findViewById(R.id.clientPic);
        client_name = findViewById(R.id.clientNm);
        client_age = findViewById(R.id.clientAge);
        client_gender = findViewById(R.id.clientGender);
        client_des = findViewById(R.id.clientIssue);
        client_phone = findViewById(R.id.clientPN);
        save_client = findViewById(R.id.saveBtn);

        cardView = findViewById(R.id.cardView);
        progressBar = findViewById(R.id.progressBar);




        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();

                        if (selectedImageUri != null) {
                            startCrop(selectedImageUri); // Start cropping the selected image
                        }

                        client_Image.setImageURI(selectedImageUri); // Display the image
                        clientImage64 = ImageUtils.convertImageToBase64(selectedImageUri, AddingClient.this);
                        if (clientImage64 != null) {
                            //Toast.makeText(AddingClient.this, "Image has been converted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddingClient.this, "Image conversion failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );



        client_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddingClient.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request Camera Permission
                    ActivityCompat.requestPermissions(AddingClient.this,
                            new String[]{Manifest.permission.CAMERA}, 101);
                } else {
                    showImageSourceDialog();
                }
            }
        });








        save_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("USER_PHONE", null);
        String userName = sharedPreferences.getString("USER_NAME", null);

        FirebaseDatabase.getInstance().getReference("UserList")
                .child(userPhone)
                .child("UserDetails")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean userFound = false;

                        for (DataSnapshot userSnapshots : snapshot.getChildren()) {

                            String registeredUserPhoto = userSnapshots.child("user_registered_profile_photo").getValue(String.class);
                            username = userSnapshots.child("user_registered_Name").getValue(String.class);
                            if (registeredUserPhoto != null) {
                                userPhoto = registeredUserPhoto.trim().replaceAll("[^A-Za-z0-9+/=]", "");
                                Log.d("DEBUG", "onDataChange: not null");
                            } else {
                                Log.d("DEBUG", "onDataChange: null");
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AddingClient.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageSourceDialog();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                client_Image.setImageURI(resultUri); // Display the cropped image
                clientImage64 = ImageUtils.convertImageToBase64(resultUri, AddingClient.this);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }





    public void uploadData() {
        // Progress bar code
        progressBar.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);

        String clientName = client_name.getText().toString();
        String clientAge = client_age.getText().toString();
        String clientGender = client_gender.getText().toString();
        String clientDes = client_des.getText().toString();
        String clientPn = client_phone.getText().toString();
        String clientImg = clientImage64;
        String userphoto = userPhoto;

        DateFormat.getDateInstance().format(Calendar.getInstance().getTime());





        // Validate input
        if (clientName.isEmpty() || clientAge.isEmpty() || clientGender.isEmpty() ||
                clientDes.isEmpty() || clientPn.isEmpty() || clientImg.isEmpty()) {

            Toast.makeText(AddingClient.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
            return;
        }

        try {
            int age = Integer.parseInt(clientAge);
            if (age <= 0 || age > 120) {
                throw new NumberFormatException("Invalid age range");
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid age.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
            save_client.setEnabled(true);
            return;
        }

        if (!clientPn.matches("\\d{10}")) {
            Toast.makeText(this, "Please enter a valid 10-digit phone number.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
            save_client.setEnabled(true);
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("USER_PHONE", null);
        String userName = username;

        // Prepare client data
        ClientData client_data = new ClientData(clientImg, clientName, clientAge, clientGender, clientDes, clientPn);
        DoctorData doctorData = new DoctorData(userphoto, userName, userPhone);


        if (userPhone != null) {
            // Generate a unique key using Firebase's push()
            String uniqueKey = FirebaseDatabase.getInstance()
                    .getReference("UserList")
                    .child(userPhone)
                    .child("Clients")
                    .push() // This generates a unique key
                    .getKey();

            // Generate a unique key using Firebase's push()
            String  Doc_uniqueKey = FirebaseDatabase.getInstance()
                    .getReference("UserList")
                    .child(clientPn)
                    .child("Doctors")
                    .push() // This generates a unique key
                    .getKey();


            // Reference the specific user's Clients node
            assert uniqueKey != null;
            FirebaseDatabase.getInstance()
                    .getReference("UserList")
                    .child(userPhone) // Unique identifier for the user
                    .child("Clients")
                    .child(uniqueKey) // Unique identifier for the client
                    .setValue(client_data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(AddingClient.this, "Client added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddingClient.this, "Error occurred while uploading", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            cardView.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddingClient.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            cardView.setVisibility(View.INVISIBLE);
                        }
                    });




            assert Doc_uniqueKey != null;
            FirebaseDatabase.getInstance()
                    .getReference("UserList")
                    .child(clientPn) // Unique identifier for the user
                    .child("Doctors")
                    .child(userPhone) // Unique identifier for the client
                    .setValue(doctorData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddingClient.this, "Doctor added successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(AddingClient.this, "Error occurred while uploading", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            cardView.setVisibility(View.INVISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddingClient.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            cardView.setVisibility(View.INVISIBLE);
                        }
                    });
        }


    }

    public static class ImageUtils {

        public static String convertImageToBase64(Uri imageUri, Context context) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri));
                // Resize the image
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

        // uCrop configuration
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionQuality(80);
        options.setCircleDimmedLayer(true); // For circular cropping (optional)
        options.setShowCropGrid(true);
        options.setShowCropFrame(true);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.Lavendar));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.Lavendar));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.Lavendar));

        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1) // 1:1 aspect ratio (you can customize this)
                .withMaxResultSize(500, 500)
                .withOptions(options)
                .start(this);
    }
    // Function to Show Gallery/Camera Selection Dialog
    private void showImageSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddingClient.this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
            if (which == 0) {
                // Open gallery
                Intent photoPicker = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPicker.setType("image/*");
                imagePickerLauncher.launch(photoPicker);
            } else {


                // Open camera and save the captured image in the gallery
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    // Create a file URI to save the image
                    Uri imageUri = createImageUri();

                    // Set the output URI to save the captured image
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                    // Launch the camera intent
                    imagePickerLauncher.launch(cameraIntent);
                }
                Toast.makeText(AddingClient.this, "IMAGE will be saved to gallery", Toast.LENGTH_LONG).show();
            }

        });
        builder.show();
    }

    // Method to create a file to save the image in the gallery
    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Captured Image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Captured by Camera");
        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    }
};