package com.mixmart.mixmart;

import java.util.List;

public class HomePageModel {

    public static final int BANNER_SLIDER = 0;
    public static final int HORIZONTAL_PRODUCT_VIEW = 1;
    public static final int GRID_PRODUCT_VIEW = 2;
    private int type;
    private String background;
    private List<WishlistModel> viewAllProductList;
    private String title;
    private  List<HorizontalProductModel> horizontalProductModelList;
    ////////// banner
    private List<SliderModel> sliderModelList;

    public HomePageModel(int type, String title,String background, List<HorizontalProductModel> horizontalProductModelList,List<WishlistModel> viewAllProductList) {
        this.type = type;
        this.title = title;
        this.background = background;
        this.horizontalProductModelList = horizontalProductModelList;
        this.viewAllProductList = viewAllProductList;
    }

    public HomePageModel(int type, List<SliderModel> sliderModelList) {
        this.type = type;
        this.sliderModelList = sliderModelList;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SliderModel> getSliderModelList() {
        return sliderModelList;
    }

    public void setSliderModelList(List<SliderModel> sliderModelList) {
        this.sliderModelList = sliderModelList;
    }

    public List<WishlistModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishlistModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }
    //////////horizontal

    ///////////// grid product layout


    public HomePageModel(int type, String title,String background, List<HorizontalProductModel> horizontalProductModelList) {
        this.type = type;
        this.title = title;
        this.background = background;
        this.horizontalProductModelList = horizontalProductModelList;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<HorizontalProductModel> getHorizontalProductModelList() {
        return horizontalProductModelList;
    }
    public void setHorizontalProductModelList(List<HorizontalProductModel> horizontalProductModelList) {
        this.horizontalProductModelList = horizontalProductModelList;
    }

    //////////////




}
