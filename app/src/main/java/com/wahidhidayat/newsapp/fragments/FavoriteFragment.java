package com.wahidhidayat.newsapp.fragments;

import android.app.SearchManager;
import android.content.Context;
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
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.wahidhidayat.newsapp.adapters.FavoriteAdapter;
import com.wahidhidayat.newsapp.models.Favorite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private List<Favorite> favoriteList;

    private DatabaseReference favReference;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = view.findViewById(R.id.rv_fav);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        favReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("favorites");
        Log.i("tag", favReference.toString());

        favoriteList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("error retrieve", databaseError.getDetails());
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

        assert searchManager != null;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint(getString(R.string.search_latest_news));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    searchNews(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchNews(newText);
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
