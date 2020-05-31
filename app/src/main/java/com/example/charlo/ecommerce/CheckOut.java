package com.example.charlo.ecommerce;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;
import com.example.charlo.ecommerce.Activity.CartActivity;
import com.example.charlo.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class CheckOut extends AppCompatActivity {
    Daraja daraja;
    String phoneNumber;
    Button btnsend, btnDelivery;
    EditText editTextPhoneNumber;
    private String totalAmount = "";
    private int code;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        setTitle("MPESA PAYMENT");
        btnDelivery = findViewById(R.id.onpayment);
        btnsend = findViewById(R.id.sendButton);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        totalAmount = getIntent().getStringExtra("Amount");

        btnDelivery.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ConfirmOrderActivity.class);
            intent.putExtra("Amount",String.valueOf(totalAmount));
            startActivity(intent);
            finish();
        });

        daraja = Daraja.with("Uku3wUhDw9z0Otdk2hUAbGZck8ZGILyh", "JDjpQBm5HpYwk38b", new DarajaListener<AccessToken>() {
            @Override
            public void onResult(@NonNull AccessToken accessToken) {
                Log.i(CheckOut.this.getClass().getSimpleName(), accessToken.getAccess_token());
                Toast.makeText(CheckOut.this, "TOKEN : " + accessToken.getAccess_token(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.e(CheckOut.this.getClass().getSimpleName(), error);
            }
        });

        btnsend.setOnClickListener(v -> {
            phoneNumber = editTextPhoneNumber.getText().toString().trim();

            if (TextUtils.isEmpty(phoneNumber)) {
                editTextPhoneNumber.setError("Please Provide a Phone Number");
                return;

            }

            LNMExpress lnmExpress = new LNMExpress(
                    "174379",
                    "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",  //https://developer.safaricom.co.ke/test_credentials
                    TransactionType.CustomerPayBillOnline,
                    totalAmount,
                    "254719490801",
                    "174379",
                    phoneNumber,
                    "http://mycallbackurl.com/checkout.php",
                    "001ABC",
                    "Goods Payment"
            );

            daraja.requestMPESAExpress(lnmExpress,
                    new DarajaListener<LNMResult>() {
                        @Override
                        public void onResult(@NonNull LNMResult lnmResult) {
                            final DatabaseReference OrdersRef = FirebaseDatabase.getInstance().getReference()
                                    .child("Orders")
                                    .child(Prevalent.currentOnlineUser.getPhone());

                            HashMap<String,Object> ordersMap = new HashMap<>();
                            ordersMap.put("amount",totalAmount);
                            ordersMap.put("ID",lnmResult.CheckoutRequestID);
                            ordersMap.put("Code",lnmResult.ResponseCode);


                            OrdersRef.updateChildren(ordersMap).addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Cart List")
                                            .child("User View")
                                            .child(Prevalent.currentOnlineUser.getPhone())
                                            .removeValue()
                                            .addOnCompleteListener(task1 -> {
                                                if(task1.isSuccessful()){
                                                }
                                            });
                                }
                            });

                            Log.i(CheckOut.this.getClass().getSimpleName(), lnmResult.ResponseDescription);
                           // Toast.makeText(CheckOut.this, lnmResult.ResponseDescription, Toast.LENGTH_SHORT).show();
                            code = Integer.parseInt(lnmResult.ResponseCode);
                            if (code == 0) {

                                startActivity(new Intent(getApplicationContext(),ConfirmOrderActivity.class));

                            }

                        }

                        @Override
                        public void onError(String error) {
                            Log.i(CheckOut.this.getClass().getSimpleName(), error);
                            Toasty.error(CheckOut.this,"Insufficient Balance in your Mpesa Account",Toast.LENGTH_LONG,true).show();

                            startActivity(new Intent(getApplicationContext(), CartActivity.class ));
                        }
                    }
            );
        });

    }
}
