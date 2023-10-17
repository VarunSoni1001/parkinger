package com.example.parkinger;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookParkingActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private FirebaseAuth mAuth;

    String email, date, time;

    private FirebaseUser userEmail;
    CollectionReference parkingRef;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_parking);






        mAuth = FirebaseAuth.getInstance();
        userEmail = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (userEmail == null) {
            Intent intent = new Intent(BookParkingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            email = userEmail.getEmail();
        }

        TextView parkingSpotName = findViewById(R.id.ParkingSpotName);
        TextView parkingSpotPrice = findViewById(R.id.ParkingSpotPrice);
        TextView parkingSlots = findViewById(R.id.ParkingSlots);
        TextView payNow = findViewById(R.id.payNowBtn);
        TextView payLater = findViewById(R.id.payLaterBtn);
        TextView failed = findViewById(R.id.FailText);
        TextView passed = findViewById(R.id.passText);
        TextInputEditText carNumber = findViewById(R.id.carNumber);
        ProgressBar progressBar = findViewById(R.id.progressBar);


        passed.setVisibility(View.INVISIBLE);
        failed.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        carNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(carNumber.getText())) {
                    passed.setVisibility(View.INVISIBLE);
                    failed.setVisibility(View.INVISIBLE);
                } else {
                    passed.setVisibility(View.INVISIBLE);
                    failed.setVisibility(View.INVISIBLE);
                    // Check if the entered car number is valid
                    if (isValidCarNumber(s.toString())) {
                        // Car number is valid, show a message indicating the correct format
                        passed.setVisibility(View.VISIBLE);
                        failed.setVisibility(View.INVISIBLE);
                        passed.setText("Valid car number: " + String.valueOf(carNumber.getText()));
                    } else {
                        failed.setVisibility(View.VISIBLE);
                        passed.setVisibility(View.INVISIBLE);
                        failed.setText("Invalid car number: " + String.valueOf(carNumber.getText()));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });

        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("LATITUDE", 28.6139);
        double longitude = intent.getDoubleExtra("LONGITUDE", 77.2090);
        String title = intent.getStringExtra("PARKING_NAME");
        int price = intent.getIntExtra("PARKING_PRICE", 0);
        int slotsAvailable = intent.getIntExtra("PARKING_SLOTS", 0);
        String timings = intent.getStringExtra("PARKING_TIMINGS");

        parkingSpotName.setText("Name of parking: " + title);
        parkingSpotPrice.setText("Price: ₹" + price + " per hour");
        parkingSlots.setText("Available slots: " + slotsAvailable + "\n" + "Timings: " + timings);

        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                failed.setVisibility(View.INVISIBLE);
                if (isValidCarNumber(String.valueOf(carNumber.getText()))) {
                    if (!TextUtils.isEmpty(String.valueOf(carNumber.getText()))) {
                        failed.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent1 = new Intent(BookParkingActivity.this, UpiActivity.class);
                                intent1.putExtra("LATITUDE", latitude);
                                intent1.putExtra("LONGITUDE", longitude);
                                intent1.putExtra("UPI_AMOUNT", price);
                                intent1.putExtra("SLOTS", slotsAvailable);
                                intent1.putExtra("CAR_NUMBER_FOR_QR", String.valueOf(carNumber.getText()));
                                intent1.putExtra("PARK_NAME", title);
                                intent1.putExtra("TIMINGS", timings);
                                startActivity(intent1);
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }, 1500);
                    } else {
                        carNumber.setError("Please! Enter your car number.");
                        failed.setVisibility(View.VISIBLE);
                        failed.setText("Please! Enter your car number.");
                    }
                } else {
                    // Car number is invalid, show error message
                    if (TextUtils.isEmpty(carNumber.getText())) {
                        carNumber.setError("Enter your car number");
                        failed.setVisibility(View.VISIBLE);
                        failed.setText("Enter your car number");
                    } else {
                        carNumber.setError("Invalid car number");
                        failed.setVisibility(View.VISIBLE);
                        failed.setText("Invalid car number");
                    }
                }
            }
        });

        payLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failed.setVisibility(View.INVISIBLE);
                if (isValidCarNumber(String.valueOf(carNumber.getText()))) {
                    if (!TextUtils.isEmpty(String.valueOf(carNumber.getText()))) {
                        failed.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                        Intent intent2 = new Intent(BookParkingActivity.this, QRActivity.class);
                        intent2.putExtra("LATITUDE", latitude);
                        intent2.putExtra("LONGITUDE", longitude);
                        intent2.putExtra("PAYMENT_STATUS", "Pending");
                        intent2.putExtra("CAR_NUMBER_QR", String.valueOf(carNumber.getText()));

                        startActivity(intent2);

                        Calendar calendar = Calendar.getInstance();
                        Date c = calendar.getTime();
                        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        DateFormat tf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
                        time = tf.format(c);
                        date = df.format(c);

                        db = FirebaseFirestore.getInstance();
                        parkingRef = db.collection("Booked Parkings");
                        String documentId = parkingRef.document().getId();
                        Map<String, String> parkingDetails = new HashMap<>();
                        parkingDetails.put("nameOfParking", title);
                        parkingDetails.put("priceOfParking", String.valueOf(price));
                        parkingDetails.put("timing", timings);
                        parkingDetails.put("carNumber", String.valueOf(carNumber.getText()).toUpperCase());
                        parkingDetails.put("paymentMethod", "Later at parking");
                        parkingDetails.put("paymentStatus", "Pending");
                        parkingDetails.put("email", email);
                        parkingDetails.put("bookingDate", date);
                        parkingDetails.put("bookingTime", time);
                        parkingDetails.put("id", documentId);
                        parkingRef.document(documentId).set(parkingDetails);
                        finish();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }, 1500);
                    } else {
                        carNumber.setError("Please! Enter your car number.");
                        failed.setVisibility(View.VISIBLE);
                        failed.setText("Please! Enter your car number.");
                    }
                } else {
                    // Car number is invalid, show error message
                    if (TextUtils.isEmpty(carNumber.getText())) {
                        carNumber.setError("Enter your car number");
                        failed.setVisibility(View.VISIBLE);
                        failed.setText("Enter your car number");
                    } else {
                        carNumber.setError("Invalid car number");
                        failed.setVisibility(View.VISIBLE);
                        failed.setText("Invalid car number");
                    }
                }
            }
        });
    }


    public boolean isValidCarNumber(String carNumber) {
        // Regular expression pattern for Indian car number plates
        String regex = "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{2}[A-Za-z0-9]{1,2}[A-Za-z]{1,3}[0-9]{1,4}$";

        // Match the car number against the regex pattern
        return carNumber.matches(regex);
    }



