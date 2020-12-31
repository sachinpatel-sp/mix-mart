package com.mixmart.mixmart;

import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomePageAdapter extends RecyclerView.Adapter {

    private List<HomePageModel> homePageModelList;
     private RecyclerView.RecycledViewPool recycledViewPool;
private int lastPosition = -1;

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch(homePageModelList.get(position).getType()){
            case 0:
                return HomePageModel.BANNER_SLIDER;
            case 1:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;
            case 2:
                return HomePageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case HomePageModel.BANNER_SLIDER:
                View bannerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_ad_layout,parent,false);
                return new BannerSliderViewHolder(bannerView);
                case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                    View horizontalProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.orizontal_scroll_layout,parent,false);
                    return new HorizontalProductViewHolder(horizontalProductView);
            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout,parent,false);
                return new GridProductViewHolder(gridProductView);

            default:
                return null;

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {
            case HomePageModel.BANNER_SLIDER:
                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModelList();
                ((BannerSliderViewHolder) holder).setBannerSliderViewPager(sliderModelList);
                break;
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                String layoutColor = homePageModelList.get(position).getBackground();
                String horizontalLayoutTitle = homePageModelList.get(position).getTitle();
                List<WishlistModel> viewAllProductList = homePageModelList.get(position).getViewAllProductList();
                List<HorizontalProductModel> horizontalProductModelList = homePageModelList.get(position).getHorizontalProductModelList();
                ((HorizontalProductViewHolder) holder).setHorizontalProductLayout(horizontalProductModelList, horizontalLayoutTitle, layoutColor, viewAllProductList);
                break;
            case HomePageModel.GRID_PRODUCT_VIEW:
                String gridLayoutColor = homePageModelList.get(position).getBackground();
                String gridLayoutTitle = homePageModelList.get(position).getTitle();
                List<HorizontalProductModel> gridProductModelList = homePageModelList.get(position).getHorizontalProductModelList();
                ((GridProductViewHolder) holder).setGridProductLayout(gridProductModelList, gridLayoutTitle, gridLayoutColor);
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
        return homePageModelList.size();
    }

    public class BannerSliderViewHolder extends RecyclerView.ViewHolder{
        private int currentBanner;
        private ViewPager bannerSliderViewPager;
        final private  long DELAY_TIME = 3000;
        final private long PERIOD_TIME = 3000;
        private Timer timer;
        Handler handler;
        Runnable update;
        private List<SliderModel> arrangedList;

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerSliderViewPager = itemView.findViewById(R.id.banner_viewpager);

        }
        private void setBannerSliderViewPager(final List<SliderModel> sliderModelList){
            currentBanner = 2;
            if(timer != null){
                timer.cancel();
            }

            arrangedList = new ArrayList<>();
            for(int x = 0; x < sliderModelList.size();x++){
                arrangedList.add(x,sliderModelList.get(x));
            }
            arrangedList.add(0,sliderModelList.get(sliderModelList.size()-2));
            arrangedList.add(1,sliderModelList.get(sliderModelList.size()-1));

            arrangedList.add(sliderModelList.get(0));
            arrangedList.add(sliderModelList.get(1));
            SliderAdaptor sliderAdaptor =new SliderAdaptor(arrangedList);
            bannerSliderViewPager.setAdapter(sliderAdaptor);
            bannerSliderViewPager.setClipToPadding(false);
            bannerSliderViewPager.setPageMargin(20);
            bannerSliderViewPager.setCurrentItem(currentBanner);
            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentBanner = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if(state == ViewPager.SCROLL_STATE_IDLE){
                        pageLooper(arrangedList);
                    }
                }
            };
            bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);

            startSlideshow(arrangedList);

            bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pageLooper(arrangedList);
                    stopSlideshow();
                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        startSlideshow(arrangedList);
                    }
                    return false;
                }
            });
        }
        private void pageLooper(List<SliderModel> sliderModelList){
            if(currentBanner == sliderModelList.size()-2){
                currentBanner = 2;
                bannerSliderViewPager.setCurrentItem(currentBanner,false);
            }

            if(currentBanner == 1){
                currentBanner = sliderModelList.size()-3;
                bannerSliderViewPager.setCurrentItem(currentBanner,false);
            }
        }
        private  void startSlideshow(final List<SliderModel> sliderModelList){
            handler = new Handler();
            update = new Runnable() {
                @Override
                public void run() {
                    if(currentBanner >= sliderModelList.size()){
                        currentBanner = 1;
                    }
                    bannerSliderViewPager.setCurrentItem(currentBanner++,true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            },DELAY_TIME,PERIOD_TIME);
        }
        private void stopSlideshow(){
            timer.cancel();
        }
    }
    public class HorizontalProductViewHolder extends RecyclerView.ViewHolder{

        private TextView horizontalLayoutTitle;
        private Button viewAll;
        private RecyclerView recyclerView;
        private ConstraintLayout constraintLayout;

        public HorizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.horizontal_container);
            horizontalLayoutTitle = itemView.findViewById(R.id.horizontal_scroll_title);
            viewAll = itemView.findViewById(R.id.hsv_all);
            recyclerView = itemView.findViewById(R.id.horizontal_scroll_recycler);
            recyclerView.setRecycledViewPool(recycledViewPool);
        }

        private void setHorizontalProductLayout(List<HorizontalProductModel> horizontalProductModels, final String title, String colors, final List<WishlistModel> viewAllProductList){
            constraintLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f34245")));
            horizontalLayoutTitle.setText(title);
            if(horizontalProductModels.size()>5){
                viewAll.setVisibility(View.VISIBLE);
                viewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.wishlistModelList = viewAllProductList;
                        Intent viewAllIntent = new Intent(itemView.getContext(),ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code",0);
                        viewAllIntent.putExtra("title",title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });
            }else{
                viewAll.setVisibility(View.INVISIBLE);
            }
            HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductModels);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);


            recyclerView.setAdapter(horizontalProductScrollAdapter);
            horizontalProductScrollAdapter.notifyDataSetChanged();
        }
    }
    public class GridProductViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout constraintLayout;
        private TextView gridLayoutTitle;
        private Button gridLayoutButton;
        private GridLayout gridProductLayout;

        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.container_grid_layout);
            gridLayoutTitle = itemView.findViewById(R.id.grid_layot_title);
            gridLayoutButton = itemView.findViewById(R.id.grid_layout_viewall);
            gridProductLayout = itemView.findViewById(R.id.grid_layout);
        }

        private void setGridProductLayout(final List<HorizontalProductModel> horizontalProductModels, final String title, String color) {
            constraintLayout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f34564")));
            gridLayoutTitle.setText(title);

            for (int x = 0; x < 4; x++) {
                ImageView productImage = gridProductLayout.getChildAt(x).findViewById(R.id.hsp_image);
                TextView productTitle = gridProductLayout.getChildAt(x).findViewById(R.id.hsp_title);
                TextView productSeller = gridProductLayout.getChildAt(x).findViewById(R.id.hsp_seller);
                TextView productPrice = gridProductLayout.getChildAt(x).findViewById(R.id.hsp_price);

                Glide.with(itemView.getContext()).load(horizontalProductModels.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.ic_photo)).into(productImage);
                productTitle.setText(horizontalProductModels.get(x).getProductTitle());
                productSeller.setText(horizontalProductModels.get(x).getProductSeller());
                productPrice.setText("₹" + horizontalProductModels.get(x).getProductPrice() + "/-");
                gridProductLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#ffffff"));
                if (!title.equals("")) {
                    final int finalX = x;
                    gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                            productDetailsIntent.putExtra("PRODUCT_ID",horizontalProductModels.get(finalX).getProductID());
                            itemView.getContext().startActivity(productDetailsIntent);
                        }
                    });
                }
            }
            if (!title.equals("")) {
                gridLayoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.horizontalProductModels = horizontalProductModels;
                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code", 1);
                        viewAllIntent.putExtra("key", title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });
            }
        }
    }
}
