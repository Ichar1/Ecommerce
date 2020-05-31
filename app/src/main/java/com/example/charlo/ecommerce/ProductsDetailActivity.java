package com.example.charlo.ecommerce;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.charlo.ecommerce.Activity.CartActivity;
import com.example.charlo.ecommerce.Model.Cart;
import com.example.charlo.ecommerce.Model.Products;
import com.example.charlo.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class ProductsDetailActivity extends AppCompatActivity {
    TextView productName, productDescription, productPrice;
    ImageView productImage;
    FloatingActionButton addToCartBtn;
    ImageView bt_toggle_text;
    ElegantNumberButton numberButton;
    NestedScrollView nestedScrollView;
    View lyt_expand_text;
    private String productID = "", state = "Normal", generatedFilePath;
    Context context;
    Button btnBuy;
    Uri ImageUri = null;
    private String productRandomKey, downloadImageUrl;
    StorageReference mStorageReference;
    StorageReference ProductImagesRef;
    DatabaseReference cartlistRef;
    String image_name;
    Cart cart;
    List<Cart> cartList;
    private Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_detail);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        context = getApplicationContext();
        cartList = new ArrayList<>();
        cart = new Cart();
        productID = getIntent().getStringExtra("pid");

        productName = findViewById(R.id.product_name_detail);
        productDescription = findViewById(R.id.product_description_detail);
        productPrice = findViewById(R.id.product_price_detail);
        productImage = findViewById(R.id.product_image_detail);
        addToCartBtn = findViewById(R.id.btn_cart);
        numberButton = findViewById(R.id.number_btn);
        bt_toggle_text = findViewById(R.id.bt_toggle_description);
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        lyt_expand_text = findViewById(R.id.lyt_expand_text);
        btnBuy = findViewById(R.id.btn_buy);


        bt_toggle_text.setOnClickListener(v -> toggleSectionText(bt_toggle_text));

        getProductDetails(productID);

        btnBuy.setOnClickListener(v -> {
            if (state.equals("Order Placed") || state.equals("Order Shipped")) {
                Toast.makeText(getApplicationContext(), "You has been placed and will shipped soon", Toast.LENGTH_LONG).show();
            } else {
                addToCart();
            }

        });

        addToCartBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), CartActivity.class));

        });
        getImage();
    }


    public static class Tools {


        public static void nestedScrollTo(final NestedScrollView nested, final View targetView) {
            nested.post(new Runnable() {
                @Override
                public void run() {
                    nested.scrollTo(500, targetView.getBottom());
                }
            });
        }

    }

    public static class ViewAnimation {

        public static void expand(final View v, final AnimListener animListener) {
            Animation a = expandAction(v);
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animListener.onFinish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            v.startAnimation(a);
        }

        private static Animation expandAction(final View v) {
            v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            final int targtetHeight = v.getMeasuredHeight();

            v.getLayoutParams().height = 0;
            v.setVisibility(View.VISIBLE);
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    v.getLayoutParams().height = interpolatedTime == 1
                            ? ViewGroup.LayoutParams.WRAP_CONTENT
                            : (int) (targtetHeight * interpolatedTime);
                    v.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
            v.startAnimation(a);
            return a;
        }

        public static void collapse(final View v) {
            final int initialHeight = v.getMeasuredHeight();

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if (interpolatedTime == 1) {
                        v.setVisibility(View.GONE);
                    } else {
                        v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                        v.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
            v.startAnimation(a);
        }

        public interface AnimListener {
            void onFinish();
        }


    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

    private void toggleSectionText(View view) {
        boolean show = toggleArrow(view);
        if (show) {
            ViewAnimation.expand(lyt_expand_text, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nestedScrollView, lyt_expand_text);
                }
            });
        } else {
            ViewAnimation.collapse(lyt_expand_text);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrder();
    }

    void getImage() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Products");
        query =  rootRef.orderByChild("pid").equalTo(productID);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    image_name = ds.child("image").getValue().toString();
                    cart.setImage(image_name);
                    cartList.add(cart);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);

    }

    private void addToCart() {
        String saveCurrentDate, saveCurrentTime;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());


        cartlistRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("image", cart.getImage());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("discount", "");

        cartlistRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productID).updateChildren(cartMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cartlistRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                .child("Products").child(productID).updateChildren(cartMap)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toasty.success(getApplicationContext(), "Added to cart", Toast.LENGTH_LONG,true).show();
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    }
                                });
                    }

                });
    }

    private void getProductDetails(String productID) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productDescription.setText(products.getDescription());
                    productPrice.setText(products.getPrice());
                    Picasso.get().load(products.getImage())
                            .fit()
                            .centerInside()
                            .into(productImage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void checkOrder() {

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                if(dataSnapshot.exists()){
//                    String shippingState = dataSnapshot.child("state").getValue().toString();
//
//                    if(shippingState.equals("shipped")){
//
//                        state = "Order Shipped";
//
//                    }else if(shippingState.equals("not shipped")){
//
//                        state = "Order Placed";
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
