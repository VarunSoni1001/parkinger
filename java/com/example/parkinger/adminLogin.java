package com.example.parkinger;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class adminLogin extends AppCompatActivity {

    TextInputEditText adminEmail, adminPassword;
    Button adminLoginBtn;
    TextView adminAuthFailText, adminAuthPassText, adminForgetPassword, userLoginBtn;
    ProgressBar adminProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);


        adminEmail = findViewById(R.id.adminEmail);
        adminPassword = findViewById(R.id.adminPassword);
        adminLoginBtn = findViewById(R.id.adminLoginBtn);
        adminAuthFailText = findViewById(R.id.adminAuthFailText);
        adminAuthPassText = findViewById(R.id.adminAuthPassText);
        userLoginBtn = findViewById(R.id.userLoginActivity);
        adminProgressBar = findViewById(R.id.adminProgressBar);

        adminAuthFailText.setVisibility(View.INVISIBLE);
        adminAuthPassText.setVisibility(View.INVISIBLE);
        adminProgressBar.setVisibility(View.INVISIBLE);

        userLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(adminLogin.this, LoginActivity.class));
            }
        });

        adminLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminProgressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adminLoginFn();
                    }
                }, 1500); // Delay of 2 seconds (2000 milliseconds)
            }
        });

        // Check if the entered username and password are correct
    }
    @SuppressLint("SetTextI18n")
    private void adminLoginFn(){
        String username = String.valueOf(adminEmail.getText()).trim();
        String password = String.valueOf(adminPassword.getText()).trim();
        if (username.equals("admin") && password.equals("admin")) {
            // Save the logged-in status in SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            // Redirect to the home screen or perform necessary actions
            startActivity(new Intent(adminLogin.this, adminActivity.class));
            finish();

            adminAuthPassText.setVisibility(View.VISIBLE);
            adminAuthFailText.setVisibility(View.INVISIBLE);
            adminProgressBar.setVisibility(View.INVISIBLE);
            adminAuthPassText.setText("Admin logged in successfully");

        } else {
            if (username.isEmpty()) {
                adminAuthFailText.setVisibility(View.VISIBLE);
                adminAuthPassText.setVisibility(View.INVISIBLE);
                adminProgressBar.setVisibility(View.INVISIBLE);
                adminAuthFailText.setText("Enter the login details");
                adminEmail.setError("Enter email");
            }
            if (password.isEmpty()){
                adminAuthFailText.setVisibility(View.VISIBLE);
                adminAuthPassText.setVisibility(View.INVISIBLE);
                adminProgressBar.setVisibility(View.INVISIBLE);
                adminAuthFailText.setText("Enter the login details");
                adminPassword.setError("Enter password");
            }
            else {
                adminAuthFailText.setVisibility(View.VISIBLE);
                adminAuthPassText.setVisibility(View.INVISIBLE);
                adminProgressBar.setVisibility(View.INVISIBLE);
                adminAuthFailText.setText("Invalid admin username and password");
            }
        }
    }
}