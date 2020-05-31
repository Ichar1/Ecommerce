package com.example.charlo.ecommerce.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charlo.ecommerce.Activity.Admin.AdminCategoryActivity;
import com.example.charlo.ecommerce.Aunthentication.MainActivity;
import com.example.charlo.ecommerce.CartViewHolder;
import com.example.charlo.ecommerce.CheckOut;
import com.example.charlo.ecommerce.ConfirmOrderActivity;
import com.example.charlo.ecommerce.HomeActivity;
import com.example.charlo.ecommerce.Model.Cart;
import com.example.charlo.ecommerce.Model.Users;
import com.example.charlo.ecommerce.Prevalent.Prevalent;
import com.example.charlo.ecommerce.ProductsDetailActivity;
import com.example.charlo.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;


public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    Button nextProcess;
    TextView txtTotalAmount;
    TextView txtMsg;
    private int totalPrice = 0;
    private String parentDbName = "User";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setTitle("Cart");
        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        nextProcess = findViewById(R.id.next_process_btn);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        txtMsg = findViewById(R.id.del_message);

        nextProcess.setOnClickListener(v -> {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userNameRef = rootRef.child("Users").child("phone");
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()) {
                        txtTotalAmount.setText(String.valueOf(totalPrice));
                        Intent intent = new Intent(getApplicationContext(), CheckOut.class);
                        intent.putExtra("Amount",String.valueOf(totalPrice));
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"get it clear",Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };
            userNameRef.addListenerForSingleValueEvent(eventListener);

        });
    }





    @Override
    protected void onStart() {
        super.onStart();

        checkCompleteOrder();
        final DatabaseReference cartList = FirebaseDatabase.getInstance().getReference().child("Cart List");


        final FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartList.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone()).child("Products"),Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                        holder.txtproductName.setText(model.getPname());
                        holder.txtproductQuantity.setText(model.getQuantity());
                        holder.txtproductprice.setText(model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        int productPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                        totalPrice = totalPrice + productPrice;


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence charSequence [] = new CharSequence[]
                                        {
                                                "Edit",
                                                "Remove"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Options");

                                builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0 ){
                                            Intent intent = new Intent(getApplicationContext(), ProductsDetailActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }if(which == 1){
                                            cartList.child("User View")
                                                    .child(Prevalent.currentOnlineUser.getPhone())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toasty.info(getApplicationContext(),"Item removed",Toast.LENGTH_SHORT,true).show();
                                                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                            }
                                                        }
                                                    });
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item_layout, viewGroup, false);
                        CartViewHolder holder = new CartViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void checkCompleteOrder(){
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


//                if(dataSnapshot.exists()){
//                    String shippingState = dataSnapshot.child("state").getValue().toString();
//                    String userName = dataSnapshot.child("name").getValue().toString();
//
//                    if(shippingState.equals("shipped")){
//
//                        txtTotalAmount.setText("Order placed");
//                        recyclerView.setVisibility(View.GONE);
//
//                        txtMsg.setVisibility(View.VISIBLE);
//                        nextProcess.setVisibility(View.GONE);
//
//                        Toast.makeText(getApplicationContext(),"You can purchase more items",Toast.LENGTH_SHORT).show();
//
//
//                    }else if(shippingState.equals("not shipped")){
//
//                        txtTotalAmount.setText("Order not shipped");
//                        recyclerView.setVisibility(View.GONE);
//
//                        txtMsg.setVisibility(View.VISIBLE);
//                        nextProcess.setVisibility(View.GONE);
//
//                        Toast.makeText(getApplicationContext(),"You can purchase more items",Toast.LENGTH_SHORT).show();
//
//                    }
//                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

