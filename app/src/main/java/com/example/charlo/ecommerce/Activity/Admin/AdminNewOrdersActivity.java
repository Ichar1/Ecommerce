package com.example.charlo.ecommerce.Activity.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.charlo.ecommerce.Model.AdminOrders;
import com.example.charlo.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {
    DatabaseReference ordersRef;
    RecyclerView ordersList;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);
        setTitle("New Orders");


        ordersList = findViewById(R.id.order_list);
        ordersList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ordersList.setLayoutManager(layoutManager);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                        .setQuery(ordersRef,AdminOrders.class)
                        .build();
        FirebaseRecyclerAdapter<AdminOrders, AdminViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminViewHolder holder, final int position, @NonNull final AdminOrders model) {

                        holder.orderName.setText("Name:" + model.getName());
                        holder.orderAddress.setText("address:" + model.getAddress());
                        holder.orderPhone.setText("phone:" + model.getPhone());
                        holder.orderDateTime.setText("Order at: " + model.getDate() + " " + model.getTime());
                        holder.orderPrice.setText("Ksh." + model.getAmount());

                        holder.btnproducts.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                                String uID = getRef(position).getKey();

                                Intent intent = new Intent(getApplicationContext(), AdminUserOrderItemsActivity.class);
                                intent.putExtra("uid", uID);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[]= new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Have you Products been shipped");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which == 0){
                                            String uID = getRef(position).getKey();
                                            RemoveOrder(uID);
                                        }
                                        else{
                                            finish();
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_layout,viewGroup,false);

                        return new AdminViewHolder(view);
                    }
                };
        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    private void RemoveOrder(String uID) {
        ordersRef.child(uID).removeValue();
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder{

        public TextView orderName, orderPhone, orderAddress, orderPrice, orderDateTime, OrderShipping;
        Button btnproducts;


        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            orderName = itemView.findViewById(R.id.order_username);
            orderPhone = itemView.findViewById(R.id.order_phone);
            orderAddress = itemView.findViewById(R.id.order_address);
            orderPrice = itemView.findViewById(R.id.order_price);
            orderDateTime = itemView.findViewById(R.id.order_datetime);
            btnproducts = itemView.findViewById(R.id.btn_items);

        }
    }

}
