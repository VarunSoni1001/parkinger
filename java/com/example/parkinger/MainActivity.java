package com.example.parkinger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity
        implements NavigationBarView.OnItemSelectedListener, NetworkChangeReceiver.ConnectivityChangeListener {
    BottomNavigationView bottomNavigationView;
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    private LinearLayout noInternetLayout;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkChangeReceiver = new NetworkChangeReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);

        bottomNavigationView
                = findViewById(R.id.bottomNavigationView);

        bottomNavigationView
                .setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.location);

        noInternetLayout = findViewById(R.id.noInternetLayout);


    }
    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bookings:
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in_left, R.anim.fade_out)
                        .replace(R.id.flFragment, secondFragment)
                        .commit();
                return true;

            case R.id.location:
                if (getSupportFragmentManager().findFragmentById(R.id.flFragment) == secondFragment) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out_left)
                            .replace(R.id.flFragment, firstFragment)
                            .commit();
                } else if (getSupportFragmentManager().findFragmentById(R.id.flFragment) == thirdFragment) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in_left, R.anim.fade_out)
                            .replace(R.id.flFragment, firstFragment)
                            .commit();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_in_left)
                            .replace(R.id.flFragment, firstFragment)
                            .commit();
                }
                return true;

            case R.id.profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out_left)
                        .replace(R.id.flFragment, thirdFragment)
                        .commit();
                return true;
        }
        return false;
    }
    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting() && networkInfo.isConnected();
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

