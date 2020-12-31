package com.mixmart.mixmart;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class GridProductAdapter extends BaseAdapter {
    List<HorizontalProductModel> horizontalProductModelsList;

    public GridProductAdapter(List<HorizontalProductModel> horizontalProductModelsList){
        this.horizontalProductModelsList = horizontalProductModelsList;
    }

    @Override
    public int getCount() {
        return horizontalProductModelsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,null);
            view.setElevation(0);
            view.setBackgroundColor(Color.parseColor("#ffffff"));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  Intent productDetailsIntent = new Intent(v.getContext(),ProductDetailsActivity.class);
                  productDetailsIntent.putExtra("PRODUCT_ID",horizontalProductModelsList.get(position).getProductID());
                  v.getContext().startActivity(productDetailsIntent);
                }
            });

            ImageView productImage = view.findViewById(R.id.hsp_image);
            TextView productTitle = view.findViewById(R.id.hsp_title);
            TextView productSeller = view.findViewById(R.id.hsp_seller);
            TextView productPrice = view.findViewById(R.id.hsp_price);

            Glide.with(parent.getContext()).load(horizontalProductModelsList.get(position).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.ic_photo)).into(productImage);
            productTitle.setText(horizontalProductModelsList.get(position).getProductTitle());
            productSeller.setText(horizontalProductModelsList.get(position).getProductSeller());
            productPrice.setText("₹"+horizontalProductModelsList.get(position).getProductPrice()+"/-");
        }else{
            view = convertView;
        }

        return view;
    }
}
