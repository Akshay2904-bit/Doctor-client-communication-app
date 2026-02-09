package com.example.medilink.Doctorside;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medilink.R;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;



// Adapter for RecyclerView to bind data to the ViewHolder and handle click actions
public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private final Context context;
    private List<ClientData> dataList;


    // Constructor to initialize dataList and context
    public MyAdapter(List<ClientData> dataList, Context context) {


        this.dataList = dataList;
        this.context = context;

    }


    @NonNull
    @Override // Inflates the view for each item (card) in the RecyclerView
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the RecyclerView (the card layout)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_file_doctor_side, parent, false);

        return new MyViewHolder(view);
    }


    @Override   // Binds data to the ViewHolder (each card)
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        ClientData data = dataList.get(position);
        // Get the data for the current position



        //Log.d("DEBUG", "Client in List: " + data.getNmClient() + ", " + data.getPnClient());

            //Log.d("DEBUG", "Binding Data: " + data.getNmClient());
            holder.disName.setText(data.getNmClient());
            holder.disPhone.setText(data.getPnClient());


            // Fetch the Base64 image string
            String base64ImageString = data.getImClient().trim().replaceAll("[^A-Za-z0-9+/=]", "");

            // Convert the Base64 string to a Bitmap
            Bitmap bitmap = decodeBase64ToBitmap(base64ImageString);

            // Check if bitmap is not null before setting to ImageView
            if (bitmap != null) {
                holder.disImage.setImageBitmap(bitmap);  // Set the Bitmap to the ImageView
            }
            else {
                //Log.d("DEBUG", "Binding Data: " + bitmap);
                // Handle empty dataset case

            }
            //Log.d("DEBUG", "Data List Size: " + dataList.size());
            for (ClientData client : dataList) {
                //Log.d("DEBUG", "Client Data: " + client.getNmClient() + ", " + client.getPnClient());
            }

            // Show a toast to confirm data binding (useful for debugging)




        // Set up an onClickListener for the card
        holder.clientCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                // Create an Intent to navigate to another activity
                Intent intent = new Intent(context, Client_data_display.class);



                // Save Bitmap to a file and get the file path
                String imagePath = saveBitmapToFile(context, bitmap);

                // Pass the client details as extras to the new activity
                intent.putExtra("Name", data.getNmClient());
                intent.putExtra("PhoneNo", data.getPnClient());
                intent.putExtra("Age", data.getAgeClient());
                intent.putExtra("Gender", data.getSxClient());
                intent.putExtra("Disease", data.getDsClient());
                intent.putExtra("Key",data.getKey());
                intent.putExtra("Image", imagePath);

                Log.d("DEBUG", "Client key: " + data.getKey() );

                // Start the new activity
                context.startActivity(intent);

            }



            // Method to save Bitmap to a file and return the file path
            public String saveBitmapToFile(Context context, Bitmap bitmap) {
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
    }// Return the total number of items (cards) in the dataList



    void searchClientList(ArrayList<ClientData> searchList){

                dataList = searchList;
                notifyDataSetChanged();

    }
    // Function to decode a Base64 image string and return a Bitmap
    public static Bitmap decodeBase64ToBitmap(String base64String) {

        try {
            // Decode the Base64 string into a byte array
            byte[] bytesImageDecoded = Base64.getDecoder().decode(base64String);

            // Convert the byte array to a ByteArrayInputStream
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytesImageDecoded);

            // Decode the stream into a Bitmap and return it
            Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
            return bitmap;
        } catch (IllegalArgumentException e) {
            // Handle invalid Base64 string
            e.printStackTrace();


            return null;
        }
    }
}



// ViewHolder class to hold the references to views for each item
class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView disImage;
    TextView disName;
    TextView disPhone;
    CardView clientCard;


    // Constructor to initialize the views in the ViewHolder
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        // Initialize the views from the layout using findViewById
        clientCard = itemView.findViewById(R.id.recyclerImage);
        disImage = itemView.findViewById(R.id.recImage);
        disName = itemView.findViewById(R.id.recName);
        disPhone = itemView.findViewById(R.id.recPhone);
    }
}
