package com.example.charlo.ecommerce;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.charlo.ecommerce.Activity.Admin.AdminMaintainProductsActivity;
import com.example.charlo.ecommerce.Activity.Admin.SettingsActivity;
import com.example.charlo.ecommerce.Activity.CartActivity;
import com.example.charlo.ecommerce.Activity.OrderReceived_Activity;
import com.example.charlo.ecommerce.Activity.OrderedItemsActivity;
import com.example.charlo.ecommerce.Aunthentication.MainActivity;
import com.example.charlo.ecommerce.Category.BabyActivity;
import com.example.charlo.ecommerce.Category.Beauty;
import com.example.charlo.ecommerce.Category.BooksActivity;
import com.example.charlo.ecommerce.Category.Game;
import com.example.charlo.ecommerce.Category.Glasses;
import com.example.charlo.ecommerce.Category.Hat;
import com.example.charlo.ecommerce.Category.LaptopActivity;
import com.example.charlo.ecommerce.Category.MenFashionActivity;
import com.example.charlo.ecommerce.Category.PurseActivity;
import com.example.charlo.ecommerce.Category.SuitsActivity;
import com.example.charlo.ecommerce.Category.Watch;
import com.example.charlo.ecommerce.Category.WomenClothes;
import com.example.charlo.ecommerce.Category.WomenFashionActivity;
import com.example.charlo.ecommerce.Model.Products;
import com.example.charlo.ecommerce.Prevalent.Prevalent;
import com.example.charlo.ecommerce.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DatabaseReference ProductsRef;
    private Query query;
    private RecyclerView recyclerView, recyclerView1;
    private RecyclerView.LayoutManager layoutManagr;
    ViewFlipper img_Banner;
    SearchView searchView;


    private String type = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("IMALL");




        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle !=null){
            type = getIntent().getExtras().get("Admin").toString();
        }


        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        query =  ProductsRef.orderByChild("category").limitToLast(8);





        Paper.init(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!type.equals("Admin")){
                    startActivity(new Intent(getApplicationContext(), CartActivity.class));
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.getBackground().setColorFilter( 0x73000000, PorterDuff.Mode.MULTIPLY);
        headerView.getBackground().setColorFilter( 0xCC23283a, PorterDuff.Mode.MULTIPLY);
        navigationView.setNavigationItemSelectedListener(this);

        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);

        if(!type.equals("Admin")){

            userNameTextView.setText(Prevalent.currentOnlineUser.getName());
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile ).into(profileImageView);
        }


        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView.setNestedScrollingEnabled(false);





        img_Banner = findViewById(R.id.imgBanner);


        int sliders [] = {
                R.drawable.tv1,
                R.drawable.itemsflip,
                R.drawable.shoes4,



        };
        for(int slide:sliders){
            bannerFlipper(slide);
        }

        findViewById(R.id.laptop).setOnClickListener(this);
        findViewById(R.id.phone).setOnClickListener(this);
        findViewById(R.id.purse).setOnClickListener(this);
        findViewById(R.id.shoes).setOnClickListener(this);
        findViewById(R.id.baby).setOnClickListener(this);
        findViewById(R.id.game).setOnClickListener(this);
        findViewById(R.id.tv).setOnClickListener(this);
        findViewById(R.id.phone).setOnClickListener(this);
        findViewById(R.id.beauty).setOnClickListener(this);
        findViewById(R.id.tshirts).setOnClickListener(this);
        findViewById(R.id.dress).setOnClickListener(this);
        findViewById(R.id.suits).setOnClickListener(this);
        findViewById(R.id.hat).setOnClickListener(this);
        findViewById(R.id.glass).setOnClickListener(this);
        findViewById(R.id.headphone).setOnClickListener(this);
        findViewById(R.id.watch).setOnClickListener(this);



    }

    public void bannerFlipper(int image){

        ImageView imageView =  new ImageView(this);
        imageView.setImageResource(image);
        img_Banner.addView(imageView);
        img_Banner.setFlipInterval(6000);
        img_Banner.setAutoStart(true);
        img_Banner.setInAnimation(this, android.R.anim.fade_in);
        img_Banner.setOutAnimation(this,android.R.anim.fade_out);

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

                        holder.imageView.setOnClickListener(v -> {

                            if(type.equals("Admin")){

                                Intent intent = new Intent(getApplicationContext(), AdminMaintainProductsActivity.class);
                                intent.putExtra("pid",model.getPid());
                                startActivity(intent);

                            }else{

                                Intent intent = new Intent(getApplicationContext(),ProductsDetailActivity.class);
                                intent.putExtra("pid",model.getPid());
                                startActivity(intent);

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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_cart) {

            if(!type.equals("Admin")){
                startActivity(new Intent(getApplicationContext(), OrderedItemsActivity.class));
            }

        } else if (id == R.id.nav_search) {

            if(!type.equals("Admin")){

                startActivity(new Intent(getApplicationContext(),SearchItemsActivity.class));
            }


        }else if (id == R.id.nav_confirm) {
            if(!type.equals("Admin")){

                startActivity(new Intent(getApplicationContext(), OrderReceived_Activity.class));
        }


        } else if (id == R.id.nav_settings) {

            if (!type.equals("Admin")) {

                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            }
        }
        else if( id == R.id.nav_about){

                startActivity(new Intent(getApplicationContext(),ContactUs_Activity.class));

            }
        else if (id == R.id.nav_logout) {

            if(!type.equals("Admin")){

                Paper.book().destroy();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.phone:
                startActivity(new Intent(getApplicationContext(),MobileActivity.class));
                break;
            case R.id.baby:
                startActivity(new Intent(getApplicationContext(), BabyActivity.class));
                break;
            case R.id.glass:
                startActivity(new Intent(getApplicationContext(), Glasses.class));
                break;
            case R.id.game:
                startActivity(new Intent(getApplicationContext(), Game.class));
                break;
            case R.id.purse:
                startActivity(new Intent(getApplicationContext(), PurseActivity.class));
                break;
            case R.id.suits:
                startActivity(new Intent(getApplicationContext(), SuitsActivity.class));
                break;
            case R.id.shoes:
                startActivity(new Intent(getApplicationContext(), WomenFashionActivity.class));
                break;
            case R.id.tv:
                startActivity(new Intent(getApplicationContext(),TelevisionActivity.class));
                break;
            case R.id.laptop:
                startActivity(new Intent(getApplicationContext(), LaptopActivity.class));
                break;
            case R.id.watch:
                startActivity(new Intent(getApplicationContext(), Watch.class));
                break;
            case R.id.tshirts:
                startActivity(new Intent(getApplicationContext(), MenFashionActivity.class));
                break;
            case R.id.hat:
                startActivity(new Intent(getApplicationContext(), Hat.class));
                break;
            case R.id.headphone:
                startActivity(new Intent(getApplicationContext(), BooksActivity.class));
                break;
            case R.id.beauty:
                startActivity(new Intent(getApplicationContext(), Beauty.class));
                break;
            case R.id.dress:
                startActivity(new Intent(getApplicationContext(), WomenClothes.class));
                break;




        }

    }
}
