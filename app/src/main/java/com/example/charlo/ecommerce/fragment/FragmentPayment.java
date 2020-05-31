package com.example.charlo.ecommerce.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charlo.ecommerce.CheckOut;
import com.example.charlo.ecommerce.Model.Checkout;
import com.example.charlo.ecommerce.Prevalent.Prevalent;
import com.example.charlo.ecommerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPayment extends Fragment {
    private View rootView;
    EditText amountEt, nameEt, emailEt;
    Button send;
    private String totalAmount = "";


    public FragmentPayment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_fragment_payment, container, false);
        amountEt = rootView.findViewById(R.id.amount);
        nameEt = rootView.findViewById(R.id.pName);
        emailEt = rootView.findViewById(R.id.email);
        send = rootView.findViewById(R.id.btnpay);
        totalAmount = getActivity().getIntent().getStringExtra("Amount");
        amountEt.setText(totalAmount);
        Toasty.info(getContext(),"Amount Ksh." +totalAmount, Toast.LENGTH_SHORT,true).show();


        send.setOnClickListener(v -> {
            createPayment();

        });

        return rootView;


    }
    private void createPayment(){

        String pname = nameEt.getText().toString();
        String pamount = amountEt.getText().toString();
        String pemail = emailEt.getText().toString();

        if(TextUtils.isEmpty(pname)){
            nameEt.setError("Enter your full name");

        }else if(TextUtils.isEmpty(pamount)){
            amountEt.setError("Enter your phone number");

        }else if(TextUtils.isEmpty(pemail)){
            emailEt.setError("Enter an appropriate");

        }else{
            SavePayment(pname,pamount,pemail);
        }

    }

    private void SavePayment(final String pname, final String pamount, final String pemail){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Orders");

        HashMap<String, Object> userMap = new HashMap<>();
            userMap. put("name", pname);
            userMap. put("amount", pamount);
            userMap. put("email", pemail);
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        Snackbar snackbar = Snackbar.make(rootView.findViewById(R.id.rootlayout), R.string.success,Snackbar.LENGTH_INDEFINITE   );
        //snackbar.setAction(R.string.undo, this);
        snackbar.show();

        Toasty.success(getContext(),"" , Toast.LENGTH_LONG,true).show();

    }

}
