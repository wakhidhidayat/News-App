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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wahidhidayat.newsapp.BuildConfig;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.activities.LoginActivity;
import com.wahidhidayat.newsapp.activities.MainActivity;
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
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    private LinearLayout homeLayout;
    private LinearLayout errorLayout;
    private ImageView ivError;
    private TextView tvErrorTitle;
    private TextView tvErrorMessage;
    private Button btnRetry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        recyclerView = view.findViewById(R.id.rv_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        homeLayout = view.findViewById(R.id.home_layout);
        errorLayout = view.findViewById(R.id.layout_error);
        ivError = view.findViewById(R.id.iv_error);
        tvErrorTitle = view.findViewById(R.id.tv_error_title);
        tvErrorMessage = view.findViewById(R.id.tv_error_message);
        btnRetry = view.findViewById(R.id.btn_retry);

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

        // change icon sign out item
        MenuItem menuItem = menu.findItem(R.id.logout);
        if (firebaseUser == null) {
            menuItem.setTitle(getString(R.string.sign_in));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                if (firebaseUser != null) {
                    // firebase signout
                    FirebaseAuth.getInstance().signOut();

                    // google signout
                    mGoogleSignInClient.signOut();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    Toast.makeText(getActivity(), R.string.success_sign_out, Toast.LENGTH_SHORT).show();
                    return true;
                }
                startActivity(new Intent(getActivity(), LoginActivity.class));
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
        errorLayout.setVisibility(View.INVISIBLE);
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
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    showError(R.drawable.no_result, "No Result", "Please Try Again\n" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showError(R.drawable.no_result, "Oops", "Network Failure, Please Try Again\n" + t.getLocalizedMessage());

                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showError(int imageView, String title, String message) {
        if (errorLayout.getVisibility() == View.INVISIBLE) {
            errorLayout.setVisibility(View.VISIBLE);
            homeLayout.setVisibility(View.GONE);
        }
        ivError.setImageResource(imageView);
        tvErrorTitle.setText(title);
        tvErrorMessage.setText(message);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        fetch("", getCountry(), BuildConfig.API_KEY);
                        homeLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }
}
