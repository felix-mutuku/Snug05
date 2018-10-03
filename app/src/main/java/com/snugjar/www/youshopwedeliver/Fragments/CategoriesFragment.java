package com.snugjar.www.youshopwedeliver.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.snugjar.www.youshopwedeliver.Activities.InternetActivity;
import com.snugjar.www.youshopwedeliver.Adapters.ProductsAdapter;
import com.snugjar.www.youshopwedeliver.Connectors.ApiConnector;
import com.snugjar.www.youshopwedeliver.R;

import org.json.JSONArray;

import static android.content.Context.CONNECTIVITY_SERVICE;

@SuppressLint("ValidFragment")
public class CategoriesFragment extends Fragment {
    String supermarketID, supermarketName, supermarketCategory, supermarketSubcategory, supermarketCountry;
    ConnectivityManager cManager;
    NetworkInfo nInfo;
    RecyclerView recycler_view;
    LinearLayout linear_available;
    SwipeRefreshLayout swipe_refresh_layout;

    public CategoriesFragment(String sCountry, String sSupermarketID, String sSupermarketName, String sCategory, String sSubCategory) {
        //get strings from activity for correct loading of categories selected
        supermarketID = sSupermarketID;
        supermarketName = sSupermarketName;
        supermarketCategory = sCategory;
        supermarketSubcategory = sSubCategory;
        supermarketCountry = sCountry;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        recycler_view = view.findViewById(R.id.recycler_view);
        linear_available = view.findViewById(R.id.linear_available);
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);

        cManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        assert cManager != null;
        nInfo = cManager.getActiveNetworkInfo();

        if (nInfo == null && !nInfo.isConnected()) {
            //when user not connected to the internet
            Intent intent = new Intent(getActivity(), InternetActivity.class);
            startActivity(intent);
        }

        swipe_refresh_layout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        initialize();
                    }
                }
        );

        initialize();

        return view;
    }

    private void initialize() {
        //show user that something is definitely happening
        swipe_refresh_layout.setRefreshing(true);
        //check to see whether user wants all products in category or a specific subcategory
        if (supermarketSubcategory.equals(getString(R.string.all))) {
            //for all categories
            new GetAllCategoryProducts().execute(new ApiConnector());
        } else {
            //for subcategory in a category
            new GetSubCategoryProducts().execute(new ApiConnector());
        }
    }

    public void setProductsAdapter(JSONArray jsonArray) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(staggeredGridLayoutManager);
        try {
            recycler_view.setAdapter(new ProductsAdapter(jsonArray, getActivity(), recycler_view, supermarketID));
            if (jsonArray == null) {
                linear_available.setVisibility(View.VISIBLE);
                //swipe_refresh_layout.setVisibility(View.VISIBLE);
            } else {
                linear_available.setVisibility(View.GONE);
            }
            swipe_refresh_layout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetAllCategoryProducts extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            try {
                return params[0].GetCategoryProducts(supermarketCountry, supermarketCategory);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setProductsAdapter(jsonArray);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSubCategoryProducts extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            try {
                return params[0].GetSubCategoryProducts(supermarketCountry, supermarketCategory, supermarketSubcategory);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setProductsAdapter(jsonArray);
        }
    }

}
