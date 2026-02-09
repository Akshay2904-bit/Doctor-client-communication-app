package com.example.medilink.Doctorside;

import static com.example.medilink.Doctorside.MyAdapter.decodeBase64ToBitmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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

import com.example.medilink.CalendarCodes.Calendar_fragment;
import com.example.medilink.GolbalActivities.LogInPage;
import com.example.medilink.GolbalActivities.UserData;
import com.example.medilink.Patientside.PatientSide;
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


public class DoctorSide extends AppCompatActivity {

    ImageView Doctor_profile_picture;
    TextView greetingTextView, Doctor_user_name;
    ImageButton add_Client;
    RecyclerView recyclerView;
    List<ClientData> dataList;
    DatabaseReference database_ref;
    ValueEventListener eventListener;
    MyAdapter adapter;
    SearchView client_search;
    // Initialize BottomNavigationView
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    UserData data;
    String base64ImageString;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_side);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });





        CardView cardView = findViewById(R.id.cardview);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        Doctor_profile_picture = findViewById(R.id.doctor_side_home_page_pfp_image_view);
        Doctor_user_name = findViewById(R.id.doctor_side_user_Name);
        greetingTextView = findViewById(R.id.greetingTextView);
        client_search = findViewById(R.id.clientSearch);
        client_search.clearFocus();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        String greeting = getGreetingBasedOnTime();
        greetingTextView.setText(greeting);
        add_Client = findViewById(R.id.addClient);
        recyclerView = findViewById(R.id.recycler_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        drawerLayout.openDrawer(GravityCompat.END);

        // Close the right-side drawer
        drawerLayout.closeDrawer(GravityCompat.END);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("USER_PHONE", null);
        if (userPhone != null) {
            Log.d("DEBUG", "onCreate: phone no"+userPhone);
        }


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
                                Doctor_profile_picture.setImageBitmap(bitmap);
                                Log.d("DEBUG", "onDataChange: not null");
                            } else {
                                Log.d("DEBUG", "onDataChange: null");
                            }

                            String registeredUserName = userSnapshots.child("user_registered_Name").getValue(String.class);
                            if (registeredUserName != null) {
                                Doctor_user_name.setText(registeredUserName);
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




        NavigationView navigationView = findViewById(R.id.drawer_view);
        Toolbar toolbar = findViewById(R.id.drawer_toolbar);

        toolbar.clearFocus();


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
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

                if (id == R.id.patient_side) {
                    // Start a new activity
                    Intent intent = new Intent(DoctorSide.this, PatientSide.class);
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



        //Toast.makeText(DoctorSide.this, "Recycler view has been identified", Toast.LENGTH_LONG).show();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(DoctorSide.this, 1);

        recyclerView.setLayoutManager(gridLayoutManager);
        //Toast.makeText(DoctorSide.this, "Recycler view has been Gridded", Toast.LENGTH_LONG).show();

        dataList = new ArrayList<>();
        adapter = new MyAdapter(dataList, DoctorSide.this);
        recyclerView.setAdapter(adapter);
        //Toast.makeText(DoctorSide.this, "Recycler view has been adapted to MyAdapter", Toast.LENGTH_LONG).show();



        progressBar.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);




        // Firebase database reference
        assert userPhone != null;
        database_ref = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(userPhone) // Unique identifier for the user
                .child("Clients");



            // Attach the listener to the Firebase reference
        eventListener = database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Call the method to handle data update
                handleDataUpdate(snapshot);
                progressBar.setVisibility(View.INVISIBLE);
                cardView.setVisibility(View.INVISIBLE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log error and dismiss dialog
                //Log.e("Firebase", "Database error: " + error.getMessage());
                cardView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

            }
        });

        client_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchListClient(newText);
                return true;
            }
        });


        add_Client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoctorSide.this, AddingClient.class);
                startActivity(intent);


            }


        });



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
                replaceFragment(new Calendar_fragment());
            } else if (itemId == R.id.Report_bottom) {
                replaceFragment(new Report_fragment());
            } else if (itemId == R.id.fab_bottom) {
                // Handle FAB action
                return false; // Do not select the FAB item
            }
            return true;
        });

        // Set Home tab as selected by default
        bottomNavigationView.setSelectedItemId(R.id.Home_bottom);



    }
    /**
     * Replaces the current fragment with the given fragment.
     *
     * @param fragment The fragment to display.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main, fragment); // Assuming 'main' is the frame for fragments
        fragmentTransaction.commit();
    }







    private void handleDataUpdate(DataSnapshot snapshot) {


        // Check if new data is present in the snapshot
        if (snapshot.exists()) {
            // Log for debugging
            //Log.d("Firebase", "Snapshot exists, children count: " + snapshot.getChildrenCount());

            // Clear the list and update it only if new data is present
            dataList.clear();


                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ClientData clientData = itemSnapshot.getValue(ClientData.class);

                    if (clientData != null) {

                        clientData.setKey(itemSnapshot.getKey()); // Correctly set the key
                        dataList.add(clientData); // Add the same object to the list

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

    private void searchListClient(String text){
        ArrayList<ClientData> searchList = new ArrayList<>();

        for(ClientData clientData: dataList){
            if(clientData.getNmClient().toLowerCase().contains(text.toLowerCase())) {

                searchList.add(clientData);

            }

        }
        adapter.searchClientList(searchList);
    }

    private void performLogout() {
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
        Intent intent = new Intent(DoctorSide.this, LogInPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);

        // Show logout toast
        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
    }


}
