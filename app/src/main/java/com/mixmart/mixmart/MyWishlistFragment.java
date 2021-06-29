package com.mixmart.mixmart;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyWishlistFragment extends Fragment {

    public MyWishlistFragment() {
        // Required empty public constructor
    }

    public ShimmerFrameLayout shimmerFrameLayout;
    private RecyclerView wishlistRecyclerView;
    public static WishlistAdapter wishlistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_wishlist, container, false);

        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
        wishlistRecyclerView = view.findViewById(R.id.my_wishlist_recycler_view);
        wishlistRecyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        wishlistRecyclerView.setLayoutManager(linearLayoutManager);
        if(DBQueries.wishlistModelList.size()==0){
            DBQueries.wishlist.clear();
            DBQueries.loadWishlist(getContext(),true);
        }

        wishlistAdapter = new WishlistAdapter(DBQueries.wishlistModelList,true);
        wishlistRecyclerView.setAdapter(wishlistAdapter);
        shimmerFrameLayout.stopShimmerAnimation();
        shimmerFrameLayout.setVisibility(view.GONE);
        wishlistRecyclerView.setVisibility(view.VISIBLE);
        wishlistAdapter.notifyDataSetChanged();
        return view;
    }

}
