package com.mixmart.mixmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import static com.mixmart.mixmart.DBQueries.lists;
import static com.mixmart.mixmart.DBQueries.loadFragmentData;
import static com.mixmart.mixmart.DBQueries.loadedCategoriesName;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private HomePageAdapter adapter;
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String title = getIntent().getStringExtra("categoryName");
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        List<SliderModel> sliderModelFakeList = new ArrayList<>();
//        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
//        sliderModelFakeList.add(new SliderModel("null","#ffffff"));
//        sliderModelFakeList.add(new SliderModel("null","#ffffff"));

        List<HorizontalProductModel> horizontalProductFakeModelList = new ArrayList<>();
//        horizontalProductFakeModelList.add(new HorizontalProductModel("","","","",""));
//        horizontalProductFakeModelList.add(new HorizontalProductModel("","","","",""));
//        horizontalProductFakeModelList.add(new HorizontalProductModel("","","","",""));
//        horizontalProductFakeModelList.add(new HorizontalProductModel("","","","",""));
//        horizontalProductFakeModelList.add(new HorizontalProductModel("","","","",""));
//        horizontalProductFakeModelList.add(new HorizontalProductModel("","","","",""));
//        horizontalProductFakeModelList.add(new HorizontalProductModel("","","","",""));
//
//        homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
//        homePageModelFakeList.add(new HomePageModel(1,"","#fff",horizontalProductFakeModelList,new ArrayList<WishlistModel>()));
//        homePageModelFakeList.add(new HomePageModel(2,"","#fff",horizontalProductFakeModelList,new ArrayList<WishlistModel>()));


        categoryRecyclerView = findViewById(R.id.category_recycler_view);

/////////////////bannerSliderViewpager

        /////////////Horizontal product layout

        ////////////

        ////////Testing
        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(this);
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        categoryRecyclerView.setLayoutManager((testingLayoutManager));

        adapter = new HomePageAdapter(homePageModelFakeList);
        int listPosition = 0;
        for(int x = 0;x < loadedCategoriesName.size();x++){
            if(loadedCategoriesName.get(x).equals(title.toUpperCase())){
                listPosition = x;
            }
        }
        if(listPosition == 0){
            loadedCategoriesName.add(title.toUpperCase());
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(categoryRecyclerView,this,loadedCategoriesName.size()-1,title);
        }else{
            adapter = new HomePageAdapter(lists.get(listPosition));
        }

        categoryRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.seach_icon, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search_appbar) {
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
