package com.example.parkinger;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SecondFragment extends Fragment {

    RecyclerView recyclerView;
    TextView noBooking, booking, haveBooking;
    List<Parking> parkingList;
    ParkingAdapter parkingAdapter;
    FirebaseFirestore db;
    String userEmail;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        userEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        haveBooking = view.findViewById(R.id.tvHaveBooking);
        booking = view.findViewById(R.id.tvBooking);
        noBooking = view.findViewById(R.id.tvNoBooking);
        recyclerView = view.findViewById(R.id.recyclerView);


        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                booking.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                if (parkingList.isEmpty()) {
                    noBooking.setVisibility(View.VISIBLE);
                } else {
                    noBooking.setVisibility(View.GONE);
                    haveBooking.setVisibility(View.VISIBLE);
                }
                parkingList.clear();
                parkingAdapter.notifyDataSetChanged();

                // Add a delay of 2 seconds before fetching the data
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        db.collection("Booked Parkings")
                                .whereEqualTo("email", userEmail)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Parking parking = document.toObject(Parking.class);
                                            parkingList.add(parking);
                                            booking.setVisibility(View.VISIBLE);
                                            recyclerView.setVisibility(View.VISIBLE);
                                            noBooking.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.GONE);
                                            haveBooking.setVisibility(View.GONE);
                                        }
                                        parkingAdapter.notifyDataSetChanged();
                                        swipeRefreshLayout.setRefreshing(false);
                                        if (parkingList.isEmpty()) {
                                            noBooking.setVisibility(View.VISIBLE);
                                        } else {
                                            noBooking.setVisibility(View.GONE);
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                });
                    }
                }, 1000);
            }
        });



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        parkingList = new ArrayList<>();
        parkingAdapter = new ParkingAdapter(parkingList);

        recyclerView.setAdapter(parkingAdapter);

        db = FirebaseFirestore.getInstance();

        progressBar = view.findViewById(R.id.progress_bar);

        if (parkingList.isEmpty()) {
            noBooking.setVisibility(View.VISIBLE);
        } else {
            noBooking.setVisibility(View.GONE);
            haveBooking.setVisibility(View.VISIBLE);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                // load data here
                progressBar.setVisibility(View.VISIBLE);
                noBooking.setVisibility(View.GONE);
                haveBooking.setVisibility(View.VISIBLE);
                db.collection("Booked Parkings")
                        .whereEqualTo("email", userEmail)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Parking parking = document.toObject(Parking.class);
                                    parkingList.add(parking);
                                    booking.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    noBooking.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    haveBooking.setVisibility(View.GONE);
                                }
                                parkingAdapter.notifyDataSetChanged();
                                if (parkingList.isEmpty()) {
                                    noBooking.setVisibility(View.VISIBLE);
                                    booking.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    haveBooking.setVisibility(View.GONE);
                                } else {
                                    noBooking.setVisibility(View.GONE);
                                }
                            } else {
                                noBooking.setVisibility(View.VISIBLE);
                                booking.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                haveBooking.setVisibility(View.GONE);
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        });
            }
        }, 0);
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(parkingAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

}
