package com.wahidhidayat.newsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wahidhidayat.newsapp.BuildConfig;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.adapters.CategoryAdapter;
import com.wahidhidayat.newsapp.models.Articles;
import com.wahidhidayat.newsapp.models.Headlines;
import com.wahidhidayat.newsapp.rests.APIClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvToolbar;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Articles> articles = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.rv_category_activity);
        toolbar = findViewById(R.id.toolbar_category);
        tvToolbar = findViewById(R.id.tv_toolbar_category);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_category);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        final String categoryIntent = intent.getStringExtra("category");
        final String country = getCountry();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        assert categoryIntent != null;
        switch (categoryIntent) {
            case "business":
                tvToolbar.setText(R.string.business);
                break;
            case "entertainment":
                tvToolbar.setText(R.string.entertainment);
                break;
            case "health":
                tvToolbar.setText(R.string.health);
                break;
            case "science":
                tvToolbar.setText(R.string.science);
                break;
            case "sports":
                tvToolbar.setText(R.string.sports);
                break;
            case "technology":
                tvToolbar.setText(R.string.technology);
                break;
            case "trending us":
                tvToolbar.setText(R.string.trending_us);
                break;
            case "trending id":
                tvToolbar.setText(R.string.trending_id);
                break;
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        if(categoryIntent.equals("trending us")) {
            fetch("us", "general", BuildConfig.API_KEY);
        } else if (categoryIntent.equals("trending id")) {
            fetch("id", "general", BuildConfig.API_KEY);
        } else {
            fetch(country, categoryIntent, BuildConfig.API_KEY);
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(categoryIntent.equals("trending us")) {
                    fetch("us", "general", BuildConfig.API_KEY);
                } else if (categoryIntent.equals("trending id")) {
                    fetch("id", "general", BuildConfig.API_KEY);
                } else {
                    fetch(country, categoryIntent, BuildConfig.API_KEY);
                }
            }
        });
    }

    private void fetch(String country, String category, String apiKey) {
        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;

        call = APIClient.getInstance().getApi().getHeadlines(country, apiKey, 100, category);

        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(@NonNull Call<Headlines> call, Response<Headlines> response) {
                if (response.isSuccessful()) {
                    articles.clear();
                    swipeRefreshLayout.setRefreshing(false);
                    assert response.body() != null;
                    articles = response.body().getArticles();
                    adapter = new CategoryAdapter(CategoryActivity.this, articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(CategoryActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }
}
