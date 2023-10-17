package com.example.parkinger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    Button buttonReg;

    TextView authFailedText;

    TextView authPassedText;
    TextView textView;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.cPassword);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        authFailedText = findViewById(R.id.authFailText);
        authPassedText = findViewById(R.id.authPassText);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, confirmPassword;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                confirmPassword = String.valueOf(editTextConfirmPassword.getText());

                if (!email.matches(emailPattern)){
                    editTextEmail.setError("Enter a valid email.");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (password.length()<6){
                    editTextPassword.setError("Password should contain at least 6 digits.");
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!password.equals(confirmPassword)){
                    editTextConfirmPassword.setError("Password and Confirm Password are not same.");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    editTextEmail.setError("Enter email.");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    editTextPassword.setError("Enter Password");
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)){
                    editTextConfirmPassword.setError("Enter Confirm Password");
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    authPassedText.setVisibility(View.VISIBLE);
                                    authPassedText.setText("Registered successfully");
                                    authFailedText.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    authFailedText.setVisibility(View.VISIBLE);
                                    authPassedText.setVisibility(View.INVISIBLE);
                                    if (Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()).contains("The email address is already in use by another account")){
                                        authFailedText.setText("Account already exists, please try login");
                                    } else {
                                        authFailedText.setText("Registration failed, Please try again");
                                    }
                                }
                            }
                        });
            }
        });
    }
}