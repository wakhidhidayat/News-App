package com.wahidhidayat.newsapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.adapters.FavoriteAdapter;
import com.wahidhidayat.newsapp.models.Favorite;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FavoriteAdapter adapter;
    EditText etSearch;
    Button btnSearch;
    List<Favorite> favoriteList;

    DatabaseReference favReference;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_fav);
        recyclerView = view.findViewById(R.id.rv_fav);
        etSearch = view.findViewById(R.id.et_search_fav);
        btnSearch = view.findViewById(R.id.btn_search_fav);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        favReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("favorites");
        Log.i("tag", favReference.toString());

        favoriteList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        favReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Favorite favorite = snapshot.getValue(Favorite.class);
                    favoriteList.add(favorite);
                }
                adapter = new FavoriteAdapter(getActivity(), favoriteList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("err", databaseError.getDetails());
            }
        });

        return view;
    }
}
