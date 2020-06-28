package com.wahidhidayat.newsapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.adapters.CategoryItemAdapter;
import com.wahidhidayat.newsapp.models.Category;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private CategoryItemAdapter adapter;
    private ArrayList<Category> categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("");

        addData();
        recyclerView = view.findViewById(R.id.rv_category);
        adapter = new CategoryItemAdapter(getActivity(), categoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void addData() {
        categoryList = new ArrayList<>();
        categoryList.add(new Category(getString(R.string.business)));
        categoryList.add(new Category(getString(R.string.entertainment)));
        categoryList.add(new Category(getString(R.string.health)));
        categoryList.add(new Category(getString(R.string.science)));
        categoryList.add(new Category(getString(R.string.sports)));
        categoryList.add(new Category(getString(R.string.technology)));
    }
}
