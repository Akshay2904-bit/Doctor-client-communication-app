package com.example.medilink.Doctorside;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
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
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;

public class UpdateClient extends AppCompatActivity {


    CardView cardView;
    ProgressBar progressBar;
    TextView updateDISEASE, updateNAME, updateAGE, updateGENDER, updatePHONE;
    ImageView updateIMAGE;
    Button Save_Update;
    String key ="";
    String clientImage64;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    DatabaseReference reference;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_client);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("USER_PHONE", null);

        reference = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(userPhone) // Unique identifier for the user
                .child("Clients");

        Bundle bundle = getIntent().getExtras();

        Save_Update=findViewById(R.id.Save_updates);
        updateIMAGE=findViewById(R.id.Updated_client_pic);
        updateNAME=findViewById(R.id.Updated_client_name);
        updateAGE=findViewById(R.id.Updated_client_age);
        updateGENDER=findViewById(R.id.Updated_client_gender);
        updateDISEASE=findViewById(R.id.Updated_client_issue);
        updatePHONE=findViewById(R.id.Updated_client_phoneNo);

        cardView = findViewById(R.id.cardView);
        progressBar = findViewById(R.id.progressBar);

        if (bundle != null) {
            // Set text fields from the bundle
            updateDISEASE.setText(bundle.getString("Disease"));
            updateNAME.setText(bundle.getString("Name"));
            updateAGE.setText(bundle.getString("Age"));
            updateGENDER.setText(bundle.getString("Gender"));
            updatePHONE.setText(bundle.getString("PhoneNo"));
            //String Image64 = getIntent().getStringExtra("ImageBase64");

            key = bundle.getString("Key");



            String imagePath = getIntent().getStringExtra("Image");
            Log.d("DEBUG", "Received image path: " + imagePath);

            if (imagePath != null) {

                // Load the Bitmap from the file path
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

                // Display the Bitmap in the ImageView
                updateIMAGE.setImageBitmap(bitmap);


            } else {
                Log.d("DEBUG", "Image path is null");
            }
        }


        updateIMAGE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPicker.setType("image/*");
                imagePickerLauncher.launch(photoPicker);

            }
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();

                        if (selectedImageUri != null) {
                            startCrop(selectedImageUri); // Start cropping the selected image
                        }

                        updateIMAGE.setImageURI(selectedImageUri); // Display the image
                        clientImage64 = AddingClient.ImageUtils.convertImageToBase64(selectedImageUri, UpdateClient.this);
                        if (clientImage64 != null) {
                            //Toast.makeText(AddingClient.this, "Image has been converted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UpdateClient.this, "Image conversion failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );


        Save_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                updateIMAGE.setImageURI(resultUri); // Display the cropped image
                clientImage64 = AddingClient.ImageUtils.convertImageToBase64(resultUri, UpdateClient.this);
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



    public void uploadData() {
        // Show progress bar and card view
        progressBar.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);

        String clientName = updateNAME.getText().toString();
        String clientAge = updateAGE.getText().toString();
        String clientGender = updateGENDER.getText().toString();
        String clientDes = updateDISEASE.getText().toString();
        String clientPn = updatePHONE.getText().toString();

        // Check if clientImage64 is null or empty
        if (clientImage64 == null || clientImage64.isEmpty()) {
            try {
                // Get the drawable from the ImageView
                BitmapDrawable drawable = (BitmapDrawable) updateIMAGE.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();

                    // Convert the Bitmap to Base64
                    Uri tempUri = getImageUriFromBitmap(bitmap);
                    clientImage64 = ImageUtils.convertImageToBase64(tempUri, this);
                } else {
                    throw new Exception("Image drawable is null");
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error converting existing image to Base64", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                cardView.setVisibility(View.INVISIBLE);
                return;
            }
        }

        // Validate input
        if (clientName.isEmpty() || clientAge.isEmpty() || clientGender.isEmpty() ||
                clientDes.isEmpty() || clientPn.isEmpty() || clientImage64 == null || clientImage64.isEmpty()) {
            Toast.makeText(UpdateClient.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
            return;
        }

        // Prepare client data
        ClientData client_data = new ClientData(clientImage64, clientName, clientAge, clientGender, clientDes, clientPn);

        if (key != null && !key.isEmpty()) {
                    reference
                    .child(key)// Unique identifier for the client
                    .setValue(client_data)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateClient.this, "Client updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UpdateClient.this, "Error occurred while uploading", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        cardView.setVisibility(View.INVISIBLE);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(UpdateClient.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        cardView.setVisibility(View.INVISIBLE);
                    });
        } else {
            Toast.makeText(UpdateClient.this, "Invalid key. Cannot update client.", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Converts a Bitmap to a temporary URI.
     */
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Temporary Image", null);
        return Uri.parse(path);
    }






}
