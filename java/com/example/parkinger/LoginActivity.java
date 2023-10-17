package com.example.parkinger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class LoginActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    TextView authFailedText, authPassedText, textView, forgetPassText, adminLoginBtn;

    String emailPattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressBar progressBar;
    FirebaseAuth mAuth;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);
        authFailedText = findViewById(R.id.authFailText);
        authPassedText = findViewById(R.id.authPassText);
        forgetPassText = findViewById(R.id.forgetPassword);
        adminLoginBtn = findViewById(R.id.adminLoginActivity);

        adminLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, adminLogin.class);
                startActivity(intent);
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        forgetPassText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
            }
        });



        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        String email, password;
                        email = String.valueOf(editTextEmail.getText());
                        password = String.valueOf(editTextPassword.getText());

                        if (!email.matches(emailPattern)) {
                            editTextEmail.setError("Enter a valid email.");
                            progressBar.setVisibility(View.INVISIBLE);
                            return;
                        } else if (password.length() < 6) {
                            editTextPassword.setError("Password should contain at least 6 digits.");
                            progressBar.setVisibility(View.INVISIBLE);
                            return;
                        }

                        if (TextUtils.isEmpty(email)) {
                            editTextEmail.setError("Enter email");
                            return;
                        }
                        if (TextUtils.isEmpty(password)) {
                            editTextPassword.setError("Enter Password");
                            return;
                        }
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        if (task.isSuccessful()) {
                                            authPassedText.setVisibility(View.VISIBLE);
                                            authPassedText.setText("Logged in successfully");
                                            authFailedText.setVisibility(View.INVISIBLE);
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            authFailedText.setVisibility(View.VISIBLE);
                                            authPassedText.setVisibility(View.INVISIBLE);
                                            if (Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()).contains("There is no user record corresponding to this identifier. The user may have been deleted")) {
                                                authFailedText.setText("Account not found, please register");
                                            } else {
                                                authFailedText.setText("Login failed! Please check email and password, then try again");
                                            }
                                        }
                                    }
                                });
                    }

                }, 1500);
            }
        });
    }
}