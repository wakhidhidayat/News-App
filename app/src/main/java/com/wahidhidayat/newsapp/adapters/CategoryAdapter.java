package com.wahidhidayat.newsapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.activities.DetailActivity;
import com.wahidhidayat.newsapp.models.Articles;
import com.wahidhidayat.newsapp.models.Favorite;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    List<Articles> articles;

    DatabaseReference favReference;
    FirebaseUser firebaseUser;

    public CategoryAdapter(Context context, List<Articles> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Articles article = articles.get(position);
        holder.tvTitle.setText(article.getTitle());
        holder.tvSource.setText(article.getSource().getName());
        holder.tvDate.setText("\u2022" + dateTime(article.getPublishedAt()));
        holder.tvDesc.setText(article.getDescription());
        String imageUrl = article.getUrlToImage();
        holder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.ivBanner);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            favReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("favorites");
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    favReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String id = "id";
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Favorite favorite = snapshot.getValue(Favorite.class);
                                assert favorite != null;
                                if (favorite.getUrl().equals(article.getUrl())) {
                                    id = favorite.getId();
                                }
                            }

                            Intent intent = new Intent(context, DetailActivity.class);
                            intent.putExtra("title", article.getTitle());
                            intent.putExtra("image", article.getUrlToImage());
                            intent.putExtra("date", article.getPublishedAt());
                            intent.putExtra("source", article.getSource().getName());
                            intent.putExtra("url", article.getUrl());
                            intent.putExtra("description", article.getDescription());
                            intent.putExtra("id", id);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("title", article.getTitle());
                    intent.putExtra("image", article.getUrlToImage());
                    intent.putExtra("date", article.getPublishedAt());
                    intent.putExtra("source", article.getSource().getName());
                    intent.putExtra("url", article.getUrl());
                    intent.putExtra("description", article.getDescription());
                    intent.putExtra("id", "id");
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSource, tvDate, tvDesc;
        ImageView ivBanner;
        CardView cardView;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title_category);
            tvSource = itemView.findViewById(R.id.tv_source_category);
            tvDate = itemView.findViewById(R.id.tv_date_category);
            ivBanner = itemView.findViewById(R.id.iv_banner_category);
            cardView = itemView.findViewById(R.id.card_view_category);
            tvDesc = itemView.findViewById(R.id.tv_desc_category);
            progressBar = itemView.findViewById(R.id.pb_category);
        }
    }
}
