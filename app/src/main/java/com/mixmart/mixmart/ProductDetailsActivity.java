package com.mixmart.mixmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mixmart.mixmart.HomeActivity.showCart;

public class ProductDetailsActivity extends AppCompatActivity {

    public static Activity productDetailsActivity;
    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;

    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;

    public static FloatingActionButton addToWishlistbutton;
    private ViewPager productImagesViewPager;
    private TabLayout viewPagerIndicator;

    private TextView productTitle;
    private TextView averageRatingMiniView;
    private TextView totalRatingMiniView;
    private TextView productPrice;
    private TextView cuttedPrice;
    private TextView averageRating;
    private FirebaseFirestore firebaseFirestore;
    ///////rating layout
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingsNumberContainer;
    private LinearLayout progressContainer;
    public static String productID;
    private Dialog loadingDialog;
    public static int initialRating;
    private TextView badgeCount;
    //////
    private DocumentSnapshot documentSnapshot;
    private Button buyNowBtn;
    private LinearLayout addToCartBtn;
    public static MenuItem cartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         productImagesViewPager = findViewById(R.id.product_images_viewpager);
         viewPagerIndicator = findViewById(R.id.viewpager_indicator);
         addToWishlistbutton = findViewById(R.id.add_to_wishlist);
         buyNowBtn = findViewById(R.id.buy_now_btn);
         addToCartBtn = findViewById(R.id.add_to_cart_btn);
         productPrice = findViewById(R.id.product_price);
         cuttedPrice = findViewById(R.id.cutted_price);
         averageRatingMiniView = findViewById(R.id.product_rating_tv_mini_view);
         totalRatingMiniView = findViewById(R.id.total_ratings_mini_view);
         productTitle = findViewById(R.id.product_title);
         ratingsNumberContainer = findViewById(R.id.ratings_count_container);

         progressContainer = findViewById(R.id.ratings_progessbar_container);
         totalRatings =findViewById(R.id.total_ratings);
         averageRating = findViewById(R.id.tv_avg_rating);

         initialRating = -1;

         loadingDialog = new Dialog(ProductDetailsActivity.this);
         loadingDialog.setContentView(R.layout.loading_progress);
         loadingDialog.setCancelable(false);
         loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
         loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
         loadingDialog.show();

