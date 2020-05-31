package com.example.charlo.ecommerce.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.charlo.ecommerce.CartViewHolder;
import com.example.charlo.ecommerce.Model.Cart;
import com.example.charlo.ecommerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderItemsFragment extends Fragment {
    View root_view;
    RecyclerView recyclerView;
    DatabaseReference cartListRef;
    RecyclerView.LayoutManager layoutManager;

    private String userID = "";


    public OrderItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef, Cart.class)
                        .build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                        holder.txtproductName.setText(model.getPname());
                        //holder.txtproductQuantity.setText(model.getQuantity());
                        holder.txtproductprice.setText("Ksh." +model.getPrice());

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        root_view = inflater.inflate(R.layout.fragment_payment_dialog_success, container, false);
        userID = getActivity().getIntent().getStringExtra("uid");

        recyclerView = root_view.findViewById(R.id.product_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        cartListRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("Admin View")
                .child(userID)
                .child("Products");




        return root_view;
    }

}
