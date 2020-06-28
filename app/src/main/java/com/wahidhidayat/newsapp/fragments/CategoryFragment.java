package com.wahidhidayat.newsapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.adapters.CategoryItemAdapter;
import com.wahidhidayat.newsapp.models.Category;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private CategoryItemAdapter adapter;
    private ArrayList<Category> categoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
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
