package com.example.charlo.ecommerce;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.charlo.ecommerce.fragment.PaymentSuccessDialog;
import com.google.android.gms.maps.GoogleMap;


public class ContactUs_Activity extends AppCompatActivity {
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

    }


    public void Complain(View view) {
        PaymentSuccessDialog dialog = new PaymentSuccessDialog();
        dialog.show(getSupportFragmentManager(),"Contact us");
    }
}
