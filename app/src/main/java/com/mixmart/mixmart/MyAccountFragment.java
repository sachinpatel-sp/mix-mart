package com.mixmart.mixmart;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    public MyAccountFragment() {
        // Required empty public constructor
    }

    private Button viewAllAddressBtn;
    private TextView userName;
    private TextView userEmail;
    public static final int MANAGE_ADDRESS = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_mail);
        userName.setText(HomeActivity.userName);
        userEmail.setText(HomeActivity.userEmail);
        viewAllAddressBtn = view.findViewById(R.id.viewall_button);
        viewAllAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MyAddressesActivity.class);
                intent.putExtra("MODE",MANAGE_ADDRESS);
                startActivity(intent);

            }
        });

        return view;
    }
}
