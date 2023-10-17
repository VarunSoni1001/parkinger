package com.example.parkinger;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ParkingViewHolder> {

    private final List<Parking> parkingList;

    String documentId;
    public ParkingAdapter(List<Parking> parkingList) {
        this.parkingList = parkingList;
    }

    @NonNull
    @Override
    public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_bookings, parent, false);
        return new ParkingViewHolder(view);
    }




    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ParkingViewHolder holder, int position) {
        Parking parking = parkingList.get(position);
        holder.nameOfParking.setText("Name of Parking: " + String.valueOf(parking.getNameOfParking()));
        holder.priceOfParking.setText("Price: ₹" + String.valueOf(parking.getPriceOfParking()));
        holder.timing.setText("Timing: " + String.valueOf(parking.getTiming()));
        holder.carNumber.setText("Car Number: " + String.valueOf(parking.getCarNumber()));
        holder.paymentMethod.setText("Payment Method: " + String.valueOf(parking.getPaymentMethod()));

        holder.paymentStatus.setText("Payment Status: " + String.valueOf(parking.getPaymentStatus()));
        if (parking.getPaymentStatus().equals("Pending")){
            holder.paymentStatus.setTextColor(Color.rgb(200, 0, 0));
            holder.paymentStatus.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        }
        else {
            holder.paymentStatus.setTextColor(Color.rgb(0, 150, 0));
            holder.paymentStatus.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        }

        holder.bookingDate.setText("Date: " + String.valueOf(parking.getBookingDate()));
        holder.bookingTime.setText("Time: " + String.valueOf(parking.getBookingTime()));
    }

    public void deleteItem(int position) {
        Parking parking = parkingList.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference parkingRef = db.collection("Booked Parkings");
        documentId = parking.getId();
        parkingList.remove(position);
        notifyItemRemoved(position);

        // Delete the document from FireStore
        parkingRef.document(documentId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }



    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    static class ParkingViewHolder extends RecyclerView.ViewHolder {
        TextView nameOfParking, priceOfParking, timing, carNumber, paymentMethod, paymentStatus, bookingDate, bookingTime;

        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingTime = itemView.findViewById(R.id.tv_parkingBookingTime);
            nameOfParking = itemView.findViewById(R.id.tv_parking_name);
            priceOfParking = itemView.findViewById(R.id.tv_parking_price);
            timing = itemView.findViewById(R.id.tv_parking_timing);
            carNumber = itemView.findViewById(R.id.tv_parking_car_number);
            paymentMethod = itemView.findViewById(R.id.tv_parking_payment_method);
            paymentStatus = itemView.findViewById(R.id.tv_parking_payment_status);
            bookingDate = itemView.findViewById(R.id.tv_parking_date);
        }
    }
}

