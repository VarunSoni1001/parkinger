package com.example.parkinger;
import static android.content.ContentValues.TAG;
import static java.lang.Integer.parseInt;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.AutoReadOtpHelper;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class UpiActivity extends AppCompatActivity implements PaymentResultListener, NetworkChangeReceiver.ConnectivityChangeListener {
    FirebaseAuth mAuth;
    String userEmail, email, time, date, carNumber, parkName, timings, upiId, name, note;
    double  lat, lng;
    int amount, slotsAvailable;
    FirebaseFirestore db;

    CollectionReference parkingRef;

    private LinearLayout noInternetLayout;
    private NetworkChangeReceiver networkChangeReceiver;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi);

        Checkout.preload(getApplicationContext());
        // Initialize Razorpay checkout


//      RAZOR_PAY_API = rzp_test_f2oA38lSbiSx9p

        mAuth = FirebaseAuth.getInstance();
        userEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        db = FirebaseFirestore.getInstance();

        noInternetLayout = findViewById(R.id.noInternetLayout);

        networkChangeReceiver = new NetworkChangeReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);

        if (userEmail == null) {
            Intent intent = new Intent(UpiActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            email = userEmail;
        }

        TextView parkingName = findViewById(R.id.ParkingName);
        TextView carNumberForQR = findViewById(R.id.carNumberForQR);
        TextView parkingPrice = findViewById(R.id.ParkingPrice);

        Calendar calendar = Calendar.getInstance();
        Date c = calendar.getTime();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        DateFormat tf = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        time = tf.format(c);
        date = df.format(c);

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("LATITUDE", 28.6139);
        lng = intent.getDoubleExtra("LONGITUDE", 77.2090);
        carNumber = intent.getStringExtra("CAR_NUMBER_FOR_QR");
        parkName = intent.getStringExtra("PARK_NAME");
        timings = intent.getStringExtra("TIMINGS");
        slotsAvailable = intent.getIntExtra("SLOTS", 0);

        amount = intent.getIntExtra("UPI_AMOUNT", 0);
        upiId = "9729274000@paytm";
        name = "Parkinger";
        note = "Payment for " + parkName;

        parkingName.setText("Parking Name: " + parkName);
        parkingPrice.setText("Price: ₹" + amount + " per hour");
        carNumberForQR.setText("Car Number: " + carNumber.toUpperCase());


        // Set up the UI
        Button payButton = findViewById(R.id.payBtn);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check internet connection
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    // if connected start payment
                    startRazorPay();
                    Toast.makeText(UpiActivity.this, "This payment is using a testing razorpay gateway.", Toast.LENGTH_SHORT).show();
                } else {
                    // if not send toast message
                    Toast.makeText(UpiActivity.this, "Internet connection is not available, please check and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startRazorPay() {
        // this is the function for starting razorpay checkout
        final Activity activity = this;
        final Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.logo);
        try {
            JSONObject options = new JSONObject();

            options.put("name", name);
            options.put("description", note);
            options.put("theme.color", "#bb86fc");
            options.put("currency", "INR");
            options.put("amount", amount * 100);//pass amount in currency subunits
//            options.put("prefill.email", "varun1001soni@gmail.com");
//            options.put("prefill.contact","9729274000");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(activity, "Something went wrong, please try again later.", Toast.LENGTH_SHORT).show();
            Log.e("TAG", "Error in starting Razorpay Checkout, " + e.getMessage(), e);
        }
    }


    @Override
    public void onPaymentSuccess(String s) {
        // payment success handler
        Log.e(TAG, "Transaction successful " + String.valueOf(s));
        Toast.makeText(this, "Transaction successful.", Toast.LENGTH_SHORT).show();


        // when payment completes do these things
        Intent qrIntent = new Intent(UpiActivity.this, QRActivity.class);
        qrIntent.putExtra("CAR_NUMBER_QR", carNumber);
        qrIntent.putExtra("LATITUDE", lat);
        qrIntent.putExtra("LONGITUDE", lng);
        qrIntent.putExtra("PAYMENT_STATUS", "Paid using RazorPay");
        startActivity(qrIntent);

        // ADD booked Parking in FireStore
        parkingRef = db.collection("Booked Parkings");
        String documentId = parkingRef.document().getId();
        Map<String, Object> parkingDetails = new HashMap<>();
        parkingDetails.put("nameOfParking", parkName);
        parkingDetails.put("priceOfParking", String.valueOf(amount));
        parkingDetails.put("timing", timings);
        parkingDetails.put("carNumber", carNumber.toUpperCase());
        parkingDetails.put("paymentMethod", "Using RazorPay");
        parkingDetails.put("paymentStatus", "Paid");
        parkingDetails.put("bookingDate", date);
        parkingDetails.put("bookingTime", time);
        parkingDetails.put("email", email);
        parkingDetails.put("id", documentId);
        parkingRef.document(documentId).set(parkingDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Booking details saved successfully in your bookings.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error saving booking details", e);
                        Toast.makeText(UpiActivity.this, "Something went wrong, failed to save booking details.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPaymentError(int i, String s) {
        // payment failure handler
        Log.e(TAG, "error code " + String.valueOf(i) + " --Transaction failed-- " + String.valueOf(s));
        try {
            Toast.makeText(this, "Transaction failed.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("onPaymentError", "Exception in onPaymentError");
        }
    }

    @Override
    public void onConnectivityChanged(boolean isConnected) {
        if (isConnected) {
            // Internet connection is available
            if (noInternetLayout.getVisibility() == View.VISIBLE) {
                // Animate the disappearance of noInternetLayout using swipe up animation
                ObjectAnimator anim = ObjectAnimator.ofFloat(noInternetLayout, "translationY", 0f, -noInternetLayout.getHeight());
                anim.setDuration(300); // Set the animation duration in milliseconds

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        noInternetLayout.setVisibility(View.GONE);
                        noInternetLayout.setTranslationY(0f); // Reset translationY for future animations
                    }
                });

                anim.start();
            }
        } else {
            // No internet connection
            if (noInternetLayout.getVisibility() != View.VISIBLE) {
                // Animate the appearance of noInternetLayout using swipe down animation
                noInternetLayout.setVisibility(View.VISIBLE);
                noInternetLayout.setTranslationY(-noInternetLayout.getHeight());

                ObjectAnimator anim = ObjectAnimator.ofFloat(noInternetLayout, "translationY", -noInternetLayout.getHeight(), 0f);
                anim.setDuration(300); // Set the animation duration in milliseconds

                anim.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

}
