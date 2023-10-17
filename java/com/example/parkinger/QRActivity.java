package com.example.parkinger;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRActivity extends AppCompatActivity {

    ImageView qrCode;
    TextView paymentStatus, carNumberToQR;

    String carNumber, payment_status;
    TextView openMapsApp;
    double longitude, latitude;

    FirebaseFirestore db;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qractivity);
        qrCode = findViewById(R.id.qrCode);
        carNumberToQR = findViewById(R.id.carNumberToQR);
        paymentStatus = findViewById(R.id.paymentStatus);
        openMapsApp = findViewById(R.id.openGoogleMaps);

        Intent intent = getIntent();

        latitude = intent.getDoubleExtra("LATITUDE", 28.6139);
        longitude = intent.getDoubleExtra("LONGITUDE", 77.2090);
        payment_status = intent.getStringExtra("PAYMENT_STATUS");
        carNumber = intent.getStringExtra("CAR_NUMBER_QR");
        paymentStatus.setText("Payment status: " + payment_status);
        carNumberToQR.setText("Car number: " + carNumber.toUpperCase());

        generateQR();

        openMapsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(QRActivity.this, "Google Maps app not installed, Please install it then try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void generateQR() {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(carNumber.toUpperCase(), BarcodeFormat.QR_CODE, 800, 800);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }
}