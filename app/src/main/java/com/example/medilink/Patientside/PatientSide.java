package com.example.medilink.Patientside;

import static com.example.medilink.Doctorside.MyAdapter.decodeBase64ToBitmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medilink.CalendarCodes.CalendarPatient;
import com.example.medilink.Doctorside.ClientData;
import com.example.medilink.Doctorside.DoctorSide;
import com.example.medilink.GolbalActivities.LogInPage;
import com.example.medilink.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PatientSide extends AppCompatActivity {

    ImageView patient_HomePage_profile_picture;
    TextView greetingTextView,patient_HomePage_display_user_name ;
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    PatientAdapter adapter;
    List<DoctorData> dataList;
    SearchView Doc_search;
    DatabaseReference database_ref;
    ValueEventListener eventListener;
    String greeting;
    String base64ImageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_side);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Doc_search = findViewById(R.id.doctor_search);

        Doc_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchListDoctor(newText);
                return true;
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("USER_PHONE", null);
        if (userPhone != null) {
            //Toast.makeText(this, "User Phone: " + userPhone, Toast.LENGTH_SHORT).show();
        }


        patient_HomePage_profile_picture = findViewById(R.id.patient_side_users_profile_picture);
        patient_HomePage_display_user_name = findViewById(R.id.patient_side_users__name);

        FirebaseDatabase.getInstance().getReference("UserList")
                .child(userPhone)
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
                                patient_HomePage_profile_picture.setImageBitmap(bitmap);
                                Log.d("DEBUG", "onDataChange: not null");
                            } else {
                                Log.d("DEBUG", "onDataChange: null");
                            }

                            String registeredUserName = userSnapshots.child("user_registered_Name").getValue(String.class);
                            if (registeredUserName != null) {
                                patient_HomePage_display_user_name.setText(registeredUserName);
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

        greetingTextView = findViewById(R.id.greetingTextView);
        greeting = getGreetingBasedOnTime();
        greetingTextView.setText(greeting);
        drawerLayout = findViewById(R.id.drawer_layout);



        recyclerView = findViewById(R.id.Doc_recycler_view);

        drawerLayout.openDrawer(GravityCompat.END);

        // Close the right-side drawer
        drawerLayout.closeDrawer(GravityCompat.END);

        //Toast.makeText(DoctorSide.this, "Recycler view has been identified", Toast.LENGTH_LONG).show();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(PatientSide.this, 1);

        recyclerView.setLayoutManager(gridLayoutManager);
        //Toast.makeText(DoctorSide.this, "Recycler view has been Gridded", Toast.LENGTH_LONG).show();

        dataList = new ArrayList<>();
        adapter = new PatientAdapter(dataList, PatientSide.this);
        recyclerView.setAdapter(adapter);
        //Toast.makeText(DoctorSide.this, "Recycler view has been adapted to MyAdapter", Toast.LENGTH_LONG).show();


        // Firebase database reference
        assert userPhone != null;
        database_ref = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(userPhone) // Unique identifier for the user
                .child("Doctors");



        // Attach the listener to the Firebase reference
        eventListener = database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Call the method to handle data update
                handleDataUpdate(snapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        NavigationView navigationView = findViewById(R.id.drawer_view);
        Toolbar toolbar = findViewById(R.id.drawer_toolbar);

        toolbar.clearFocus();




        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(PatientSide.this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();




        // Optional: Modify toggle behavior if needed
        toolbar.setNavigationOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });





        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.Doctor_Side) {
                    // Start a new activity
                    Intent intent = new Intent(PatientSide.this, DoctorSide.class);
                    startActivity(intent);
                } else if (id == R.id.nav_logout) {
                    // Perform logout logic
                    performLogout();
                } else {
                    // Handle other cases if needed
                    return false;
                }

                // Close the drawer after selecting an item
                drawerLayout.closeDrawers();
                return true;
            }
        });


        // Initialize bottomNavigationView properly
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView == null) {
            throw new IllegalStateException("BottomNavigationView is not found. Check R.id.bottom_navigation in your layout.");
        }

        // Set listener for BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.Home_bottom) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentById(R.id.main);
                if (fragment != null) {
                    fragmentManager.beginTransaction().remove(fragment).commit();
                }
            } else if (itemId == R.id.Calendar_bottom) {
                replaceFragment(new CalendarPatient());
            } else if (itemId == R.id.pofile_page) {
                replaceFragment(new ProfilePage());
            }
            return true;
        });

        // Set Home tab as selected by default
        bottomNavigationView.setSelectedItemId(R.id.Home_bottom);
    }



    //Replaces the current fragment with the given fragment. @param fragment The fragment to display.

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main, fragment); // Assuming 'main' is the frame for fragments
        fragmentTransaction.commit();
    }



    private void searchListDoctor(String text){
        ArrayList<DoctorData> searchList = new ArrayList<>();

        for(DoctorData doctorData: dataList){
            if(doctorData.getName().toLowerCase().contains(text.toLowerCase())) {

                searchList.add(doctorData);

            }

        }
        adapter.searchDoctorList(searchList);
    }


    private void handleDataUpdate(DataSnapshot snapshot) {


        // Check if new data is present in the snapshot
        if (snapshot.exists()) {

            // Clear the list and update it only if new data is present
            dataList.clear();


            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                DoctorData doctorData = itemSnapshot.getValue(DoctorData.class);

                if (doctorData != null) {

                    doctorData.setKey(itemSnapshot.getKey()); // Correctly set the key
                    dataList.add(doctorData); // Add the same object to the list

                } else {
                    Log.w("Firebase", "Null data found for snapshot: " + itemSnapshot.getKey());
                }
            }

            // Log for debugging
            Log.d("Firebase", "Data list updated, size: " + dataList.size());

            // Notify the adapter to update the RecyclerView
            adapter.notifyDataSetChanged();
        } else {
            // Log if the snapshot doesn't have any data
            //Log.d("Firebase", "Snapshot is empty.");
            dataList.clear();
            adapter.notifyDataSetChanged();
        }
    }



    private String getGreetingBasedOnTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 6 && hour < 12) {
            return "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon";
        } else if (hour >= 17 && hour < 21) {
            return "Good Evening";
        } else {
            return "Good Night";
        }
    }

    public void performLogout() {
        // Clear shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
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
        Intent intent = new Intent(PatientSide.this, LogInPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);

        // Show logout toast
        Toast.makeText(PatientSide.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
    }
}