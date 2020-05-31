package com.example.charlo.ecommerce.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.charlo.ecommerce.ContactUs_Activity;
import com.example.charlo.ecommerce.HomeActivity;
import com.example.charlo.ecommerce.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;


public class PaymentSuccessDialog extends DialogFragment {
    EditText name, message, email, phone;
    Button btnSend;
    private View root_view;
    DatabaseReference Contactus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_payment_dialog_success, container, false);
        phone = root_view.findViewById(R.id.msgPhone);
        email = root_view.findViewById(R.id.msgEmail);
        name = root_view.findViewById(R.id.msgName);
        message = root_view.findViewById(R.id.msgMessage);
        btnSend = root_view.findViewById(R.id.btnsend);

        ((FloatingActionButton) root_view.findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //finish();

                dismiss();
            }
        });

        btnSend.setOnClickListener(v -> {

            String saveCurrentDate, saveCurrentTime,productRandomKey;

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calendar.getTime());

            productRandomKey = saveCurrentDate + saveCurrentTime;

            Contactus = FirebaseDatabase.getInstance().getReference().child("Contact us Messages");

            final HashMap<String, Object> cartMap = new HashMap<>();
            cartMap.put("Name", name.getText().toString());
            cartMap.put("Message", message.getText().toString());
            cartMap.put("Email", email.getText().toString());
            cartMap.put("Phone", phone.getText().toString());
            cartMap.put("date", saveCurrentDate);
            cartMap.put("time", saveCurrentTime);



            Contactus.child(productRandomKey).updateChildren(cartMap)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(getActivity(), ContactUs_Activity.class);
                            startActivity(intent);
                            Toasty.success(getActivity(),"Message Successfully Saved",Toasty.LENGTH_LONG).show();

                        }

                    });

        });


        return root_view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
