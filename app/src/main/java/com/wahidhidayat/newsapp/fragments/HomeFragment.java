package com.wahidhidayat.newsapp.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.wahidhidayat.newsapp.BuildConfig;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.activities.LoginActivity;
import com.wahidhidayat.newsapp.adapters.NewsAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsAdapter adapter;
    private List<Articles> articles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        recyclerView = view.findViewById(R.id.rv_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("");

        final String country = getCountry();
        fetch("", country, BuildConfig.API_KEY);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetch("", country, BuildConfig.API_KEY);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager = (SearchManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.search);

        final String country = getCountry();

        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint(getString(R.string.search_latest_news));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    fetch(query, country, BuildConfig.API_KEY);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fetch(newText, country, BuildConfig.API_KEY);
                return true;
            }
        });
        searchMenuItem.getIcon().setVisible(false, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;

            case R.id.language:
                Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intent);
                return true;

            case R.id.search:
                return false;
        }
        return false;
    }


    private void fetch(String query, String country, String apiKey) {
        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;

        if (query.length() > 0) {
            call = APIClient.getInstance().getApi().getNews(query, apiKey, 100);
        } else {
            call = APIClient.getInstance().getApi().getHeadlines(country, apiKey, 100, "general");
        }

        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(@NonNull Call<Headlines> call, Response<Headlines> response) {
                if (response.isSuccessful()) {
                    swipeRefreshLayout.setRefreshing(false);
                    articles.clear();
                    assert response.body() != null;
                    articles = response.body().getArticles();
                    adapter = new NewsAdapter(getActivity(), articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Headlines> call, Throwable t) {
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
