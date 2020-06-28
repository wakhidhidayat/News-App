package com.wahidhidayat.newsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.activities.CategoryActivity;
import com.wahidhidayat.newsapp.activities.MainActivity;
import com.wahidhidayat.newsapp.models.Category;

import java.util.ArrayList;

public class CategoryItemAdapter extends RecyclerView.Adapter<CategoryItemAdapter.ViewHolder> {

    private ArrayList<Category> categories;
    private Context context;

    public CategoryItemAdapter(Context context, ArrayList<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.tvCategory.setText(categories.get(position).getCategoryItem());

        String category = "category";
        String tvValue = holder.tvCategory.getText().toString();

        if(tvValue.equals("Business")) {
            category = "business";
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attach_money_black_24dp, 0, 0, 0);
        } else if(tvValue.equals("Entertainment")) {
            category = "entertainment";
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_live_tv_black_24dp, 0, 0, 0);
        } else if(tvValue.equals("Health")) {
            category = "health";
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_hospital_black_24dp, 0, 0, 0);
        } else if(tvValue.equals("Science")) {
            category = "science";
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_functions_black_24dp, 0, 0, 0);
        } else if(tvValue.equals("Sports")) {
            category = "sports";
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fitness_center_black_24dp, 0, 0, 0);
        } else if(tvValue.equals("Technology")) {
            category = "technology";
            holder.tvCategory.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_desktop_mac_black_24dp, 0, 0, 0);
        }

        final String finalCategory = category;
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CategoryActivity.class);
                intent.putExtra("category", finalCategory);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        TextView tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.layout_item_categories);
            tvCategory = itemView.findViewById(R.id.tv_category_item);
        }
    }
}
