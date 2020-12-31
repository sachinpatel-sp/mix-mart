package com.mixmart.mixmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    private static int SPLASH_SCREEN_TIME_OUT=1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.
        setContentView(R.layout.activity_main);
        //this will bind your MainActivity.class file with activity_main.
        firebaseAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(firebaseAuth.getCurrentUser()== null){
                Intent i=new Intent(MainActivity.this,
                        Register.class);
                //Intent is used to switch from one activity to another.

                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
                //invoke the SecondActivity.
                else{
                    Intent i=new Intent(MainActivity.this,
                            HomeActivity.class);
                    //Intent is used to switch from one activity to another.

                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }

                finish();
                //the current activity will get finished.
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}
