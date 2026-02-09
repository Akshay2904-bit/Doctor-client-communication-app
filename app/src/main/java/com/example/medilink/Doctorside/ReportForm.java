package com.example.medilink.Doctorside;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medilink.R;
import com.google.android.material.button.MaterialButton;

import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;

public class ReportForm extends AppCompatActivity {

    private LinearLayout tlPrescriptionReport;
    private EditText etDoctorName, etPatientName, etPatientAge, etPatientGender;
    private Button btnPrescriptionDate;
    private EditText[] etMedications, etDosages, etFrequencies;
    private Calendar selectedDate;
    private MaterialButton btnDownloadPrescription;
    private ActivityResultLauncher<Intent> saveFileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tlPrescriptionReport = findViewById(R.id.tlPrescriptionReport);
        etDoctorName = findViewById(R.id.etDoctorName);
        btnPrescriptionDate = findViewById(R.id.btnPrescriptionDate);
        etPatientName = findViewById(R.id.etPatientName);
        etPatientAge = findViewById(R.id.etPatientAge);
        etPatientGender = findViewById(R.id.etPatientGender);

        selectedDate = Calendar.getInstance();

        btnPrescriptionDate.setOnClickListener(v -> {
            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH);
            int day = selectedDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ReportForm.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        String formattedDate = DateFormat.getDateInstance().format(selectedDate.getTime());
                        btnPrescriptionDate.setText(formattedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        etMedications = new EditText[]{
                findViewById(R.id.etMedication1),
                findViewById(R.id.etMedication2),
                findViewById(R.id.etMedication3),
                findViewById(R.id.etMedication4),
                findViewById(R.id.etMedication5),
                findViewById(R.id.etMedication6),
                findViewById(R.id.etMedication7),
                findViewById(R.id.etMedication8),
        };

        etDosages = new EditText[]{
                findViewById(R.id.etDosage1),
                findViewById(R.id.etDosage2),
                findViewById(R.id.etDosage3),
                findViewById(R.id.etDosage4),
                findViewById(R.id.etDosage5),
                findViewById(R.id.etDosage6),
                findViewById(R.id.etDosage7),
                findViewById(R.id.etDosage8),
        };

        etFrequencies = new EditText[]{
                findViewById(R.id.etFrequency1),
                findViewById(R.id.etFrequency2),
                findViewById(R.id.etFrequency3),
                findViewById(R.id.etFrequency4),
                findViewById(R.id.etFrequency5),
                findViewById(R.id.etFrequency6),
                findViewById(R.id.etFrequency7),
                findViewById(R.id.etFrequency8),
        };

        btnDownloadPrescription = findViewById(R.id.btnDownloadPrescription);

        saveFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            Bitmap bitmap = getBitmapFromView(tlPrescriptionReport);
                            saveBitmapToUri(bitmap, uri);
                        }
                    }
                }
        );

        btnDownloadPrescription.setOnClickListener(v -> {
            Bitmap bitmap = getBitmapFromView(tlPrescriptionReport);
            if (bitmap != null) {
                openFilePicker();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TITLE, "Prescription_" + System.currentTimeMillis() + ".png");
        saveFileLauncher.launch(intent);
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void saveBitmapToUri(Bitmap bitmap, Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Toast.makeText(this, "Saved successfully as PNG.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving file.", Toast.LENGTH_SHORT).show();
        }
    }
}