         firebaseFirestore = FirebaseFirestore.getInstance();
         final List<String> productImages = new ArrayList<>();
         productID = getIntent().getStringExtra("PRODUCT_ID");
         firebaseFirestore.collection("products").document(productID)
                 .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 if(task.isSuccessful()){

                      documentSnapshot = task.getResult();
                     for( long x=1;x<(long)documentSnapshot.get("no_of_product_images")+1;x++){
                         productImages.add(documentSnapshot.get("product_image_"+x).toString());
                     }
                     ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                     productImagesViewPager.setAdapter(productImagesAdapter);

                     averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
                     productTitle.setText(documentSnapshot.get("product_title").toString());
                     totalRatingMiniView.setText("("+(long)documentSnapshot.get("total_ratings")+" ratings)");
                     productPrice.setText("₹"+documentSnapshot.get("product_price").toString()+"/-");
                     cuttedPrice.setText("₹"+documentSnapshot.get("cutted_price").toString()+"/-");
                     averageRating.setText(documentSnapshot.get("average_rating").toString());
                     totalRatings.setText("("+(long)documentSnapshot.get("total_ratings")+" ratings)");

                     for(int x = 0;x <5;x++){
                         TextView rating = (TextView) ratingsNumberContainer.getChildAt(x);
                         rating.setText(String.valueOf((long)documentSnapshot.get((5-x)+"_star")));
                         ProgressBar progressBar = (ProgressBar)progressContainer.getChildAt(x);
                         int maxProgress = Integer.parseInt(String.valueOf((long)documentSnapshot.get("total_ratings")));
                         progressBar.setMax(maxProgress);
                         progressBar.setProgress(Integer.parseInt(String.valueOf((long)documentSnapshot.get((5-x)+"_star"))));
                     }

                         if(DBQueries.myRating.size()==0){
                             DBQueries.LoadRatingList(ProductDetailsActivity.this);
                         }
                         if (DBQueries.wishlist.size() == 0) {
                             DBQueries.loadWishlist(ProductDetailsActivity.this,false);
                             loadingDialog.dismiss();
                         } else {
                             loadingDialog.dismiss();
                         }

                     if(DBQueries.myRatedIds.contains(productID)){
                         int index = DBQueries.myRatedIds.indexOf(productID);
                         initialRating = Integer.parseInt(String.valueOf(DBQueries.myRating.get(index)))-1;
                         setRating(initialRating);
                     }

                     if(DBQueries.cartList.contains(productID)){
                         ALREADY_ADDED_TO_CART = true;
                     }else{
                         ALREADY_ADDED_TO_CART = false;
                     }

                     if(DBQueries.wishlist.contains(productID)){
                         ALREADY_ADDED_TO_WISHLIST = true;
                         addToWishlistbutton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#2AC17E")));
                     }else{
                         ALREADY_ADDED_TO_WISHLIST = false;
                         addToWishlistbutton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#E4E4E4")));
                     }

                     if((boolean)documentSnapshot.get("in_stock")){

                         addToCartBtn.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 if (!running_cart_query) {
                                     running_cart_query = true;
                                     if (ALREADY_ADDED_TO_CART) {
                                         running_cart_query = false;
                                         Toast.makeText(ProductDetailsActivity.this,"Already Added To Cart",Toast.LENGTH_LONG).show();
                                     } else {
                                         Map<String, Object> addProduct = new HashMap<>();
                                         addProduct.put("product_ID_" + DBQueries.cartList.size(), productID);
                                         addProduct.put("list_size", (long) DBQueries.cartList.size() + 1);

                                         firebaseFirestore.collection("users").document(HomeActivity.firebaseAuth.getUid()).collection("USER_DATA").document("MY_CART")
                                                 .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful()) {
                                                     if (DBQueries.cartItemModelList.size() != 0) {
                                                         DBQueries.cartItemModelList.add(0,new CartItemModel(CartItemModel.CART_ITEM, productID
                                                                 , documentSnapshot.get("product_title").toString()
                                                                 , documentSnapshot.get("product_price").toString()
                                                                 , documentSnapshot.get("cutted_price").toString()
                                                                 , documentSnapshot.get("product_image_1").toString()
                                                                 , (long) documentSnapshot.get("free_coupons")
                                                                 , (long) 1
                                                                 , (long) 0
                                                                 , (long) 0
                                                                 , (boolean)documentSnapshot.get("in_stock")
                                                                 , (Long)documentSnapshot.get("max_quantity")));
                                                     }

                                                     String suc = "Added To Cart";
                                                     ALREADY_ADDED_TO_CART = true;
                                                     DBQueries.cartList.add(productID);
                                                     Toast.makeText(ProductDetailsActivity.this, suc, Toast.LENGTH_SHORT).show();
                                                     //addToWishlistbutton.setEnabled(true);
                                                     invalidateOptionsMenu();
                                                     running_cart_query = false;

                                                 } else {
                                                     //addToWishlistbutton.setEnabled(true);
                                                     running_cart_query = false;
                                                     String error = task.getException().getMessage();
                                                     Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                 }
                                             }
                                         });
                                     }
                                 }
                             }
                         });
                     }else{
                         buyNowBtn.setVisibility(View.GONE);
                         TextView outOfStock = (TextView) addToCartBtn.getChildAt(0);
                         outOfStock.setText("Out Of Stock");
                         outOfStock.setTextColor(getResources().getColor(R.color.colorPrimary));

                     }
                 }else{
                     loadingDialog.dismiss();
                     String error = task.getException().getMessage();
                     Toast.makeText(ProductDetailsActivity.this,error,Toast.LENGTH_SHORT).show();
                 }
             }
         });

        viewPagerIndicator.setupWithViewPager(productImagesViewPager,true);

        addToWishlistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addToWishlistbutton.setEnabled(false);
                if (!running_wishlist_query) {
                    running_wishlist_query = true;
                    if (ALREADY_ADDED_TO_WISHLIST) {
                        int index = DBQueries.wishlist.indexOf(productID);
                        DBQueries.removeFromWishlist(index, ProductDetailsActivity.this);
                        addToWishlistbutton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#E4E4E4")));
                        addToWishlistbutton.setEnabled(true);
                    } else {
                        addToWishlistbutton.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
                        Map<String, Object> addProduct = new HashMap<>();
                        addProduct.put("product_ID_" + String.valueOf(DBQueries.wishlist.size()), productID);
                        addProduct.put("list_size", (long) DBQueries.wishlist.size() + 1);

                        firebaseFirestore.collection("users").document(HomeActivity.firebaseAuth.getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (DBQueries.wishlistModelList.size() != 0) {
                                        DBQueries.wishlistModelList.add(new WishlistModel(productID, documentSnapshot.get("product_image_1").toString()
                                                , documentSnapshot.get("product_full_title").toString()
                                                , (long) documentSnapshot.get("free_coupons")
                                                , documentSnapshot.get("average_rating").toString()
                                                , (long) documentSnapshot.get("total_ratings")
                                                , documentSnapshot.get("product_price").toString()
                                                , documentSnapshot.get("cutted_price").toString()
                                        ,(boolean)documentSnapshot.get("in_stock")));
                                    }

                                    String suc = "Added To Wishlist";
                                    ALREADY_ADDED_TO_WISHLIST = true;
                                    addToWishlistbutton.setSupportImageTintList(getResources().getColorStateList(R.color.colorPrimary));
                                    DBQueries.wishlist.add(productID);
                                    Toast.makeText(ProductDetailsActivity.this, suc, Toast.LENGTH_SHORT).show();
                                    
                                }
                                 else {
                                    //addToWishlistbutton.setEnabled(true);
                                    addToWishlistbutton.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                    String error = task.getException().getMessage();
                                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                                running_wishlist_query = false;
                            }
                        });
                    }
                }
            }
        });

         /////ratings layout

        rateNowContainer = findViewById(R.id.rate_now_container);
        for(int x = 0; x < rateNowContainer.getChildCount(); x++){
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (starPosition != initialRating) {
                        if (!running_rating_query) {
                            running_rating_query = true;
                            setRating(starPosition);
                            Map<String, Object> updateRating = new HashMap<>();
                            if (DBQueries.myRatedIds.contains(productID)) {
                                TextView oldRating = (TextView) ratingsNumberContainer.getChildAt(5 - initialRating - 1);
                                TextView finalRating = (TextView) ratingsNumberContainer.getChildAt(5 - starPosition - 1);
                                updateRating.put((initialRating + 1) + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                updateRating.put((starPosition + 1) + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                updateRating.put("average_rating", String.valueOf(calculateAverageRating((long) starPosition - initialRating, true)));
                            } else {
                                updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                updateRating.put("average_rating", String.valueOf(calculateAverageRating(starPosition + 1, false)));
                                updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
                            }
                            firebaseFirestore.collection("products").document(productID)
                                    .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Map<String, Object> myRating = new HashMap<>();

                                        if (DBQueries.myRatedIds.contains(productID)) {
                                            myRating.put("rating_" + DBQueries.myRatedIds.indexOf(productID), (long) starPosition + 1);
                                        } else {
                                            myRating.put("list_size", (long) DBQueries.myRatedIds.size() + 1);
                                            myRating.put("product_ID_" + (long) DBQueries.myRatedIds.size(), productID);
                                            myRating.put("rating_" + (long) DBQueries.myRatedIds.size(), starPosition + 1);
                                        }

                                        firebaseFirestore.collection("users").document(HomeActivity.firebaseAuth.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (DBQueries.myRatedIds.contains(productID)) {
                                                        DBQueries.myRating.set(DBQueries.myRatedIds.indexOf(productID), (long) starPosition + 1);
                                                        TextView oldRating = (TextView) ratingsNumberContainer.getChildAt(5 - initialRating - 1);
                                                        TextView finalRating = (TextView) ratingsNumberContainer.getChildAt(5 - starPosition - 1);
                                                        oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                        finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));
                                                    } else {

                                                        DBQueries.myRatedIds.add(productID);
                                                        DBQueries.myRating.add((long) starPosition + 1);

                                                        TextView rating = (TextView) ratingsNumberContainer.getChildAt(5 - starPosition - 1);
                                                        rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                        totalRatingMiniView.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + " ratings)");
                                                        totalRatings.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + " ratings)");

                                                        averageRating.setText(calculateAverageRating(starPosition + 1, false));
                                                        averageRatingMiniView.setText(calculateAverageRating(starPosition + 1, false));

                                                        Toast.makeText(ProductDetailsActivity.this, "Thank you! for rating", Toast.LENGTH_SHORT).show();
                                                    }
                                                    for (int x = 0; x < 5; x++) {
                                                        TextView ratingFigures = (TextView) ratingsNumberContainer.getChildAt(x);
                                                        ProgressBar progressBar = (ProgressBar) progressContainer.getChildAt(x);
                                                        if (DBQueries.myRatedIds.contains(productID)) {
                                                            int maxProgress = Integer.parseInt(totalRatings.getText().toString());
                                                            progressBar.setMax(maxProgress);
                                                        }
                                                        progressBar.setProgress(Integer.parseInt(ratingFigures.getText().toString()));

                                                    }
                                                    initialRating = starPosition;
                                                    averageRating.setText(String.valueOf(calculateAverageRating(0, true)));
                                                    averageRatingMiniView.setText(String.valueOf(calculateAverageRating(0, false)));
                                                    if (DBQueries.wishlist.contains(productID) && DBQueries.wishlistModelList.size() != 0) {
                                                        int index = DBQueries.wishlist.indexOf(productID);
                                                        DBQueries.wishlistModelList.get(index).setRating(averageRating.getText().toString());
                                                        DBQueries.wishlistModelList.get(index).setTotalRatings(Long.parseLong(totalRatings.getText().toString()));
                                                    }

                                                } else {
                                                    setRating(initialRating);
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                                running_rating_query = false;
                                            }
                                        });
                                    } else {
                                        running_rating_query = false;
                                        setRating(initialRating);
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    }
                }
            });
        }

        ////rating layout
        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryActivity.fromCart = false;
                loadingDialog.show();
                productDetailsActivity = ProductDetailsActivity.this;
                DeliveryActivity.cartItemModelList = new ArrayList<>();
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.CART_ITEM, productID
                        , documentSnapshot.get("product_title").toString()
                        , documentSnapshot.get("product_price").toString()
                        , documentSnapshot.get("cutted_price").toString()
                        , documentSnapshot.get("product_image_1").toString()
                        , (long) documentSnapshot.get("free_coupons")
                        , (long) 1
                        , (long) 0
                        , (long) 0
                        , (boolean)documentSnapshot.get("in_stock")
                        , (Long)documentSnapshot.get("max_quantity")));
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                if(DBQueries.addressesModelArrayList.size() == 0){
                    DBQueries.loadAddresses(ProductDetailsActivity.this,loadingDialog);
                }else{
                    loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(ProductDetailsActivity.this,DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
    }

    public static void setRating(int starPosition){

            for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
                ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
                if (x <= starPosition) {
                    starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
                }
            }

    }
    private String  calculateAverageRating(long currentUserRating,boolean update) {
        Double totalstars = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingsNumberContainer.getChildAt(5 - x);
            totalstars = totalstars + (Long.parseLong(ratingNo.getText().toString())) * x;
        }
        totalstars = totalstars + currentUserRating;
        if (update) {
            return String.valueOf(totalstars / Long.parseLong(totalRatings.getText().toString())).substring(0,2);

        } else {
            return String.valueOf(totalstars / Long.parseLong(totalRatings.getText().toString())).substring(0,2);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.search_cart_icon, menu);

         cartItem = menu.findItem(R.id.my_cart_appbar);

            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.ic_cart);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);
        if (DBQueries.cartList.size() == 0) {
            DBQueries.loadCartList(ProductDetailsActivity.this, loadingDialog,false,badgeCount,new TextView(ProductDetailsActivity.this));
        }else{
            badgeCount.setVisibility(View.VISIBLE);
            if(DBQueries.cartList.size() < 99 ) {
                badgeCount.setText(String.valueOf(DBQueries.cartList.size()));
            }else{
                badgeCount.setText("99+");
            }
        }
            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cartIntent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                    showCart = true;
                    startActivity(cartIntent);
                }
            });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        }else if( id == R.id.search_appbar){
            return true;
        }else if (id == R.id.my_cart_appbar){
            Intent cartIntent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
            showCart = true;
            startActivity(cartIntent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = this;
        super.onBackPressed();
    }
}
