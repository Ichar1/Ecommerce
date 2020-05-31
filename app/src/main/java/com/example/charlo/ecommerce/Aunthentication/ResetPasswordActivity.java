package com.example.charlo.ecommerce.Aunthentication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.charlo.ecommerce.Prevalent.Prevalent;
import com.example.charlo.ecommerce.R;
import com.example.charlo.ecommerce.Security;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class ResetPasswordActivity extends AppCompatActivity {
    TextView phone_no, question1, question2;
    Button Resetpwd;
    TextView title1, title2;
    ImageView imgReset;
    Animation atg ,fade_in;

    private String check = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        check = getIntent().getStringExtra("check");

        phone_no = findViewById(R.id.phone_number);
        question1 = findViewById(R.id.qn1);
        question2 = findViewById(R.id.qn2);
        Resetpwd = findViewById(R.id.btnresetpwd);
        title1 = findViewById(R.id.rQn);
        title2 = findViewById(R.id.rQn2);
        imgReset = findViewById(R.id.reset);


        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        atg = AnimationUtils.loadAnimation(this,R.anim.atg);

        imgReset.startAnimation(atg);
        //title2.startAnimation(fade_in);





    }

    @Override
    protected void onStart() {
        super.onStart();
        phone_no.setVisibility(View.GONE);
        if(check.equals("settings")){
            title1.setText("SET QUESTIONS");
            title2.setText("Set Answers For The Following Questions");

            getAnswers();
            Resetpwd.setOnClickListener(v -> {
                setAnswers();
            });


        }else if(check.equals("login")){
            phone_no.setVisibility(View.VISIBLE);
            Resetpwd.setOnClickListener(v -> setQuestions());

        }
    }

    private void setAnswers(){


        String ans1 = question1.getText().toString().toLowerCase();
        String ans2 = question2.getText().toString().toLowerCase();

        if(question1.equals("") && question2.equals("")){
            Toasty.info(getApplicationContext(),"Please answer the two questions",Toasty.LENGTH_LONG).show();
        }else {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("User")
                    .child(Prevalent.currentOnlineUser.getPhone());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Answer 1",ans1);
            hashMap.put("Answer 2",ans2);
            ref.child("Security Questions").updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toasty.success(getApplicationContext(),"Answers Have Been saved Successfully",Toasty.LENGTH_LONG,true).show();
                    }

                }
            });
        }

    }

    private void getAnswers(){

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child("User")
                .child(Prevalent.currentOnlineUser.getPhone());

        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String ans1 = dataSnapshot.child("Answer 1").getValue().toString();
                    String ans2 = dataSnapshot.child("Answer 2").getValue().toString();

                    question1.setText(ans1);
                    question2.setText(ans2);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setQuestions(){
        String phone = phone_no.getText().toString();
        String answer1 = question1.getText().toString().trim();
        String answer2 = question2.getText().toString().trim();

        if(!phone.equals("") && !answer1.equals("") && !answer2.equals(""))
        {

            final DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("User")
                    .child(phone);

            ref.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if(dataSnapshot.exists())
                    {
                        // String phone = dataSnapshot.child("phone").getValue().toString();
                        if (dataSnapshot.hasChild("Security Questions"))
                        {
                            String ans1 = dataSnapshot.child("Security Questions").child("Answer 1").getValue().toString();
                            String ans2 = dataSnapshot.child("Security Questions").child("Answer 2").getValue().toString();

                            if (!ans1.equals(answer1))
                            {
                                Toasty.error(getApplicationContext(), "Your answer to the first question is wrong", Toasty.LENGTH_LONG, true).show();
                            } else if (!ans2.equals(answer2))
                            {
                                Toasty.error(getApplicationContext(), "Your answer to the Second question is wrong", Toasty.LENGTH_LONG, true).show();
                            } else{

                                AlertDialog builder = new AlertDialog.Builder(ResetPasswordActivity.this).create();
                                builder.setTitle("New Password");

                                LayoutInflater inflater = getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.new_password,null);
                                builder.setCancelable(false);

                                // Set the custom layout as alert dialog view



                                EditText editText = dialogView.findViewById(R.id.new_password);
                                Button save = dialogView.findViewById(R.id.btnchange_password);
                                Button cancel = dialogView.findViewById(R.id.cancel_password);



                                save.setOnClickListener(v -> {
                                    String new_ps = editText.getText().toString().trim();
                                    String pwd= null;
                                    try {
                                        pwd = Security.encrypt(new_ps);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                 if (!pwd.equals("")) {
                                        ref.child("password")
                                                .setValue(pwd)
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        Toasty.success(getApplicationContext(), "Password Changed Successfully", Toasty.LENGTH_LONG).show();
                                                        startActivity(new Intent(getApplicationContext(),Login.class));
                                                    }

                                                });
                                    }

                                });

                                cancel.setOnClickListener(v -> builder.dismiss());

                                builder.setView(dialogView);
                                builder.show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Toasty.error(getApplicationContext(),"Please enter all the details",Toasty.LENGTH_LONG).show();
        }


    }
}
