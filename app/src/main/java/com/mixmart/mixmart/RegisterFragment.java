package com.mixmart.mixmart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;
    private FirebaseFirestore firebaseFirestore;
   private TextView backLg;
   private FrameLayout parentFrameLayout;
   private EditText fullName,email,password,cnfmPassword;
   private Button register;
   private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
   private FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        backLg = view.findViewById(R.id.backLg);
        parentFrameLayout = getActivity().findViewById(R.id.register_frame_layout);

        firebaseFirestore = FirebaseFirestore.getInstance();

        fullName = view.findViewById(R.id.fullName);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.pass);
        cnfmPassword = view.findViewById(R.id.cnfmPass);
        register = view.findViewById(R.id.regButton);

        mLoginFormView = view.findViewById(R.id.registerLayout);
        mProgressView = view.findViewById(R.id.login_progress);
        tvLoad = view.findViewById(R.id.tvLoad);
        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backLg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new LoginFragment());
            }
        });
        fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                  checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cnfmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginRegister();
            }
        });
    }
    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.silde_in_left,R.anim.slide_out);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    private void checkInputs(){
        if(!TextUtils.isEmpty(fullName.getText().toString())){
            if(!TextUtils.isEmpty(email.getText().toString())){
                if(password.getText().toString().length()>=8){
                    if(cnfmPassword.getText().toString().length()>=8){
                        register.setEnabled(true);
                    }
                }else{
                    register.setEnabled(false);
                    password.setError("Password must be of 8 characters");
                }
            }else{
                register.setEnabled(false);
                email.setError("Email can't be empty");
            }
        }else{
            register.setEnabled(false);
            fullName.setError("Name Can't be empty");
        }

    }

  private void beginRegister() {
         register.setEnabled(false);
         if(email.getText().toString().matches(emailPattern)){
             if(password.getText().toString().trim().matches(
                     cnfmPassword.getText().toString().trim())){
                 showProgress(true);
                 mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(),
                         password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful()){

                             Map<String,Object> userdata = new HashMap<>();
                             userdata.put("fullname",fullName.getText().toString().trim());

                             firebaseFirestore.collection("users").document(mAuth.getUid())
                                     .set(userdata)
                                     .addOnSuccessListener(new OnSuccessListener<Void>() {
                                         @Override
                                         public void onSuccess(Void aVoid) {

                                             CollectionReference userDataReference = firebaseFirestore.collection("users").document(mAuth.getUid())
                                                     .collection("USER_DATA");

                                             Map<String,Object> wishlistMap = new HashMap<>();
                                             wishlistMap.put("list_size",(long)0);

                                             Map<String,Object> ratingsMap = new HashMap<>();
                                             ratingsMap.put("list_size",(long)0);

                                             Map<String,Object> cartMap = new HashMap<>();
                                             cartMap.put("list_size",(long)0);

                                             Map<String,Object> myAddressesMap = new HashMap<>();
                                             myAddressesMap.put("list_size",(long)0);

                                             final List<String> documentNames = new ArrayList<>();
                                             documentNames.add("MY_WISHLIST");
                                             documentNames.add("MY_RATINGS");
                                             documentNames.add("MY_CART");
                                             documentNames.add("MY_ADDRESSES");


                                             List<Map<String,Object>> documentFields = new ArrayList<>();
                                             documentFields.add(wishlistMap);
                                             documentFields.add(ratingsMap);
                                             documentFields.add(cartMap);
                                             documentFields.add(myAddressesMap);

                                             for(int x = 0;x < documentFields.size();x++){
                                                 final int finalX = x;
                                                 userDataReference.document(documentNames.get(x)).set(documentFields.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<Void> task) {
                                                              if(task.isSuccessful()){
                                                                  if(finalX == documentNames.size()-1) {
                                                                      Intent mainIntent = new Intent(getActivity(), HomeActivity.class);
                                                                      startActivity(mainIntent);
                                                                      getActivity().finish();
                                                                  }
                                                              }else{
                                                                  showProgress(false);
                                                                  Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                                                                  register.setEnabled(true);
                                                              }
                                                     }
                                                 });
                                             }

                                         }
                                     }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Toast.makeText(getActivity(),"Error",Toast.LENGTH_SHORT).show();
                                 }
                             });
                         }
                         else{
                             showProgress(false);
                             Toast.makeText(getActivity(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                             register.setEnabled(true);
                         }
                     }
                 });

             }else{
                   cnfmPassword.setError("Password doesn't match");
                   register.setEnabled(true);
             }
      }else{
              email.setError("Incorrect E-mail");
                     register.setEnabled(true);
      }
  }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
