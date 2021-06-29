package com.mixmart.mixmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

import static com.mixmart.mixmart.DeliveryActivity.SELECT_ADDRESS;

public class MyAddressesActivity extends AppCompatActivity {

    private int previousAddress;
    private LinearLayout addNewAddress;
    private TextView addressSaved;
    private RecyclerView myAddressesRecyclerView;
    private Button deliverHereBtn;
    private static AddressesAdapter addressesAdapter;
    private Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);
        myAddressesRecyclerView = findViewById(R.id.addresses_recycler_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Address");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);



        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        previousAddress=DBQueries.selectedAddress;
        myAddressesRecyclerView.setLayoutManager(linearLayoutManager);

        deliverHereBtn = findViewById(R.id.deliver_here_btn);
        addNewAddress = findViewById(R.id.add_new_address_btn);
        addressSaved =findViewById(R.id.address_saved);


        int MODE = getIntent().getIntExtra("MODE",-1);
        if(MODE == SELECT_ADDRESS){
            deliverHereBtn.setVisibility(View.VISIBLE);
        }else{
            deliverHereBtn.setVisibility(View.GONE);
        }

        deliverHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DBQueries.selectedAddress != previousAddress){
                    final int previousAddressIndex = previousAddress;
                    loadingDialog.show();
                    Map<String,Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_"+String.valueOf(previousAddress+1),false);
                    updateSelection.put("selected_"+String.valueOf(DBQueries.selectedAddress+1),true);
                    previousAddress = DBQueries.selectedAddress;
                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid())
                            .collection("USER_DATA").document("MY_ADDRESSES")
                            .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                finish();
                            }else{
                                previousAddress = previousAddressIndex;
                                String error = task.getException().getMessage();
                                Toast.makeText(MyAddressesActivity.this,error,Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                }else{
                    finish();
                }
            }
        });
         addressesAdapter = new AddressesAdapter(DBQueries.addressesModelArrayList,MODE);
        myAddressesRecyclerView.setAdapter(addressesAdapter);

        ((SimpleItemAnimator)myAddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesAdapter.notifyDataSetChanged();
        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addressIntent = new Intent(MyAddressesActivity.this,AddAddressActivity.class);
                addressIntent.putExtra("INTENT","null");
                startActivity(addressIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        addressSaved.setText(String.valueOf(DBQueries.addressesModelArrayList.size()+" saved addresses"));
        super.onStart();
    }

    public static void refreshItem(int position, int currentPosition){
         addressesAdapter.notifyItemChanged(position);
        addressesAdapter.notifyItemChanged(currentPosition);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(DBQueries.selectedAddress != previousAddress){
                DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).setSelected(false);
                DBQueries.addressesModelArrayList.get(previousAddress).setSelected(true);
                DBQueries.selectedAddress = previousAddress;
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(DBQueries.selectedAddress != previousAddress){
            DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).setSelected(false);
            DBQueries.addressesModelArrayList.get(previousAddress).setSelected(true);
            DBQueries.selectedAddress = previousAddress;
        }
        super.onBackPressed();
    }
}
