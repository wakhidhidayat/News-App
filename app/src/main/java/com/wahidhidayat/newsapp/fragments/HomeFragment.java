package com.wahidhidayat.newsapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.wahidhidayat.newsapp.BuildConfig;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.adapters.NewsAdapter;
import com.wahidhidayat.newsapp.models.Articles;
import com.wahidhidayat.newsapp.models.Headlines;
import com.wahidhidayat.newsapp.rests.APIClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsAdapter adapter;
    private EditText etSearch;
    private Button btnSearch;
    private List<Articles> articles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);
        recyclerView = view.findViewById(R.id.rv_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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

        return view;
    }

    private void fetch(String query, String country, String apiKey) {
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
                    adapter = new NewsAdapter(getActivity(), articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }
}
