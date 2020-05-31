package com.example.charlo.ecommerce.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.charlo.ecommerce.CheckOut;
import com.example.charlo.ecommerce.Prevalent.Prevalent;
import com.example.charlo.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentShipping extends Fragment {
    EditText email, name, phone, address, Saddress,country, zip, city, state;
    Button ship,pay;
    private View rootView;
    private String totalAmount = "";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


    public FragmentShipping() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fragment_shipping, container, false);
        email = rootView.findViewById(R.id.ship_email);
        name = rootView.findViewById(R.id.ship_name);
        phone = rootView.findViewById(R.id.ship_phone);
        address = rootView.findViewById(R.id.address);
        country = rootView.findViewById(R.id.country);
        zip = rootView.findViewById(R.id.zip);
        city = rootView.findViewById(R.id.city);
        //state = rootView.findViewById(R.id.state);
        ship = rootView.findViewById(R.id.btncontact);

        ship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShippingAccount();
            }
        });







//



        return  rootView;
    }

    private void ShippingAccount() {

        String sname = name.getText().toString().trim();
        String sphone = phone.getText().toString().trim();
        String saddress = address.getText().toString().trim();
        String scity = city.getText().toString().trim();
        String szip = zip.getText().toString().trim();
        String scountry = country.getText().toString().trim();
        String Smail = email.getText().toString().trim();




        if(TextUtils.isEmpty(sname)){
            name.setError("Enter your full name");

        }else if(TextUtils.isEmpty(Smail)  ){
            email.setError("Enter your an Email");

        } else if(TextUtils.isEmpty(sphone) && sphone.length()==10){
            phone.setError("Enter your phone number");

        }else if(TextUtils.isEmpty(saddress)){
            address.setError("Enter your complete address");

        }else if(TextUtils.isEmpty(scity)){
            city.setError("Enter the name of city you live in");

        }else if(TextUtils.isEmpty(szip)){
            zip.setError("Enter your zip code");

        }else if(TextUtils.isEmpty(scountry)){
            country.setError("Enter the name of your Country");

        }else if(TextUtils.isEmpty(Smail)){
            email.setError("Enter the name of your Email");

        }else{
            shippingDetails(sname,sphone,saddress,scity,saddress,Smail,szip);

        }
    }

    private  void shippingDetails(final String sname, final String sphone, final String saddress,final String scity,final String scountry
        ,final String Smail, final String szip){

        String saveCurrentDate, saveCurrentTime;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference OrdersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String,Object> ordersMap = new HashMap<>();
        ordersMap.put("name", sname);
        ordersMap.put("phone", sphone);
        ordersMap.put("address", saddress);
        ordersMap.put("city", scity);
        ordersMap.put("zip",szip);
        ordersMap.put("country", scountry);
        ordersMap.put("Email", Smail);
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("state", "not shipped");

        OrdersRef.updateChildren(ordersMap).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseDatabase.getInstance().getReference()
                        .child("Cart List")
                        .child("User View")
                        .child(Prevalent.currentOnlineUser.getPhone())
                        .removeValue()
                        .addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){

                                Snackbar snackbar = Snackbar.make(rootView.findViewById(R.id.rootlayout),"Shipping Details Uploaded successfully",
                                        Snackbar.LENGTH_LONG);
                                snackbar.show();
                                Toasty.success(getContext(),"",Toasty.LENGTH_LONG,true).show();

                            }
                        });
            }
        });



}

}
