package com.example.parkinger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.parkinger.databinding.FragmentFirstBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirstFragment extends Fragment implements OnMapReadyCallback {

    private FragmentFirstBinding binding;

    private SupportMapFragment smf;
    private FusedLocationProviderClient client;

    double lat, lng;
    String parkName, parkingTimings;
    int slotsAvailable, parkingPrice;
    FirebaseFirestore db;

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        client = LocationServices.getFusedLocationProviderClient(requireContext());

        db = FirebaseFirestore.getInstance();

        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        smf.getMapAsync(this);
    }

    private void showLocationSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enable Location")
                .setMessage("Location services are disabled. Please enable location services to use this app.")
                .setPositiveButton("Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    requireActivity().finish();
                })
                .setCancelable(false)
                .setOnDismissListener(dialog -> {

                })
                .show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(true);
//        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
        googleMap.getUiSettings().setAllGesturesEnabled(true);


        try {
            CollectionReference parkingCollectionRef = db.collection("MarkersData");
            parkingCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        lat = Double.parseDouble(String.valueOf(documentSnapshot.getDouble("lat")));
                        lng = Double.parseDouble(String.valueOf(documentSnapshot.getDouble("lng")));
                        parkName = documentSnapshot.getString("parkName");
                        parkingTimings = documentSnapshot.getString("parkingTimings");
                        Number parkingPriceNumber = documentSnapshot.getLong("parkingPrice");
                        assert parkingPriceNumber != null;
                        parkingPrice = parkingPriceNumber.intValue();
                        Number slotsAvailableNumber = documentSnapshot.getLong("slotsAvailable");
                        assert slotsAvailableNumber != null;
                        slotsAvailable = slotsAvailableNumber.intValue();


                        LatLng position = new LatLng(lat, lng);
                        MarkerOptions markerOptions = new MarkerOptions().position(position).title(parkName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        Marker marker = googleMap.addMarker(markerOptions);
                        // Create a new map to store the marker's data
                        Map<String, Object> markerData = new HashMap<>();
                        markerData.put("parkName", parkName);
                        markerData.put("parkingTimings", parkingTimings);
                        markerData.put("parkingPrice", parkingPrice);
                        markerData.put("slotsAvailable", slotsAvailable);
                        assert marker != null;
                        marker.setTag(markerData);

                        // Set the info window adapter
                        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                            @Override
                            public View getInfoWindow(@NonNull Marker marker) {
                                // Return null here to use the default InfoWindow frame
                                return null;
                            }

                            @SuppressLint("SetTextI18n")
                            @Override
                            public View getInfoContents(@NonNull Marker marker) {
                                // Inflate the custom layout for the InfoWindow
                                @SuppressLint("InflateParams")
                                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

                                // Find the views in the custom layout
                                TextView textView = view.findViewById(R.id.title);
                                TextView textViewDescription = view.findViewById(R.id.description);

                                // Retrieve the marker's data
                                Map<String, Object> markerData = (Map<String, Object>) marker.getTag();
                                assert markerData != null;
                                String parkName = (String) markerData.get("parkName");
                                int slotsAvailable = (int) markerData.get("slotsAvailable");
                                int parkingPrice = (int) markerData.get("parkingPrice");
                                String parkingTimings = (String) markerData.get("parkingTimings");

                                textView.setText(parkName);
                                textViewDescription.setText("Slots available: " + slotsAvailable + "\n" + "Price: ₹" + parkingPrice +  "/hr" +"\n" + "Timings: " + parkingTimings);

                                return view;
                            }
                        });


                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(@NonNull Marker marker) {
                                Map<String, Object> markerData = (Map<String, Object>) marker.getTag();
                                String parkName = (String) markerData.get("parkName");
                                int slotsAvailable = (int) markerData.get("slotsAvailable");
                                int parkingPrice = (int) markerData.get("parkingPrice");
                                String parkingTimings = (String) markerData.get("parkingTimings");


                                Intent intent = new Intent(getContext(), BookParkingActivity.class);
                                intent.putExtra("LATITUDE", lat);
                                intent.putExtra("LONGITUDE", lng);
                                intent.putExtra("PARKING_NAME", parkName);
                                intent.putExtra("PARKING_PRICE", parkingPrice);
                                intent.putExtra("PARKING_SLOTS", slotsAvailable);
                                intent.putExtra("PARKING_TIMINGS", parkingTimings);
                                startActivity(intent);
                            }
                        });
                    }
                }
            });

            LatLng defaultLocationDelhi = new LatLng(28.7041, 77.1025);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocationDelhi));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(9f));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationSettingsDialog();
            getContext();
        }

        // Enable my location button and display my location on the map
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Get the user's last known location and add a marker to the map
        client.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f)); // Change the zoom level as per your requirement
                }
                else {
                    LatLng defaultLocationDelhi = new LatLng(28.7041, 77.1025);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocationDelhi));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(11f));
                }
            }
        });
    }
}