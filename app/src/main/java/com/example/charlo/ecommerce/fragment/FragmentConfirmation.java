package com.example.charlo.ecommerce.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.charlo.ecommerce.Activity.CartActivity;
import com.example.charlo.ecommerce.CartViewHolder;
import com.example.charlo.ecommerce.Model.Cart;
import com.example.charlo.ecommerce.Model.Checkout;
import com.example.charlo.ecommerce.Prevalent.Prevalent;
import com.example.charlo.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentConfirmation extends Fragment {
    private TextView amount, name, address, edit, edit2, phone;
    RecyclerView recyclerView;
    DatabaseReference cartListRef;
    View root_view;



    public FragmentConfirmation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root_view = inflater.inflate(R.layout.fragment_fragment_confirmation, container, false);
        name = root_view.findViewById(R.id.cName);
        address = root_view.findViewById(R.id.CAddress);
        amount = root_view.findViewById(R.id.camount);
        phone = root_view.findViewById(R.id.cphone);
        recyclerView = (RecyclerView) root_view.findViewById(R.id.products_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        final DatabaseReference OrdersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        OrdersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Checkout checkout = dataSnapshot.getValue(Checkout.class);

                    name.setText(checkout.getName());
                    address.setText(checkout.getAddress());
                    amount.setText(checkout.getAmount());
                    phone.setText(checkout.getPhone());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return root_view;

    }

    @Override
    public void onStart() {
        super.onStart();

        cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("Admin View")
                .child(Prevalent.currentOnlineUser.getPhone())
                .child("Products");

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef, Cart.class)
                        .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                        holder.txtproductName.setText(model.getPname());
                        holder.txtproductQuantity.setText(model.getQuantity());
                        holder.txtproductprice.setText("Ksh." + model.getPrice());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

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
}
