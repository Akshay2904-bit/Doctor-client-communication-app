package com.example.medilink.Patientside;

import static android.app.Activity.RESULT_OK;
import static android.app.ProgressDialog.show;
import static android.content.Context.MODE_PRIVATE;
import static com.example.medilink.Doctorside.MyAdapter.decodeBase64ToBitmap;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medilink.GolbalActivities.LogInPage;
import com.example.medilink.GolbalActivities.SignUpPage;
import com.example.medilink.GolbalActivities.UserData;
import com.example.medilink.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;


public class ProfilePage extends Fragment {

    EditText User_name;
    String User_password;
    EditText User_email;
    EditText User_phone_no;
    TextView User;
    ImageView User_profile_pic;
    Button Save_updates;
    String User_profile_pic64;
    FloatingActionButton pic_image;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    UserData data = new UserData();
    String base64ImageString;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        User_name = view.findViewById(R.id.Updated_profile_username);
        User_email = view.findViewById(R.id.Update_profile_email);
        User_phone_no = view.findViewById(R.id.profile_phone);
        User_profile_pic = view.findViewById(R.id.User_profile);
        Save_updates = view.findViewById(R.id.edit_profile);
        User = view.findViewById(R.id.profile_name);
        pic_image = view.findViewById(R.id.Updated_User_pic);

        // Access SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String UserName = sharedPreferences.getString("USER_NAME", null);
        String UserEmail = sharedPreferences.getString("USER_EMAIL", null);
        String UserPhone_no = sharedPreferences.getString("USER_PHONE", null);
        String UserPassword = sharedPreferences.getString("USER_PASS", null);


        User_name.setText(UserName);
        User_email.setText(UserEmail);
        User_phone_no.setText(UserPhone_no);
        User_password = UserPassword;
        User.setText(UserName);

        FirebaseDatabase.getInstance().getReference("UserList")
                .child(UserPhone_no)
                .child("UserDetails")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean userFound = false;

                        for (DataSnapshot userSnapshots : snapshot.getChildren()) {

                            String registeredUserPhoto = userSnapshots.child("user_registered_profile_photo").getValue(String.class);
                            if (registeredUserPhoto != null) {
                                base64ImageString = registeredUserPhoto.trim().replaceAll("[^A-Za-z0-9+/=]", "");
                                Bitmap bitmap = decodeBase64ToBitmap(base64ImageString);
                                User_profile_pic.setImageBitmap(bitmap);
                                Log.d("DEBUG", "onDataChange: not null");
                            } else {
                                Log.d("DEBUG", "onDataChange: null");
                            }

                            String registeredUserName = userSnapshots.child("user_registered_Name").getValue(String.class);
                            if (registeredUserName != null) {
                                User_name.setText(registeredUserName);
                                Log.d("DEBUG", "registeredUserName: not null");
                            } else {
                                Log.d("DEBUG", "registeredUserName: null");
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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

                        User_profile_pic.setImageURI(selectedImageUri); // Display the image
                        User_profile_pic64 = SignUpPage.ImageUtils.convertImageToBase64(selectedImageUri, getContext());
                        if (User_profile_pic64 != null) {
                            //Toast.makeText(AddingClient.this, "Image has been converted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Image conversion failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );


        pic_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPicker.setType("image/*");
                imagePickerLauncher.launch(photoPicker);
            }
        });

        Save_updates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String username = User_name.getText().toString().trim();
                String email = User_email.getText().toString().trim();
                String password = User_password.trim();
                String Phone_no = User_phone_no.getText().toString().trim();
                String Profile_pic;
                if(User_profile_pic64 != null ){
                    Profile_pic = User_profile_pic64;
                }else{
                    Profile_pic = base64ImageString;
                }


                if(isValidUsername(username)) {


                    FirebaseDatabase.getInstance().getReference("UserList")
                            .child(Phone_no)// Accessing the first-level key (Phone number)
                            .child("UserDetails")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // Loop through the children under the phone number
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        // The second-level key (unique key) is accessible with data.getKey()
                                        String key = data.getKey();  // This is the unique key like 'OFB0-JVSY7YZFpb59vw'

                                        // Create a new UserData object to update
                                        UserData userData = new UserData(Profile_pic, username, email, Phone_no);

                                        // Check if the username is valid
                                        if (isValidUsername(username)) {
                                            // Update the user data under the specific key (phone number -> second-level key)
                                            assert key != null;
                                            FirebaseDatabase.getInstance()
                                                    .getReference("UserList")
                                                    .child(Phone_no) // First-level key (phone number)
                                                    .child("UserDetails")
                                                    .child(key)// Second-level key (user ID)
                                                    .setValue(userData)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                // Data update successful
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Handle failure
                                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                    performLogout();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle any database errors
                                    Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                }
                else{

                    User_name.setError("3-16 characters, using letters, numbers, and optionally underscores. Avoid spaces, special characters, and personal info like full names or birthdates. Keep it unique and professional if needed.");

                }


            }
        });

        return view;

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
        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_image.jpg"));

        // UCrop configuration
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionQuality(80);
        options.setCircleDimmedLayer(true); // For circular cropping (optional)
        options.setShowCropGrid(true);
        options.setShowCropFrame(true);
        options.setToolbarColor(ContextCompat.getColor(requireContext(), R.color.Lavendar));
        options.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.Lavendar));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(requireContext(), R.color.Lavendar));

        // Start the crop activity
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1) // 1:1 aspect ratio (customize as needed)
                .withMaxResultSize(500, 500)
                .withOptions(options)
                .start(requireContext(), this); // Use fragment context and reference
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                User_profile_pic.setImageURI(resultUri); // Display the cropped image
                User_profile_pic64 = ImageUtils.convertImageToBase64(resultUri, getContext());
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(getContext(), cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Helper function to check if a character is a special character
    public static boolean isValidUsername(String username) {
        if (username == null || username.length() <3 || username.length() > 16) {
            return false;
        }
        String regex = "^[a-zA-Z0-9_]+$";
        return username.matches(regex);
    }


    public void performLogout() {
        // Clear shared preferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all data
        editor.apply();

        // Check if a user is logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // Sign out from Firebase
            auth.signOut();
        }

        // Redirect to Login screen
        Intent intent = new Intent(getContext(), LogInPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);

        // Show logout toast
        Toast.makeText(requireContext(), "Edits Saved, Login to see the updates", Toast.LENGTH_SHORT).show();
    }

}


