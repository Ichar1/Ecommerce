package com.example.charlo.ecommerce;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.charlo.ecommerce.Interface.ItemClickListener;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtproductName, txtproductprice, txtproductQuantity;
    public ImageView imageView;
    ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        txtproductName = itemView.findViewById(R.id.cart_pname);
        txtproductprice = itemView.findViewById(R.id.cart_price);
        txtproductQuantity = itemView.findViewById(R.id.cart_quantity);
        imageView = itemView.findViewById(R.id.product_cart_image);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(),false);

    }
}
