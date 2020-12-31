package com.mixmart.mixmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryActivity extends AppCompatActivity {

    private ConstraintLayout orderConfirmationLayout;
    private Button continueShoppingButton;
    private TextView orderID;
    private RecyclerView deliveryRecyclerView;
    private Button changeOrAddNewAddressBtn;
    public static final int SELECT_ADDRESS=0;
    private TextView totalAmount;
    private TextView fullName;
    private TextView fullAddress;
    private TextView pinCode;
    private Button continueButton;
    private boolean successResponse = false;
    public static boolean fromCart;

    public static List<CartItemModel> cartItemModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");

        changeOrAddNewAddressBtn = findViewById(R.id.change_add_address_btn);
        deliveryRecyclerView = findViewById(R.id.delivery_recycler_view);
        totalAmount=findViewById(R.id.total_cart_amount);
        fullName=findViewById(R.id.full_name);
        fullAddress=findViewById(R.id.address);
        pinCode=findViewById(R.id.pincode);
        continueButton = findViewById(R.id.cart_continue_btn);
        orderID = findViewById(R.id.order_id);
        continueShoppingButton = findViewById(R.id.continue_shopping);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(linearLayoutManager);

        final CartAdapter cartAdapter = new CartAdapter(cartItemModelList,totalAmount,false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();
        orderConfirmationLayout.setVisibility(View.GONE);
        changeOrAddNewAddressBtn.setVisibility(View.VISIBLE);
        changeOrAddNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeliveryActivity.this,MyAddressesActivity.class);
                intent.putExtra("MODE",SELECT_ADDRESS);
                startActivity(intent);
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                successResponse = true;
                if(HomeActivity.mainActivity != null){
                    HomeActivity.mainActivity.finish();
                    HomeActivity.mainActivity = null;
                    HomeActivity.showCart = false;
                }

                if(ProductDetailsActivity.productDetailsActivity != null){
                    ProductDetailsActivity.productDetailsActivity.finish();
                    ProductDetailsActivity.productDetailsActivity = null;
                }

                if(fromCart){
                    Map<String, Object> updateCartlist = new HashMap<>();
                    long cartListSize = 0;
                    final List<Integer> indexList = new ArrayList<>();
                    for (int x = 0; x < DBQueries.cartList.size(); x++) {
                        if(!cartItemModelList.get(x).isInStock()){
                            cartListSize++;
                            updateCartlist.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                        }else{
                            indexList.add(x);
                        }
                    }
                    updateCartlist.put("list_size", cartListSize);
                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                            .set(updateCartlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                for(int x = 0;x < indexList.size();x++){
                                    DBQueries.cartList.remove(indexList.get(x).intValue());
                                    DBQueries.cartItemModelList.remove(indexList.get(x).intValue());
                                    DBQueries.cartItemModelList.remove(DBQueries.cartItemModelList.size()-1);
                                }
                            }else{
                                String error = task.getException().getMessage();
                                Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                orderID.setText("Order ID "+"656aygdhagd76d8bshdbhsd76");
                orderConfirmationLayout.setVisibility(View.VISIBLE);
                continueShoppingButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        fullName.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getFullName());
        fullAddress.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getAddress());
        pinCode.setText(DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).getPinCode());

        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if( id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(successResponse){
            finish();
            return;
        }
        super.onBackPressed();
    }
}
