package com.wahidhidayat.newsapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.models.Favorite;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    WebView webView;
    Toolbar toolbar;
    FloatingActionButton btnFav;

    FirebaseUser firebaseUser;
    DatabaseReference userReference;
    DatabaseReference favReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        webView = findViewById(R.id.web_view);
        toolbar = findViewById(R.id.toolbar);
        btnFav = findViewById(R.id.btn_fav);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        if (firebaseUser != null) {
            favReference = userReference.child(firebaseUser.getUid()).child("favorites");
        }

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        Intent intent = getIntent();

        final String id = intent.getStringExtra("id");
        final String title = intent.getStringExtra("title");
        final String image = intent.getStringExtra("image");
        final String source = intent.getStringExtra("source");
        final String date = intent.getStringExtra("date");
        final String url = intent.getStringExtra("url");
        final String description = intent.getStringExtra("description");

        assert id != null;
        Log.i("favId", id);
        if (id.equals("id")) {
            btnFav.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.outline_bookmark_border_black_24dp));
        } else {
            btnFav.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.outline_bookmark_black_24dp));
        }

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.equals("id")) {
                    if (firebaseUser != null) {
                        addFavorite(favReference.push().getKey(), url, title, source, date, image, description);
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(DetailActivity.this);
                        alert.setMessage(R.string.to_save_articles);
                        alert.setPositiveButton(getString(R.string.sign_in), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(DetailActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                        alert.setNegativeButton(R.string.cancel, null);

                        // create and show the alert dialog
                        AlertDialog dialog = alert.create();
                        dialog.show();
                    }
                } else {
                    favReference.child(id).child("url").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String urlDb = dataSnapshot.getValue(String.class);
                            assert urlDb != null;
                            if (urlDb.equals(url)) {
                                removeFavorite(id);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    private void addFavorite(String id, String url, String title, String source, String date, String image, String description) {

        Favorite favorite = new Favorite(id, url, title, source, date, image, description);

        favReference.child(id).setValue(favorite).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, R.string.success_add_favorites, Toast.LENGTH_SHORT).show();
                    btnFav.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.outline_bookmark_black_24dp));
                }
            }
        });
    }

    private void removeFavorite(String id) {
        favReference.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, R.string.success_remove_favorites, Toast.LENGTH_SHORT).show();
                    btnFav.setImageDrawable(ContextCompat.getDrawable(DetailActivity.this, R.drawable.outline_bookmark_border_black_24dp));
                }
            }
        });
    }
}
