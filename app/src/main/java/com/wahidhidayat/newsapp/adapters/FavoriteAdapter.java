package com.wahidhidayat.newsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.activities.DetailActivity;
import com.wahidhidayat.newsapp.models.Favorite;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    Context context;
    List<Favorite> favorites;

    public FavoriteAdapter(Context context, List<Favorite> favorites) {
        this.context = context;
        this.favorites = favorites;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorites, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Favorite favorite = favorites.get(position);
        holder.tvTitle.setText(favorite.getTitle());
        holder.tvSource.setText(favorite.getSource());
        holder.tvDate.setText("\u2022" + dateTime(favorite.getDate()));

        String imageUrl = favorite.getImage();

        Glide.with(context)
                .load(imageUrl)
                .into(holder.ivBanner);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("title", favorite.getTitle());
                intent.putExtra("image", favorite.getImage());
                intent.putExtra("date", favorite.getDate());
                intent.putExtra("source", favorite.getSource());
                intent.putExtra("url", favorite.getUrl());
                intent.putExtra("id", favorite.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSource, tvDate;
        ImageView ivBanner;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title_fav);
            tvSource = itemView.findViewById(R.id.tv_source_fav);
            tvDate = itemView.findViewById(R.id.tv_date_fav);
            ivBanner = itemView.findViewById(R.id.iv_banner_fav);
            cardView = itemView.findViewById(R.id.card_view_fav);
        }
    }

    public String getCountry() {
        Locale locale = Locale.getDefault();
        String country = locale.getCountry();
        return country.toLowerCase();
    }

    public String dateTime(String t) {
        PrettyTime prettyTime = new PrettyTime(new Locale(getCountry()));
        String time = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:", Locale.ENGLISH);
            Date date = simpleDateFormat.parse(t);
            time = prettyTime.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }
}