//    public void showTimePickerDialog(View view) {
//        final TextInputEditText editText = (TextInputEditText) view;
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//                new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        final int selectedYear = year;
//                        final int selectedMonth = monthOfYear;
//                        final int selectedDay = dayOfMonth;
//
//                        // Check if the selected date is in the past
//                        Calendar selectedDate = Calendar.getInstance();
//                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
//                        if (selectedDate.before(Calendar.getInstance())) {
//                            // Selected date is in the past, show error message
//                            Toast.makeText(BookParkingActivity.this, "Invalid date", Toast.LENGTH_SHORT).show();
//                            return;
//
//                        }
//
//                        int hour = c.get(Calendar.HOUR_OF_DAY);
//                        int minute = c.get(Calendar.MINUTE);
//
//                        TimePickerDialog timePickerDialog = new TimePickerDialog(BookParkingActivity.this,
//                                new TimePickerDialog.OnTimeSetListener() {
//                                    @Override
//                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                        // Check if the selected time is in the past
//                                        Calendar selectedTime = Calendar.getInstance();
//                                        selectedTime.set(selectedYear, selectedMonth, selectedDay, hourOfDay, minute);
//                                        if (selectedTime.before(Calendar.getInstance())) {
//                                            // Selected time is in the past, show error message
//                                            Toast.makeText(BookParkingActivity.this, "Invalid time", Toast.LENGTH_SHORT).show();
//                                            editText.setError("");
//                                            return;
//                                        }
//
//                                        String amPm;
//                                        if (hourOfDay < 12) {
//                                            amPm = "AM";
//                                        } else {
//                                            amPm = "PM";
//                                            hourOfDay -= 12;
//                                        }
//                                        String formattedDateTime = String.format(Locale.getDefault(), "%02d:%02d %s - %02d/%02d/%04d", hourOfDay, minute, amPm, selectedDay, selectedMonth + 1, selectedYear);
//                                        editText.setText(formattedDateTime);
//                                    }
//                                }, hour, minute, false); // Set is24HourView to false
//
//                        timePickerDialog.show();
//                    }
//                }, year, month, day);
//
//        // Set minimum date to today
//        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
//
//        datePickerDialog.show();
//    }

}
