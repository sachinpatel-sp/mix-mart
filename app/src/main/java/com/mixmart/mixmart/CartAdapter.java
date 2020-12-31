package com.mixmart.mixmart;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.w3c.dom.Text;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartItemModel> cartItemModelList;
    private int lastPosition = -1;
    private TextView cartTotalAmount;
    private boolean showDeleteBtn;

    public CartAdapter(List<CartItemModel> cartItemModelList,TextView cartTotalAmount,boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn=showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()){
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType){
            case CartItemModel.CART_ITEM:
                View cartView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                return new cartItemViewHolder(cartView);
            case CartItemModel.TOTAL_AMOUNT:
                View totalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout,parent,false);
                return new cartTotalAmountViewHolder(totalView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
         switch (cartItemModelList.get(position).getType()){
             case CartItemModel.CART_ITEM:
                 String productID = cartItemModelList.get(position).getProductID();
                 String resource = cartItemModelList.get(position).getProductImage();
                 String title = cartItemModelList.get(position).getProductTitle();
                 Long freeCoupons = cartItemModelList.get(position).getFreeCoupons();
                 String productPrice = cartItemModelList.get(position).getProductPrice();
                 String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                 Long offersApplied = cartItemModelList.get(position).getOffersApplied();
                 boolean inStock = cartItemModelList.get(position).isInStock();
                 ((cartItemViewHolder)holder).setItemDetails(productID,resource,title,freeCoupons,productPrice,cuttedPrice,offersApplied,position,inStock);
                  break;
             case CartItemModel.TOTAL_AMOUNT:
                 int totalItems = 0;
                 int totalItemPrice = 0;
                 String deliveryPrice ;
                 int totalAmount ;
                 int savedAmount = 0;
                 for(int x = 0;x < cartItemModelList.size();x++) {
                     if(cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()){
                         totalItems++;
                         totalItemPrice =totalItemPrice+Integer.parseInt(cartItemModelList.get(x).getProductPrice());
                     }
                 }

                 if(totalItemPrice > 500){
                     deliveryPrice="Free";
                     totalAmount=totalItemPrice;
                 }else{
                     deliveryPrice="60";
                     totalAmount=totalItemPrice+60;
                 }

                 ((cartTotalAmountViewHolder)holder).setTotalAmount(totalItems,totalItemPrice,deliveryPrice,totalAmount,savedAmount);
                   break;
                 default:
                 return;
         }
        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }

    class cartItemViewHolder extends RecyclerView.ViewHolder{
        private ImageView productImage;
        private ImageView freeCouponIcon;
        private TextView productTitle;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView freeCoupons;
        private TextView productQuantity;
        private TextView offersApplied;
        private TextView couponsApplied;

        private LinearLayout clearBtn;
        public cartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            freeCouponIcon = itemView.findViewById(R.id.free_coupon_icons);
            productPrice  = itemView.findViewById(R.id.product_price);
            freeCoupons = itemView.findViewById(R.id.tv_free_coupons);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            offersApplied = itemView.findViewById(R.id.offers_applied);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            couponsApplied = itemView.findViewById(R.id.coupons_applied);
            clearBtn = itemView.findViewById(R.id.remove_item_btn);
        }

        private void setItemDetails(String productID, String resource, String title, Long freeCoupon, String productprice, String cuttedprice, Long offersAppliedNumber, final int position,boolean inStock){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.ic_photo)).into(productImage);
            productTitle.setText(title);
            if(freeCoupon > 0){
                freeCouponIcon.setVisibility(View.VISIBLE);
                freeCoupons.setVisibility(View.VISIBLE);
                if(freeCoupon == 1)
                    freeCoupons.setText("free " + freeCoupon  + " Coupon");
                else
                    freeCoupons.setText("free " + freeCoupon + " Coupons");
            }else{
                freeCouponIcon.setVisibility(View.INVISIBLE);
                freeCoupons.setVisibility(View.INVISIBLE);
            }
            //set cutted Price
            if(inStock){
                productPrice.setText(productprice);
                productPrice.setTextColor(Color.parseColor("#000000"));
                couponsApplied.setVisibility(View.VISIBLE);
                if(offersAppliedNumber > 0){
                    offersApplied.setVisibility(View.VISIBLE);
                    offersApplied.setText(offersAppliedNumber + " offers applied");
                }else{
                    offersApplied.setVisibility(View.INVISIBLE);
                }
                if(productprice != cuttedprice){
                    cuttedPrice.setVisibility(View.VISIBLE);
                    cuttedPrice.setText(cuttedprice);
                }else{
                    cuttedPrice.setVisibility(View.INVISIBLE);
                }
            }else{
                productPrice.setText("Out Of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimaryDark));
                couponsApplied.setVisibility(View.GONE);
                freeCoupons.setVisibility(View.INVISIBLE);
                couponsApplied.setVisibility(View.INVISIBLE);
                freeCouponIcon.setVisibility(View.INVISIBLE);
            }


            if(showDeleteBtn){
                clearBtn.setVisibility(View.VISIBLE);
            }else{
                clearBtn.setVisibility(View.INVISIBLE);
            }
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!ProductDetailsActivity.running_cart_query){
                        ProductDetailsActivity.running_cart_query = true;

                        DBQueries.removeFromCart(position,itemView.getContext(),cartTotalAmount);
                    }
                }
            });

        }
    }

    class cartTotalAmountViewHolder extends RecyclerView.ViewHolder{
        private TextView totalItems,totalItemPrice,deliveryPrice,totalAmount,savedAmount;
        public cartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);
            totalItems = itemView.findViewById(R.id.total_items);
            totalItemPrice = itemView.findViewById(R.id.total_items_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_price);
            totalAmount = itemView.findViewById(R.id.total_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);
        }
        private void setTotalAmount(int totalItemsText,int totalItemPriceText,String deliveryPriceText,int totalAmountText,int savedAmountText){
            totalItems.setText("Price("+totalItemsText+"items)");
            totalItemPrice.setText("$"+totalItemPriceText+"/-");
            if(deliveryPriceText.equals("Free"))
                deliveryPrice.setText(deliveryPriceText);
            else
                deliveryPrice.setText("$"+deliveryPriceText+"/-");

            totalAmount.setText("$"+totalAmountText+"/-");
            cartTotalAmount.setText("$"+totalAmountText+"/-");
            savedAmount.setText("You saved $"+savedAmountText+" on this order.");
            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
            if(totalItemPriceText == 0){
                DBQueries.cartItemModelList.remove(DBQueries.cartItemModelList.size()-1);
                parent.setVisibility(View.GONE);
            }else{
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}
