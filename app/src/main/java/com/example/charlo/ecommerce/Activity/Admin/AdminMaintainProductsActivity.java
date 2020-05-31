package com.example.charlo.ecommerce.Activity.Admin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.charlo.ecommerce.Activity.Admin.AdminCategoryActivity;
import com.example.charlo.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    EditText ed_name,ed_price,ed_description;
    Button Submit, Delete;
    ImageView imageView;
    private String productID ="";
    private DatabaseReference MaintainceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        productID = getIntent().getStringExtra("pid");
        MaintainceRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

        ed_name = findViewById(R.id.maintain_name);
        ed_price = findViewById(R.id.maintain_price);
        ed_description = findViewById(R.id.maintain_description);
        imageView = findViewById(R.id.maintain_image);
        Submit = findViewById(R.id.btn_save_changes);
        Delete = findViewById(R.id.btn_delete_products);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });

        displayProductInfo();


    }

    private void deleteProduct() {
        MaintainceRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                startActivity(new Intent(getApplicationContext(), AdminCategoryActivity.class));
                Toast.makeText(getApplicationContext(),"this product has been deleted successfully",Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void saveChanges() {
        String name = ed_name.getText().toString();
        String price = ed_price.getText().toString();
        String description = ed_description.getText().toString();

        if(name.equals("")){

        }else if(price.equals("")){

        }else if(description.equals("")){

        }else {

            HashMap<String, Object> changesMap = new HashMap<>();
            changesMap.put("pid",productID);
            changesMap.put("pname",name);
            changesMap.put("price",price);
            changesMap.put("description",description);

            MaintainceRef.updateChildren(changesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Changes saved successfully",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AdminCategoryActivity.class));
                        finish();
                    }
                }
            });

        }
    }

    private void displayProductInfo() {

        MaintainceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name = dataSnapshot.child("pname").getValue().toString();
                    String price = dataSnapshot.child("price").getValue().toString();
                    String description = dataSnapshot.child("description").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();


                    ed_name.setText(name);
                    ed_price.setText(price);
                    ed_description.setText(description);
                    Picasso.get().load(image).into(imageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
