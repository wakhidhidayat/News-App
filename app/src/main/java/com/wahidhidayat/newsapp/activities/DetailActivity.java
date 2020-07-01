package com.wahidhidayat.newsapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    ProgressBar progressBar;

    FirebaseUser firebaseUser;
    DatabaseReference userReference;
    DatabaseReference favReference;

    private String EXTRA_ID = "id";
    private String EXTRA_TITLE = "title";
    private String EXTRA_IMAGE = "image";
    private String EXTRA_SOURCE = "source";
    private String EXTRA_DATE = "date";
    private String EXTRA_URL = "url";
    private String EXTRA_DESCRIPTION = "desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        webView = findViewById(R.id.web_view);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.pb_webview);
        progressBar.setVisibility(View.VISIBLE);

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
        EXTRA_ID = intent.getStringExtra("id");
        EXTRA_TITLE = intent.getStringExtra("title");
        EXTRA_IMAGE = intent.getStringExtra("image");
        EXTRA_SOURCE = intent.getStringExtra("source");
        EXTRA_DATE = intent.getStringExtra("date");
        EXTRA_URL = intent.getStringExtra("url");
        EXTRA_DESCRIPTION = intent.getStringExtra("description");

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewArticle());
        webView.loadUrl(EXTRA_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_menu, menu);
        MenuItem btnSave = menu.findItem(R.id.save);

        // check if article has already in favorites or not
        assert EXTRA_ID != null;
        if (EXTRA_ID.equals("id")) {
            btnSave.setIcon(R.drawable.ic_bookmark_border_black_24dp);
        } else {
            btnSave.setIcon(R.drawable.ic_bookmark_black_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                try {
                    Intent intentShare = new Intent(Intent.ACTION_SEND);
                    intentShare.setType("text/plan");
                    intentShare.putExtra(Intent.EXTRA_SUBJECT, EXTRA_TITLE);
                    intentShare.putExtra(Intent.EXTRA_TEXT, EXTRA_TITLE + "\nSee more this article in " + EXTRA_URL + "\n\nShared from NewsApp");
                    startActivity(Intent.createChooser(intentShare, getString(R.string.share_with)));
                } catch (Exception e) {
                    Toast.makeText(this, R.string.error_share, Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.open_browser:
                Intent intentBrowser = new Intent(Intent.ACTION_VIEW);
                intentBrowser.setData(Uri.parse(EXTRA_URL));
                startActivity(intentBrowser);
                return true;

            case R.id.save:
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                userReference = FirebaseDatabase.getInstance().getReference("Users");

                if (firebaseUser != null) {
                    favReference = userReference.child(firebaseUser.getUid()).child("favorites");
                }

                assert EXTRA_ID != null;
                if (EXTRA_ID.equals("id")) {
                    if (firebaseUser != null) {
                        addFavorite(favReference.push().getKey(), EXTRA_URL, EXTRA_TITLE, EXTRA_SOURCE, EXTRA_DATE, EXTRA_IMAGE, EXTRA_DESCRIPTION);
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
                    favReference.child(EXTRA_ID).child("url").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String urlDb = dataSnapshot.getValue(String.class);
                            assert urlDb != null;
                            if (urlDb.equals(EXTRA_URL)) {
                                removeFavorite(EXTRA_ID);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                return true;
        }
        return false;
    }

    private void addFavorite(String id, String url, String title, String source, String date, String image, String description) {

        Favorite favorite = new Favorite(id, url, title, source, date, image, description);

        favReference.child(id).setValue(favorite).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(DetailActivity.this, R.string.success_add_favorites, Toast.LENGTH_SHORT).show();
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
                }
            }
        });
    }

    public class WebViewArticle extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }
}
