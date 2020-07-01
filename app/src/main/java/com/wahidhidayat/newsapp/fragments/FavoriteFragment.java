package com.wahidhidayat.newsapp.fragments;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.activities.LoginActivity;
import com.wahidhidayat.newsapp.activities.MainActivity;
import com.wahidhidayat.newsapp.adapters.FavoriteAdapter;
import com.wahidhidayat.newsapp.models.Favorite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FavoriteAdapter adapter;
    private List<Favorite> favoriteList;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private DatabaseReference favReference;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = view.findViewById(R.id.rv_fav);
        progressBar = view.findViewById(R.id.pb_item_fav);
        progressBar.setVisibility(View.VISIBLE);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("");

        if (firebaseUser != null) {
            favReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("favorites");
        }

        favoriteList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (firebaseUser != null) {
            favReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    favoriteList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Favorite favorite = snapshot.getValue(Favorite.class);
                        favoriteList.add(favorite);
                    }
                    adapter = new FavoriteAdapter(getActivity(), favoriteList);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.i("error retrieve", databaseError.getDetails());
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage(R.string.you_must_be_logged_in);
            alert.setPositiveButton(getString(R.string.sign_in), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
            alert.setNegativeButton(R.string.cancel, null);

            // create and show the alert dialog
            AlertDialog dialog = alert.create();
            dialog.show();
        }

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

        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint(getString(R.string.search_latest_news));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (firebaseUser != null) {
                    if (query.length() > 2) {
                        searchNews(query);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (firebaseUser != null) {
                    searchNews(newText);
                    return true;
                }
                return false;
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

    private void searchNews(String s) {
        Query query = favReference.orderByChild("title").startAt(s).endAt(s + "\uf8ff"); // The character \uf8ff used in the query is a very high code point in the Unicode range (it is a Private Usage Area [PUA] code)
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Favorite favorite = snapshot.getValue(Favorite.class);
                    favoriteList.add(favorite);
                }
                adapter = new FavoriteAdapter(getActivity(), favoriteList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("error search", databaseError.getDetails());
            }
        });
    }
}
