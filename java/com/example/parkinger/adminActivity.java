package com.example.parkinger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class adminActivity extends AppCompatActivity {
    TextInputEditText latitude, longitude, parkingNameToAdd, priceOfParking, timingOfParking, totalSlots;
    String parkName, parkingTimings;
    int parkingPrice, slotsAvailable;
    Double lat, lng;
    TextView failedText, passedText;
    ProgressBar progressBar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        parkingNameToAdd = findViewById(R.id.parkingNameToAdd);
        priceOfParking = findViewById(R.id.priceOfParking);
        timingOfParking = findViewById(R.id.timingOfParking);
        totalSlots = findViewById(R.id.totalSlots);
        failedText = findViewById(R.id.adminFailText);
        passedText = findViewById(R.id.adminPassText);
        progressBar = findViewById(R.id.adminProgressBar1);

        failedText.setVisibility(View.INVISIBLE);
        passedText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        Button saveButton = findViewById(R.id.addParkingBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                failedText.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        saveParkingDataToFireStore();
                    }
                }, 1000);
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void saveParkingDataToFireStore() {
        lat = Double.valueOf(String.valueOf(latitude.getText()).trim());
        lng = Double.valueOf(String.valueOf(longitude.getText()).trim());
        parkName = String.valueOf(parkingNameToAdd.getText()).trim();
        parkingPrice = Integer.parseInt(String.valueOf(priceOfParking.getText()).trim());
        parkingTimings = String.valueOf(timingOfParking.getText()).trim();
        slotsAvailable = Integer.parseInt(String.valueOf(totalSlots.getText()).trim());

        failedText.setVisibility(View.INVISIBLE);
        passedText.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        // Create a Firestore document reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference parkingCollectionRef = db.collection("MarkersData");
        DocumentReference parkingDocumentRef = parkingCollectionRef.document();

        // Create a data object or a Map to store the parking details
        Map<String, Object> parkingData = new HashMap<>();
        parkingData.put("lat", lat);
        parkingData.put("lng", lng);
        parkingData.put("parkName", parkName);
        parkingData.put("parkingPrice", parkingPrice);
        parkingData.put("parkingTimings", parkingTimings);
        parkingData.put("slotsAvailable", slotsAvailable);

        // Save the data to Firestore using the document reference
        parkingDocumentRef.set(parkingData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    @SuppressLint("SetTextI18n")
                    public void onSuccess(Void aVoid) {
                        // Data successfully saved in Firestore
                        latitude.setText("");
                        longitude.setText("");
                        parkingNameToAdd.setText("");
                        priceOfParking.setText("");
                        timingOfParking.setText("");
                        totalSlots.setText("");

                        passedText.setVisibility(View.VISIBLE);
                        failedText.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        passedText.setText("Parking location and data saved successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to save data in Firestore
                        failedText.setVisibility(View.VISIBLE);
                        passedText.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        failedText.setText("Failed to save parking location and data");
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Check if the user is logged in and perform sign-out
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Perform the sign-out process, such as clearing the login status and redirecting to the login screen
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            // Redirect to the login screen
            startActivity(new Intent(adminActivity.this, LoginActivity.class));
            finish();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Check if the user is logged in and perform sign-out
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Perform the sign-out process, such as clearing the login status and redirecting to the login screen
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            // Redirect to the login screen
            startActivity(new Intent(adminActivity.this, LoginActivity.class));
            finish();
        }
    }

}