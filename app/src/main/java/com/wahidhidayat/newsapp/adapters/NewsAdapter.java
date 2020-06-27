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

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    Context context;
    List<Articles> articles;

    DatabaseReference favReference;
    FirebaseUser firebaseUser;

    public NewsAdapter(Context context, List<Articles> articles) {
        this.context = context;
        this.articles = articles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Articles article = articles.get(position);
        holder.tvTitle.setText(article.getTitle());
        holder.tvSource.setText(article.getSource().getName());
        holder.tvDate.setText("\u2022" + dateTime(article.getPublishedAt()));
        String imageUrl = article.getUrlToImage();
        Glide.with(context)
             .load(imageUrl)
             .into(holder.ivBanner);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        favReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).child("favorites");

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String id = "id";
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Favorite favorite = snapshot.getValue(Favorite.class);
                            assert favorite != null;
                            if(favorite.getUrl().equals(article.getUrl())) {
                                id = favorite.getId();
                            }
                        }

                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("title", article.getTitle());
                        intent.putExtra("image", article.getUrlToImage());
                        intent.putExtra("date", article.getPublishedAt());
                        intent.putExtra("source", article.getSource().getName());
                        intent.putExtra("url", article.getUrl());
                        intent.putExtra("id", id);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSource, tvDate;
        ImageView ivBanner;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvSource = itemView.findViewById(R.id.tv_source);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivBanner = itemView.findViewById(R.id.iv_banner);
            cardView = itemView.findViewById(R.id.card_view);
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
