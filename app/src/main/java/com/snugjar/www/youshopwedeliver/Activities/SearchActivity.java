package com.snugjar.www.youshopwedeliver.Activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.snugjar.www.youshopwedeliver.Adapters.ProductsAdapter;
import com.snugjar.www.youshopwedeliver.Connectors.ApiConnector;
import com.snugjar.www.youshopwedeliver.Connectors.Constants;
import com.snugjar.www.youshopwedeliver.R;

import org.json.JSONArray;

public class SearchActivity extends AppCompatActivity {
    EditText search_bar;
    SwipeRefreshLayout swipe_refresh_layout;
    ImageView back, logo;
    RecyclerView recycler_view;
    String searchText, SSupermarketID, SCountry;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_bar = findViewById(R.id.search);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        back = findViewById(R.id.back);
        recycler_view = findViewById(R.id.recycler_view);
        logo = findViewById(R.id.logo);

        //get supermarketID
        SSupermarketID = getIntent().getStringExtra("id");
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SCountry = sharedPreferences.getString(Constants.COUNTRY, "N/A");

        search_bar.addTextChangedListener(TextEditorWatcher);

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                      @TargetApi(Build.VERSION_CODES.KITKAT)
                                                      @Override
                                                      public void onRefresh() {
                                                          if (!search_bar.getText().toString().equals("")) {
                                                              //search item is not empty
                                                              new GetSearchItems().execute(new ApiConnector());
                                                              swipe_refresh_layout.setRefreshing(true);
                                                          } else {
                                                              //search item is empty
                                                              swipe_refresh_layout.setRefreshing(false);
                                                              logo.setVisibility(View.VISIBLE);
                                                          }
                                                      }
                                                  }
        );

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back
                onBackPressed();
            }
        });

        //dismiss keyboard when user starts to scroll on the recyclerview
        recycler_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
    }

    private final TextWatcher TextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //before a user types
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //as a user types..avoid as may use excessive data
        }

        public void afterTextChanged(Editable s) {
            //after a user has already typed...best place to do action
            searchText = search_bar.getText().toString();
            if (!searchText.equals("")) {
                //if user has typed something
                logo.setVisibility(View.INVISIBLE);
                swipe_refresh_layout.setRefreshing(true);
                new GetSearchItems().execute(new ApiConnector());
            } else {
                //if user hasn't typed anything
                logo.setVisibility(View.VISIBLE);
                //swipe_refresh_layout.setVisibility(View.INVISIBLE);
            }
        }
    };

    public void setSearchAdapter(JSONArray jsonArray) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recycler_view.setLayoutManager(staggeredGridLayoutManager);
        try {
            recycler_view.setAdapter(new ProductsAdapter(jsonArray, this, recycler_view, SSupermarketID));
            /*if (jsonArray == null) {
                available.setVisibility(View.VISIBLE);
                swipe_refresh_layout.setVisibility(View.VISIBLE);
            }*/
            swipe_refresh_layout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSearchItems extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            try {
                swipe_refresh_layout.setVisibility(View.VISIBLE);
                //it is executed on Background thread
                searchText = searchText.replaceAll(" ", "+");
                return params[0].GetSearchItems(searchText, SCountry);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setSearchAdapter(jsonArray);
        }
    }
}
