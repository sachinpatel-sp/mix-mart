package com.mixmart.mixmart;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private List<WishlistModel> wishlistModelList;
    private Boolean wishlist;
    private int lastPosition = -1;
    public WishlistAdapter(List<WishlistModel> wishlistModelList,Boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlist = wishlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.ViewHolder holder, int position) {
            String productID = wishlistModelList.get(position).getProductID();
            String resource = wishlistModelList.get(position).getProductImage();
            String title = wishlistModelList.get(position).getProductTitle();
            long freeCoupons = wishlistModelList.get(position).getFreeCoupons();
            String rating = wishlistModelList.get(position).getRating();
            long totalRating = wishlistModelList.get(position).getTotalRatings();
            String productPrice = wishlistModelList.get(position).getProductPrice();
            String cuttedPrice = wishlistModelList.get(position).getCuttedPrice();
            boolean inStock = wishlistModelList.get(position).isInStock();
           holder.setData(productID,resource,title,freeCoupons,rating,totalRating,productPrice,cuttedPrice,position,inStock);

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
          private ImageView productImage;
          private TextView productTitle;
          private ImageView couponIcon;
          private TextView productPrice;
          private TextView cuttedPrice;
          private View cutLine;
          private TextView freeCoupons;
          private TextView rating;
          private TextView totalRating;
          private ImageButton deletebtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            couponIcon = itemView.findViewById(R.id.coupon_icon);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            cutLine= itemView.findViewById(R.id.cut_view);
            freeCoupons = itemView.findViewById(R.id.free_coupon);
            rating = itemView.findViewById(R.id.product_rating_tv_mini_view);
            totalRating = itemView.findViewById(R.id.total_ratings);
            deletebtn = itemView.findViewById(R.id.delete_button);

        }

        private void setData(final String productID, String resource, String title, long freeCouponsNo, String averageRate, long totalRatingNo, final String price, String cuttedPriceValue, final int index,boolean inStock){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.ic_menu_gallery)).into(productImage);
            productTitle.setText(title);
            if(freeCouponsNo != 0 && inStock){
                couponIcon.setVisibility(View.VISIBLE);
                if(freeCouponsNo == 1)
                freeCoupons.setText("free "+freeCouponsNo+" coupon");
                else
                    freeCoupons.setText("free "+freeCouponsNo+" coupons");
            }else{
                couponIcon.setVisibility(View.INVISIBLE);
                freeCoupons.setVisibility(View.INVISIBLE);
            }

            if(inStock) {
                rating.setVisibility(View.VISIBLE);
                totalRating.setVisibility(View.VISIBLE);
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setVisibility(View.VISIBLE);

                rating.setText(averageRate);
                totalRating.setText("(" + totalRatingNo + ")" + " ratings");
                productPrice.setText("₹" + price);
                if (cuttedPriceValue.equals(price)) {
                    cutLine.setVisibility(View.GONE);
                    cuttedPrice.setVisibility(View.GONE);
                } else {
                    cutLine.setVisibility(View.VISIBLE);
                    cuttedPrice.setVisibility(View.VISIBLE);
                    cuttedPrice.setText("₹" + cuttedPriceValue);

                }
            }else{
                rating.setVisibility(View.INVISIBLE);
                totalRating.setVisibility(View.INVISIBLE);
                productPrice.setText("Out Of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimaryDark));
                cuttedPrice.setVisibility(View.INVISIBLE);
            }
            if(wishlist){
                deletebtn.setVisibility(View.VISIBLE);
            }else{
                deletebtn.setVisibility(View.GONE);
            }
            deletebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductDetailsActivity.running_wishlist_query) {
                        ProductDetailsActivity.running_wishlist_query = true;
                        DBQueries.removeFromWishlist(index, itemView.getContext());
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailsIntent = new Intent(itemView.getContext(),ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID",productID);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }
}
