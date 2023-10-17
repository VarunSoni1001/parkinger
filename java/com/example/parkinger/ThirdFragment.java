package com.example.parkinger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;
import java.util.Objects;

public class ThirdFragment extends Fragment {

    private FirebaseAuth mAuth;
    private Button button;
    private TextView textView, buttonDelete;
    private FirebaseUser user;
    private String emailText;
    private FirebaseFirestore db;
    private CollectionReference parkingRef;

    public ThirdFragment() {
        // require an empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        mAuth = FirebaseAuth.getInstance();
        button = view.findViewById(R.id.logout);
        textView = view.findViewById(R.id.user_details);
        user = mAuth.getCurrentUser();
        ProgressBar progressBar = view.findViewById(R.id.progressBarDelete);
        progressBar.setVisibility(View.INVISIBLE);

        if (user == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            emailText = user.getEmail();
            textView.setText("Logged in from: " + emailText);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        buttonDelete = view.findViewById(R.id.delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserDataAndAccount();
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void logoutUser() {
        if (user != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }, 1500);
        }
    }

    // best working function
    private void deleteUserDataAndAccount() {
        if (user != null) {
            db = FirebaseFirestore.getInstance();
            parkingRef = db.collection("Booked Parkings");
            parkingRef.whereEqualTo("email", String.valueOf(emailText)).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            WriteBatch batch = db.batch();
                            List<DocumentSnapshot> ds = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : ds) {
                                batch.delete(snapshot.getReference());
                            }
                            if (ds.size() > 0) {
                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Successfully deleted user's data.", Toast.LENGTH_SHORT).show();
                                        deleteAccount();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error deleting user's data.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                deleteAccount();
                            }
                        }
                    });
        }
    }

    private void deleteAccount() {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // User deleted successfully
                    Toast.makeText(getActivity(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                    // Redirect to login screen or do something else
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    // Error occurred while deleting user
                    try {
                        Toast.makeText(getActivity(), "Something went wrong, account was not deleted.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        // Show error message or handle the error
                        Log.e("TAG", "Error while deleting the account - " + e.getMessage());
                    }
                }
            }
        });
    }
}

// working fine:
//    private void deleteUser() {
//        if (user != null) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    db = FirebaseFirestore.getInstance();
//                    parkingRef = db.collection("Booked Parkings");
//                    parkingRef.whereEqualTo("email", String.valueOf(emailText)).get()
//                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                @Override
//                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                    WriteBatch batch = db.batch();
//                                    List<DocumentSnapshot> ds = queryDocumentSnapshots.getDocuments();
//                                    for (DocumentSnapshot snapshot : ds) {
//                                        batch.delete(snapshot.getReference());
//                                    }
//                                    batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            // delete user's account after deleting their data
//                                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        // User deleted successfully
//                                                        Toast.makeText(getActivity(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
//                                                        // Redirect to login screen or do something else
//                                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
//                                                        startActivity(intent);
//                                                        getActivity().finish();
//
//                                                        // Show toast for data deletion
//                                                        Toast.makeText(getContext(), "Successfully deleted user's data.", Toast.LENGTH_SHORT).show();
//                                                    } else {
//                                                        // Error occurred while deleting user
//                                                        try {
//                                                            Toast.makeText(getActivity(), "Something went wrong, account was not deleted.", Toast.LENGTH_SHORT).show();
//                                                        } catch (Exception e) {
//                                                            // Show error message or handle the error
//                                                            Log.e("TAG", "Error while deleting the account - " + e.getMessage());
//                                                        }
//                                                    }
//                                                }
//                                            });
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(getContext(), "Error deleting user's data.", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            });
//                }
//            }, 1500);
//        }
//    }