package com.example.charlo.ecommerce.Aunthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charlo.ecommerce.Activity.Admin.AdminCategoryActivity;
import com.example.charlo.ecommerce.HomeActivity;
import com.example.charlo.ecommerce.Model.Users;
import com.example.charlo.ecommerce.Prevalent.Prevalent;
import com.example.charlo.ecommerce.R;
import com.example.charlo.ecommerce.Security;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;


public class Login extends AppCompatActivity {

    private TextInputEditText InputPass, InputPhone;
    Button btnLogin;
    ProgressDialog loadingBar;
    private String parentDbName = "User";
    private CheckBox chkBoxRememberMe;
    TextView AdminLink, NotAdminLink, signup,login, txtforgotPass;
    ImageView splash;
    Animation atg, fade_in;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        txtforgotPass = findViewById(R.id.forgotpassword);

        splash = findViewById(R.id.splash);
        login = findViewById(R.id.login);

        fade_in = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        atg = AnimationUtils.loadAnimation(this,R.anim.atg);

        login.startAnimation(fade_in);
        splash.startAnimation(atg);

        signup = findViewById(R.id.signup_login);
        btnLogin = findViewById(R.id.btnLogin);
        InputPass =  findViewById(R.id.register_password_input);
        InputPhone = findViewById(R.id.register_phone_number);
        AdminLink = (TextView) findViewById(R.id.admin_panel_link);

        NotAdminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        loadingBar = new ProgressDialog(this);

        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_chkb);

        Login();
        Register();
        getAdmin();
        Admin();
        forgotpassword();



    }

    void forgotpassword(){
        txtforgotPass.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),ResetPasswordActivity.class);
            intent.putExtra("check", "login");
            startActivity(intent);

        });
    }

    void Login(){

        btnLogin.setOnClickListener(v -> loginAccount());

    }

    void Register(){

        signup.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Register.class)));

    }

    void getAdmin(){

        AdminLink.setOnClickListener(view -> {
            btnLogin.setText("Login Admin");
            AdminLink.setVisibility(View.INVISIBLE);
            NotAdminLink.setVisibility(View.VISIBLE);
            parentDbName = "Admin";
        });
    }

    void Admin(){

        NotAdminLink.setOnClickListener(view -> {
            btnLogin.setText("Login");
            AdminLink.setVisibility(View.VISIBLE);
            NotAdminLink.setVisibility(View.INVISIBLE);
            parentDbName = "User";
        });


    }

    private void loginAccount() {

        String phone = InputPhone.getText().toString();
        String password = InputPass.getText().toString();
        String pwd= null;
        try {
            pwd = Security.encrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(phone)) {
            Toasty.error(this, "Enter Phone number", Toast.LENGTH_LONG,true).show();
        } else if (TextUtils.isEmpty(password)) {
            Toasty.error(this, "Enter Password", Toast.LENGTH_LONG,true).show();
        } else {
            loadingBar.setTitle("create account");
            loadingBar.setMessage("checking your credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone, pwd);
        }


    }

    private void AllowAccessToAccount(final String phone, final String password)
    {
        if(chkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(parentDbName).child(phone).exists())
                {
                    Users usersData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Admin"))
                            {
                                Toasty.success(getApplicationContext(), "Welcome Admin, you are logged in Successfully...", Toast.LENGTH_SHORT,true).show();
                                loadingBar.dismiss();

                                startActivity(new Intent(getApplicationContext(), AdminCategoryActivity.class));
                            }
                            else if (parentDbName.equals("User"))
                            {
                                Toasty.success(getApplicationContext(), "logged in Successfully...", Toast.LENGTH_LONG,true).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toasty.error(getApplicationContext(), "Password is incorrect.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else
                {
                    Toasty.error(getApplicationContext(), "Account with this " + phone + " number do not exists.", Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}