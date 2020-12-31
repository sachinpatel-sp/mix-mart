package com.mixmart.mixmart;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder> {
    private List<HorizontalProductModel> horizontalProductModelList;

    public HorizontalProductScrollAdapter(List<HorizontalProductModel> horizontalProductScrollModelList) {
        this.horizontalProductModelList = horizontalProductScrollModelList;
    }

    @NonNull
    @Override
    public HorizontalProductScrollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductScrollAdapter.ViewHolder holder, int position) {

        String resource = horizontalProductModelList.get(position).getProductImage();
        String title = horizontalProductModelList.get(position).getProductTitle();
        String seller = horizontalProductModelList.get(position).getProductSeller();
        String price = horizontalProductModelList.get(position).getProductPrice();
        String productID = horizontalProductModelList.get(position).getProductID();
        holder.setData(resource,title,seller,price,productID);
    }

    @Override
    public int getItemCount() {
        if(horizontalProductModelList.size() > 8)
            return 8;
        else{
            return horizontalProductModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle,productSeller,productPrice;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.hsp_image);
            productTitle = itemView.findViewById(R.id.hsp_title);
            productSeller = itemView.findViewById(R.id.hsp_seller);
            productPrice = itemView.findViewById(R.id.hsp_price);


        }

        private void setData(String resource, String title, String seller, String price, final String productID){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions()).placeholder(R.drawable.ic_photo).into(productImage);
            productTitle.setText(title);
            productPrice.setText("₹"+price+"/-");
            productSeller.setText(seller);

            if(!title.equals("")){
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
}
