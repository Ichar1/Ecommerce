package com.example.charlo.ecommerce.Model;

public class Checkout {
    private String name;
    private String amount;
    private String address;
    private String phone;
    private String time;
    private String date;

    public Checkout() {
    }

    public Checkout(String name, String amount, String address, String phone, String time, String date) {
        this.name = name;
        this.amount = amount;
        this.address = address;
        this.phone = phone;
        this.time = time;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
