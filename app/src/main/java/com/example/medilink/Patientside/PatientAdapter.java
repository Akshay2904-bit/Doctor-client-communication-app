package com.example.medilink.Patientside;

import static com.example.medilink.Doctorside.MyAdapter.decodeBase64ToBitmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medilink.CalendarCodes.Event_details;
import com.example.medilink.Doctorside.ClientData;
import com.example.medilink.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {


    Context context;
    List<DoctorData> dataList;

    public PatientAdapter(List<DoctorData> dataList, Context context) {
        this.context = context;
        this.dataList = dataList;
    }



    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_file_patient_side,parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Log.d("DEBUG", "onBindViewHolder: reached here" + dataList.size());

        DoctorData data = dataList.get(position);

        holder.Display_name.setText(data.getName());
        holder.Display_phone.setText(data.getPhoneNumber());

        // Fetch the Base64 image string
        String base64ImageString = data.getPhoto().trim().replaceAll("[^A-Za-z0-9+/=]", "");

        // Convert the Base64 string to a Bitmap
        Bitmap bitmap = decodeBase64ToBitmap(base64ImageString);

        // Check if bitmap is not null before setting to ImageView
        if (bitmap != null) {
            holder.Display_pfp.setImageBitmap(bitmap);  // Set the Bitmap to the ImageView
        }else{
            holder.Display_pfp.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ab));

        }

        holder.doctor_Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("DEBUG", "CLICKED");

                Intent intentEvent = new Intent(context, Doctor_Details_Display.class);

                String imagePath = saveBitmapToFile(context, bitmap);


                Bundle bundle = new Bundle();


                intentEvent.putExtra("NAME", data.getName());
                intentEvent.putExtra("PHONE", data.getPhoneNumber());
                intentEvent.putExtra("KEY", data.getKey());
                intentEvent.putExtra("Image", imagePath);

                // Start the new activity
                context.startActivity(intentEvent);




            }

            private String saveBitmapToFile(Context context, Bitmap bitmap) {
                // Define the directory where the image will be saved
                File directory = context.getCacheDir();  // Use cache directory or another location
                File file = new File(directory, "image.jpg");  // Define the file name

                // Try to save the bitmap to the file
                try (FileOutputStream out = new FileOutputStream(file)) {
                    // Compress the Bitmap into the file (JPEG format, 100% quality)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();  // Ensure everything is written
                    //Log.d("DEBUG", "Image saved to: " + file.getAbsolutePath());
                    return file.getAbsolutePath();  // Return the file path
                } catch (IOException e) {
                    e.printStackTrace();
                    //Log.e("ERROR", "Failed to save image to file");
                    return null;  // Return null if there was an error
                }
            }

        });


    }

    @Override
    public int getItemCount() {
       return dataList.size();
    }
    void searchDoctorList(ArrayList<DoctorData> searchList){

        dataList = searchList;
        notifyDataSetChanged();

    }

    public class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView Display_name;
        ImageView Display_pfp;
        TextView Display_phone;
        CardView doctor_Card;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);

            doctor_Card = itemView.findViewById(R.id.recyclerImage_doctor);
            Display_name = itemView.findViewById(R.id.recName_doc);
            Display_phone = itemView.findViewById(R.id.recPhone_doc);
            Display_pfp = itemView.findViewById(R.id.recImage_doc);
        }
    }
}
