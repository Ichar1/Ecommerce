package com.example.charlo.ecommerce.Activity;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.charlo.ecommerce.Prevalent.Prevalent;
import com.example.charlo.ecommerce.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class OrderReceived_Activity extends AppCompatActivity {
    Button btnConfirm;
    EditText edphone, edmessage, edcomplain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_receieved);
        setTitle("Confirm Order Received");

        edmessage = findViewById(R.id.order_con);
        edphone = findViewById(R.id.phone);
        edcomplain = findViewById(R.id.complain);
        btnConfirm = findViewById(R.id.btnsubmit);

        edphone.setText(Prevalent.currentOnlineUser.getPhone());


        btnConfirm.setOnClickListener(v -> orderRecieved());
    }



    private void orderRecieved(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Confirmation of Orders");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("phone", edphone.getText().toString());
        userMap. put("confirmation", edmessage.getText().toString());
        userMap. put("Complement", edcomplain.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.rootSnack), "Submitted Successfully",Snackbar.LENGTH_INDEFINITE   );
        //snackbar.setAction(R.string.undo, this);
        snackbar.show();


    }
}
