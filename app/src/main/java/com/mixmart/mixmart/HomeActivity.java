package com.mixmart.mixmart;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener{

    public static Activity mainActivity;
    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDERS_FRAGMENT = 2;
    private static final int WISHLIST_FRAGMENT = 3;
    private static final int ACCOUNT_FRAGMENT = 4;
    public static Boolean showCart = false;
    static FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    static String userName,userEmail;
    FrameLayout frameLayout;
    private int currentFragment = -1;
    private NavigationView navigationView;
    private TextView badgeCount;
    private TextView name,email;
    public static DrawerLayout drawer;
    private int scrollFlags;
    private AppBarLayout.LayoutParams params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
       // toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        scrollFlags = params.getScrollFlags();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        name = navigationView.getHeaderView(0).findViewById(R.id.nav_name);
        email = navigationView.getHeaderView(0).findViewById(R.id.nav_mail);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userEmail = firebaseUser.getEmail();
        DBQueries.firebaseFirestore.collection("users").document(firebaseAuth.getUid()).
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userName = task.getResult().get("fullname").toString();
                name.setText(userName);
            }
        });
        email.setText(userEmail);
        frameLayout = findViewById(R.id.home_frame_layout);
        if (showCart) {
            mainActivity = HomeActivity.this;
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           gotoFragment("My Cart",new MyCartFragment(),-2);
        } else {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            setFragment(new HomeFragment(), HOME_FRAGMENT);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        mainActivity = null;
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(currentFragment == HOME_FRAGMENT) {
                currentFragment = -1;
                super.onBackPressed();
            }else {
                if (showCart) {
                    showCart = false;
                    finish();
                } else {
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (currentFragment == HOME_FRAGMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.home, menu);
            MenuItem cartItem = menu.findItem(R.id.my_cart_appbar);
                cartItem.setActionView(R.layout.badge_layout);
                ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
                badgeIcon.setImageResource(R.drawable.ic_cart);
                badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

                if (DBQueries.cartList.size() == 0) {
                    DBQueries.loadCartList(HomeActivity.this, new Dialog(HomeActivity.this),false,badgeCount,new TextView(HomeActivity.this));
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
                        gotoFragment("My Cart",new MyCartFragment(),CART_FRAGMENT);
                    }
                });

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if( id == R.id.search_appbar){
            return true;
        } else if (id == R.id.notification_appbar) {
            return true;
        }else if (id == R.id.my_cart_appbar) {
           gotoFragment("My Cart",new MyCartFragment(),CART_FRAGMENT);
            return true;
        }else if(id == android.R.id.home){
            if(showCart){
                mainActivity = null;
                showCart = false;
                finish();
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoFragment(String title,Fragment fragment,int fragmentNo) {
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment, fragmentNo);
        if (fragmentNo == CART_FRAGMENT || showCart) {
            navigationView.getMenu().getItem(1).setChecked(true);
            params.setScrollFlags(0);
        }else{
            params.setScrollFlags(scrollFlags);
        }
    }

    MenuItem menuItem;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
       drawer.closeDrawer(GravityCompat.START);
       menuItem = item;
       drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
           @Override
           public void onDrawerClosed(View drawerView) {
               super.onDrawerClosed(drawerView);
               int id = menuItem.getItemId();
               if (id == R.id.nav_home) {
                   invalidateOptionsMenu();
                   setFragment(new HomeFragment(),HOME_FRAGMENT);
               } else if (id == R.id.nav_my_orders) {
                   gotoFragment("My Orders",new MyOrdersFragment(),ORDERS_FRAGMENT);
               } else if(id == R.id.nav_my_wishlist){
                   gotoFragment("My Wishlist",new MyWishlistFragment(),WISHLIST_FRAGMENT);
               }else if (id == R.id.nav_my_account){
                   gotoFragment("My Account",new MyAccountFragment(),ACCOUNT_FRAGMENT);
               }else if (id == R.id.sign_out_button){
                   firebaseAuth.getInstance().signOut();
                   // call clearData method in DBQueries to clear all lists.
                   Intent intent = new Intent(HomeActivity.this,Register.class);
                   startActivity(intent);
                   finish();
               }
           }
       });
        // Handle navigation view item clicks here.

        return true;
    }

    private void setFragment(Fragment fragment,int fragmentNo) {
        if (fragmentNo != currentFragment) {
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }
}
