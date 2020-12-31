package com.mixmart.mixmart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private EditText city;
    private EditText locality;
    private EditText flatNo;
    private EditText pinCode;
    private EditText landmark;
    private Spinner state;
    private EditText name;
    private EditText phone;
    private String selectedState;
    private String[] stateList;
    private Dialog loadingDialog;

    private Button saveBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add a new Address");

        loadingDialog = new Dialog(AddAddressActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(AddAddressActivity.this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        saveBtn = findViewById(R.id.save_btn);
        city = findViewById(R.id.city);
        locality = findViewById(R.id.locality);
        flatNo = findViewById(R.id.flat_no);
        pinCode = findViewById(R.id.pin_code);
        landmark = findViewById(R.id.landmark);
        state = findViewById(R.id.state_spinner);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.mobile_no);
        stateList = getResources().getStringArray(R.array.india_states);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, stateList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        state.setAdapter(spinnerAdapter);
        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState = stateList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(city.getText())) {
                    if (!TextUtils.isEmpty(locality.getText())) {
                        if (!TextUtils.isEmpty(flatNo.getText())) {
                            if (!TextUtils.isEmpty(pinCode.getText()) && pinCode.getText().length() == 6) {
                                if (!TextUtils.isEmpty(name.getText())) {
                                    if (!TextUtils.isEmpty(phone.getText()) && phone.getText().length() == 10) {
                                        Map<String, Object> addAddress = new HashMap();
                                        loadingDialog.show();
                                        final String fullAddress = flatNo.getText().toString()+","+locality.getText().toString()+","+landmark.getText().toString()+","+city.getText().toString()+","+selectedState;

                                        addAddress.put("list_size", (long) DBQueries.addressesModelArrayList.size() + 1);
                                        addAddress.put("fullname_" + String.valueOf((long) DBQueries.addressesModelArrayList.size() + 1), name.getText().toString() + "-" + phone.getText().toString());
                                        addAddress.put("address_" + String.valueOf((long) DBQueries.addressesModelArrayList.size() + 1), fullAddress);
                                        addAddress.put("pincode_" + String.valueOf((long) DBQueries.addressesModelArrayList.size() + 1), pinCode.getText().toString());
                                        addAddress.put("selected_" + String.valueOf((long) DBQueries.addressesModelArrayList.size() + 1), true);
                                        if (DBQueries.addressesModelArrayList.size() > 0) {
                                            addAddress.put("selected_" + (DBQueries.selectedAddress + 1), false);
                                        }

                                        FirebaseFirestore.getInstance().collection("users")
                                                .document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                                                .document("MY_ADDRESSES")
                                                .update(addAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    if (DBQueries.addressesModelArrayList.size() > 0) {
                                                        DBQueries.addressesModelArrayList.get(DBQueries.selectedAddress).setSelected(false);
                                                    }
                                                    if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                                        DBQueries.addressesModelArrayList.add(new AddressesModel(name.getText().toString() + "-" + phone.getText().toString(), fullAddress, pinCode.getText().toString(), true));
                                                        Intent deliveryIntent = new Intent(AddAddressActivity.this, DeliveryActivity.class);
                                                        startActivity(deliveryIntent);
                                                    }else{
                                                        MyAddressesActivity.refreshItem(DBQueries.selectedAddress,DBQueries.addressesModelArrayList.size()-1);
                                                    }
                                                    DBQueries.selectedAddress = DBQueries.addressesModelArrayList.size() - 1;
                                                    finish();
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(AddAddressActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                                loadingDialog.dismiss();
                                            }
                                        });
                                    } else {
                                        phone.requestFocus();
                                        Toast.makeText(AddAddressActivity.this, "Please Provide valid number", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    name.requestFocus();
                                }
                            } else {
                                pinCode.requestFocus();
                                Toast.makeText(AddAddressActivity.this, "Please provide valid pincode", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            flatNo.requestFocus();
                        }
                    } else {
                        locality.requestFocus();
                    }
                } else {
                    city.requestFocus();
                }

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       int id = item.getItemId();
       if(id == android.R.id.home){
           finish();
           return true;
       }
        return super.onOptionsItemSelected(item);
    }
}
