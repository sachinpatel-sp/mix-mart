package com.mixmart.mixmart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.mixmart.mixmart.DBQueries.categoryModelList;
import static com.mixmart.mixmart.DBQueries.firebaseFirestore;
import static com.mixmart.mixmart.DBQueries.lists;
import static com.mixmart.mixmart.DBQueries.loadCategories;
import static com.mixmart.mixmart.DBQueries.loadFragmentData;
import static com.mixmart.mixmart.DBQueries.loadedCategoriesName;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }
    private RecyclerView homePageRecyclerView;
    private RecyclerView categoryRecyclerView;
    private HomePageAdapter adapter;
    private CategoryAdaptor categoryAdaptor;
    private List<CategoryModel> categoryModelFakeList = new ArrayList<>();
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    public static SwipeRefreshLayout swipeRefreshLayout;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        categoryRecyclerView = view.findViewById(R.id.category_recycler);
        homePageRecyclerView = view.findViewById(R.id.home_recycler_view);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homePageRecyclerView.setLayoutManager((testingLayoutManager));

        categoryAdaptor = new CategoryAdaptor(categoryModelFakeList);
        adapter = new HomePageAdapter(homePageModelFakeList);

        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);

       if( categoryModelList.size() == 0){
           loadCategories(categoryRecyclerView,getContext());
       }else{
           categoryAdaptor = new CategoryAdaptor(categoryModelList);
           categoryAdaptor.notifyDataSetChanged();
       }

        categoryRecyclerView.setAdapter(categoryAdaptor);

/////////////////bannerSliderViewpager

       //////////Horizontal product layout

        List<HorizontalProductModel> horizontalProductModels = new ArrayList<>();

        ////////////

        ////////Testing

        if( lists.size() == 0){
            loadedCategoriesName.add("HOME");
            lists.add(new ArrayList<HomePageModel>());

            loadFragmentData(homePageRecyclerView,getContext(),0,"Home");

        }else{
            adapter = new HomePageAdapter(lists.get(0));
            adapter.notifyDataSetChanged();
        }
        homePageRecyclerView.setAdapter(adapter);



        ///////////

        ///////// refresh layout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                DBQueries.clearData();
                categoryAdaptor = new CategoryAdaptor(categoryModelFakeList);
                adapter = new HomePageAdapter(homePageModelFakeList);
                categoryRecyclerView.setAdapter(categoryAdaptor);
                homePageRecyclerView.setAdapter(adapter);
                loadCategories(categoryRecyclerView,getContext());
                loadedCategoriesName.add("HOME");
                lists.add(new ArrayList<HomePageModel>());
                loadFragmentData(homePageRecyclerView,getContext(),0,"Home");

            }
        });

         /////////
        return view;
    }
}
