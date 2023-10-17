package com.example.parkinger;

public class Parking {
    private String nameOfParking;
    private String priceOfParking;
    private String timing;
    private String carNumber;
    private String paymentMethod;
    private String paymentStatus;
    private String email;
    private String bookingDate;
    private String date;
    private long timeStamp;
    private String bookingTime;
    private String id;

    public Parking(String nameOfParking, String priceOfParking, String timing, String carNumber, String paymentMethod, String paymentStatus, String email, String bookingDate, String bookingTime, String date, String timeStamp) {
        this.nameOfParking = nameOfParking;
        this.priceOfParking = priceOfParking;
        this.timing = timing;
        this.carNumber = carNumber;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.email = email;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.date = date;
        this.timeStamp = Long.parseLong(timeStamp);
    }

    public Parking() {
        // Required empty public constructor
    }
    public String getNameOfParking() {
        return nameOfParking;
    }

    public void setNameOfParking(String nameOfParking) {
        this.nameOfParking = nameOfParking;
    }

    public String getPriceOfParking() {
        return priceOfParking;
    }

    public void setPriceOfParking(String priceOfParking) {
        this.priceOfParking = priceOfParking;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void ParkingAdapter(){

    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
