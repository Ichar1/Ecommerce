package com.example.charlo.ecommerce.Aunthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charlo.ecommerce.R;
import com.example.charlo.ecommerce.Security;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class Register extends AppCompatActivity {

    EditText InputName, InputPhoneNumber, InputPassword, cInputPassword;
    Button CreateAccountButton;
    ProgressDialog loadingBar;
    ImageView splash;
    Animation atg, fade_in;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        login = findViewById(R.id.register);
        splash = findViewById(R.id.splash);

        fade_in = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        atg = AnimationUtils.loadAnimation(this,R.anim.atg);

        splash.startAnimation(atg);
        login.startAnimation(fade_in);

        login = findViewById(R.id.login_sigin);
        CreateAccountButton = findViewById(R.id.register_btn);
        InputName = findViewById(R.id.register_username_input);
        InputPhoneNumber = findViewById(R.id.register_phone_number_input);
        InputPassword = findViewById(R.id.register_password_input);
        cInputPassword = findViewById(R.id.register_cpassword_input);



        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(v -> {
            computeMD5Hash(InputPassword.toString());
            CreateAccount();
        });

        login.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Login.class)));

    }

    public void computeMD5Hash(String password) {

        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }

            //result.setText(MD5Hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void CreateAccount(){

        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        String cpassword = cInputPassword.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toasty.error(this, "Enter Username",Toast.LENGTH_LONG,true).show();
        }
        else if(TextUtils.isEmpty(phone) || InputPhoneNumber.length()<= 10) {
            Toasty.error(this, "Enter Phone number not less than 10 numbers",Toast.LENGTH_LONG,true).show();
        }
        else if(TextUtils.isEmpty(password)|| cpassword.length()<6){
            Toasty.error(this, "Enter Password more than 6 characters",Toast.LENGTH_LONG,true).show();
        }else if(!cpassword.equals(password)){
            Toasty.error(this,"Password not matching",Toasty.LENGTH_LONG,true).show();
        }
        else{
            loadingBar.setTitle("create account");
            loadingBar.setMessage("checking your credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            validatephoneNumber(name,phone,password);
        }

    }

    private void validatephoneNumber(final String name, final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("Users").child(phone).exists()){
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    try {
                        userdataMap.put("password", Security.encrypt(password));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    userdataMap.put("name", name);

                    RootRef.child("User").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()){
                                    Toasty.success(Register.this,"Your account has been successfully saved",Toast.LENGTH_SHORT,true).show();
                                    loadingBar.dismiss();
                                }else{
                                    loadingBar.dismiss();
                                    Toasty.error(Register.this,"Network error: Try again after sometime",Toast.LENGTH_SHORT,true).show();
                                }
                            });

                }else{
                    Toasty.info(Register.this,"This "+ phone + " already exists",Toast.LENGTH_SHORT,true).show();
                    loadingBar.dismiss();
                    Toasty.info(Register.this,"Please try again using another phone number",Toast.LENGTH_SHORT,true).show();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
