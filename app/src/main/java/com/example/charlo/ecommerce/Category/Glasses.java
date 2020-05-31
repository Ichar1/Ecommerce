package com.example.charlo.ecommerce.Category;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.charlo.ecommerce.Activity.Admin.AdminMaintainProductsActivity;
import com.example.charlo.ecommerce.Model.Products;
import com.example.charlo.ecommerce.ProductsDetailActivity;
import com.example.charlo.ecommerce.R;
import com.example.charlo.ecommerce.SpacingItemDecoration;
import com.example.charlo.ecommerce.Tools;
import com.example.charlo.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class Glasses extends AppCompatActivity {

    private DatabaseReference ProductsRef;
    private Query query;
    RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManagr;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glasses);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle !=null){
            type = getIntent().getExtras().get("Admin").toString();
        }


        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        query =  ProductsRef.orderByChild("category").equalTo("Glasses");

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView.setNestedScrollingEnabled(false);

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(query,Products.class)
                .build();


        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new
                FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model) {

                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductPrice.setText("Ksh." + model.getPrice());
                        //holder.txtProductDescription.setText(model.getDescription());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(type.equals("Admin")){

                                    Intent intent = new Intent(getApplicationContext(), AdminMaintainProductsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);

                                }else{

                                    Intent intent = new Intent(getApplicationContext(), ProductsDetailActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);

                                }



                            }
                        });

                    }


                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_items_layout,viewGroup,false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}



