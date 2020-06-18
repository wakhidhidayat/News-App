package com.wahidhidayat.newsapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wahidhidayat.newsapp.BuildConfig;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.adapters.NewsAdapter;
import com.wahidhidayat.newsapp.models.Articles;
import com.wahidhidayat.newsapp.models.Headlines;
import com.wahidhidayat.newsapp.rests.APIClient;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    NewsAdapter adapter;
    EditText etSearch;
    Button btnSearch;
    List<Articles> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        recyclerView = findViewById(R.id.rv_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final String country = getCountry();
        fetch("", country, BuildConfig.API_KEY);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetch("", country, BuildConfig.API_KEY);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etSearch.getText().toString().equals("")) {
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            fetch("", country, BuildConfig.API_KEY);
                        }
                    });
                    fetch("", country, BuildConfig.API_KEY);
                } else {
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            fetch(etSearch.getText().toString(), country, BuildConfig.API_KEY);
                        }
                    });
                    fetch(etSearch.getText().toString(), country, BuildConfig.API_KEY);
                }
            }
        });
    }

    public void fetch(String query, String country, String apiKey) {
        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;

        if(etSearch.getText().toString().equals("")) {
             call = APIClient.getInstance().getApi().getHeadlines(country, apiKey);
        } else {
             call = APIClient.getInstance().getApi().getNews(query, apiKey);
        }


        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                if(response.isSuccessful()) {
                    swipeRefreshLayout.setRefreshing(false);
                    articles.clear();
                    articles = response.body().getArticles();
                    adapter = new NewsAdapter(MainActivity.this, articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }
}