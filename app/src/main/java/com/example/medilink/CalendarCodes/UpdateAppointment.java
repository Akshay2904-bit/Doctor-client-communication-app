package com.example.medilink.CalendarCodes;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medilink.Doctorside.ClientData;
import com.example.medilink.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UpdateAppointment extends AppCompatActivity {

    TextView client_picker;
    EditText clientPhoneEditText; // Reference to the phone number EditText
    Dialog dialog;
    List<ClientData> clientList; // List of clients fetched from Firebase
    List<ClientData> filteredClients; // Filtered list for search
    RecyclerView recyclerView;
    Adding_Appointment.ClientAdapter clientAdapter;
    DatabaseReference clientReference;
    String key;



    private Button pickDate, pickTime_from, pickTime_till, saveAppointment;
    //private ImageView eventImage;
    private Calendar selectedDateTime;
    private String eventTime_from = "";
    private String eventTime_till = "";
    private String eventDate = "";
    private HashMap<String, Boolean> eventDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_appointment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        eventDates = new HashMap<>();

        client_picker = findViewById(R.id.appointment_client_name);
        clientPhoneEditText = findViewById(R.id.appointment_client_phone_no); // Initialize phone number EditText

        pickDate = findViewById(R.id.pickDateButton);
        pickTime_from = findViewById(R.id.pickTimeButton1);
        pickTime_till = findViewById(R.id.pickTimeButton2);
        saveAppointment = findViewById(R.id.saveAppointmentButton);


        Bundle bundle = getIntent().getExtras();


        if (bundle != null) {
            // Populate fields with event data
            clientPhoneEditText.setText(bundle.getString("Phone", ""));
            client_picker.setText(bundle.getString("NameClient", ""));
            pickTime_till.setText(bundle.getString("End_Time", ""));
            pickTime_from.setText(bundle.getString("Start_Time", ""));
            pickDate.setText(bundle.getString("Date", ""));
            key = bundle.getString("Key", ""); // Retrieve the key
        }

        selectedDateTime = Calendar.getInstance();

        SharedPreferences sharedPreferences = UpdateAppointment.this.getSharedPreferences("UserPrefs", UpdateAppointment.this.MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("USER_PHONE", null);


        // Initialize Firebase reference
        clientReference = FirebaseDatabase.getInstance()
                .getReference("UserList")
                .child(userPhone)
                .child("Clients");

        // Initialize the client list
        clientList = new ArrayList<>();

        client_picker.setOnClickListener(v -> {
            dialog = new Dialog(UpdateAppointment.this);
            dialog.setContentView(R.layout.searchable_spinner_for_clients);
            Objects.requireNonNull(dialog.getWindow()).setLayout(800, 800);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            EditText client_search = dialog.findViewById(R.id.enter_name_of_client);
            recyclerView = dialog.findViewById(R.id.recycler_view);

            // Initialize RecyclerView
            filteredClients = new ArrayList<>();
            recyclerView.setLayoutManager(new LinearLayoutManager(UpdateAppointment.this));
            clientAdapter = new Adding_Appointment.ClientAdapter(filteredClients, selectedClient -> {
                client_picker.setText(selectedClient.getNmClient());
                clientPhoneEditText.setText(selectedClient.getPnClient()); // Set the phone number in the EditText
                dialog.dismiss();
            });
            recyclerView.setAdapter(clientAdapter);

            // Add search functionality
            client_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterClients(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // Fetch clients from Firebase
            fetchClientsFromFirebase();
        });


        // Date Picker
        pickDate.setOnClickListener(v -> {
            int year = selectedDateTime.get(Calendar.YEAR);
            int month = selectedDateTime.get(Calendar.MONTH);
            int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    UpdateAppointment.this,
                    (DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) -> {
                        selectedDateTime.set(selectedYear, selectedMonth, selectedDay);
                        eventDate = DateFormat.getDateInstance().format(selectedDateTime.getTime());
                        pickDate.setText(eventDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        // Time Picker
        pickTime_from.setOnClickListener(v -> {
            int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
            int minute = selectedDateTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    UpdateAppointment.this,
                    (TimePicker timePicker, int selectedHour, int selectedMinute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        selectedDateTime.set(Calendar.MINUTE, selectedMinute);
                        eventTime_from = String.format("%02d:%02d", selectedHour, selectedMinute);
                        pickTime_from.setText(eventTime_from);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });

        pickTime_till.setOnClickListener(v -> {
            int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
            int minute = selectedDateTime.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    UpdateAppointment.this,
                    (TimePicker timePicker, int selectedHour, int selectedMinute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        selectedDateTime.set(Calendar.MINUTE, selectedMinute);
                        eventTime_till = String.format("%02d:%02d", selectedHour, selectedMinute);
                        pickTime_till.setText(eventTime_till);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });

        // Save Event
        saveAppointment.setOnClickListener(v -> saveEventToFirebase());





    }

    // Fetch clients from Firebase Realtime Database
    private void fetchClientsFromFirebase() {
        clientReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clientList.clear();
                for (DataSnapshot clientSnapshot : snapshot.getChildren()) {
                    ClientData clientData = clientSnapshot.getValue(ClientData.class);
                    if (clientData != null) {
                        clientData.setKey(clientSnapshot.getKey());
                        clientList.add(clientData);
                    }
                }
                // Initially, show all clients in the dialog
                filteredClients.clear();
                filteredClients.addAll(clientList);
                clientAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateAppointment.this, "Failed to load clients: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Filter clients based on search input
    private void filterClients(String query) {
        filteredClients.clear();
        for (ClientData client : clientList) {
            if (client.getNmClient().toLowerCase().contains(query.toLowerCase()) ||
                    client.getPnClient().toLowerCase().contains(query.toLowerCase())) {
                filteredClients.add(client);
            }
        }
        clientAdapter.notifyDataSetChanged();
    }

    // Adapter for RecyclerView
    static class ClientAdapter extends RecyclerView.Adapter<Adding_Appointment.ClientAdapter.ViewHolder> {
        private final List<ClientData> clients;
        private final Adding_Appointment.OnClientSelectedListener listener;

        ClientAdapter(List<ClientData> clients, Adding_Appointment.OnClientSelectedListener listener) {
            this.clients = clients;
            this.listener = listener;
        }

        @NonNull
        @Override
        public Adding_Appointment.ClientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.client_list_item, parent, false);
            return new Adding_Appointment.ClientAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adding_Appointment.ClientAdapter.ViewHolder holder, int position) {
            ClientData client = clients.get(position);
            holder.nameTextView.setText(client.getNmClient());
            holder.phoneTextView.setText(client.getPnClient());
            holder.itemView.setOnClickListener(v -> listener.onClientSelected(client));
        }

        @Override
        public int getItemCount() {
            return clients.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView, phoneTextView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(R.id.client_name);
                phoneTextView = itemView.findViewById(R.id.client_phone);
            }
        }
    }

    // Listener interface for client selection
    interface OnClientSelectedListener {
        void onClientSelected(ClientData selectedClient);
    }


    private void saveEventToFirebase() {
        String name = client_picker.getText().toString();
        String phone = clientPhoneEditText.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || eventDate.isEmpty() || eventTime_from.isEmpty() || eventTime_till.isEmpty()) {
            Toast.makeText(UpdateAppointment.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = UpdateAppointment.this.getSharedPreferences("UserPrefs", UpdateAppointment.this.MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("USER_PHONE", null);

        if (userPhone != null) {

            // Prepare client data
            AppointmentData appointmentData = new AppointmentData(name, phone, eventDate, eventTime_from, eventTime_till);
            appointmentData.setKey(key);  // Set the key here

            FirebaseDatabase.getInstance()
                    .getReference("UserList")
                    .child(userPhone)
                    .child("Appointments")
                    .child(key)
                    .setValue(appointmentData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateAppointment.this, "Appointment saved successfully!", Toast.LENGTH_SHORT).show();
                            clearFields();
                        } else {
                            Toast.makeText(UpdateAppointment.this, "Error saving Appointments", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }



    private void clearFields() {
        pickDate.setText("Pick Date");
        pickTime_from.setText("Pick Time from");
        pickTime_till.setText("Pick Time till");
    }

}