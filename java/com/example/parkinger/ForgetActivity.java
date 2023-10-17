package com.example.parkinger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class ForgetActivity extends AppCompatActivity {

    TextView authPassedText, authFailedText, goBackToLogin, goToRegister;
    ProgressBar progressBar;
    Button button, btnOpenMail;
    EditText editTextEmail;

    String email, emailID;

    FirebaseAuth mAuth;

    String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);


        authFailedText = findViewById(R.id.authFailText);
        authPassedText = findViewById(R.id.authPassText);
        progressBar = findViewById(R.id.progressBar);
        button = findViewById(R.id.btn_forget);
        editTextEmail = findViewById(R.id.email);
        goBackToLogin = findViewById(R.id.goBackToLogin);
        btnOpenMail = findViewById(R.id.btnOpenMailApp);
        goToRegister = findViewById(R.id.goToRegister);
        mAuth = FirebaseAuth.getInstance();

        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetActivity.this, RegisterActivity.class));
                finish();
            }
        });

        goBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgetActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnOpenMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMailApp();
            }
        });


        btnOpenMail.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private void openMailApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        startActivity(intent);
    }

    private void validateData() {

        btnOpenMail.setVisibility(View.GONE);
        button.setVisibility(View.VISIBLE);

        email = String.valueOf(editTextEmail.getText());

        if (!email.matches(emailPattern)){
            editTextEmail.setError("Enter a valid email.");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (email.isEmpty()){
            editTextEmail.setError("Enter email");
            progressBar.setVisibility(View.GONE);
        } else {
            forgetPassword();
        }
    }

    private void forgetPassword() {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {

            @SuppressLint("SetTextI18n")
            @Override

            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    authPassedText.setText("Reset link sent, check your mail");
                    authFailedText.setVisibility(View.INVISIBLE);
                    authPassedText.setVisibility(View.VISIBLE);
                    btnOpenMail.setVisibility(View.VISIBLE);
                    button.setVisibility(View.GONE);
//                    Intent intent = new Intent(ForgetActivity.this, LoginActivity.class);
//                    finish();
                } else{
                    authFailedText.setVisibility(View.VISIBLE);
                    authPassedText.setVisibility(View.INVISIBLE);
                    if (Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()).contains("There is no user record corresponding to this identifier. The user may have been deleted")){
                        authFailedText.setText("No account found with: "+ email);
                        authFailedText.setTypeface(Typeface.MONOSPACE);
                        editTextEmail.getText().clear();
                        button.setVisibility(View.VISIBLE);
                        btnOpenMail.setVisibility(View.GONE);
                    }else{
                        authFailedText.setText("Something went wrong");
                    }
                }
            }
        });
    }
}