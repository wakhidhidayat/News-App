package com.wahidhidayat.newsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wahidhidayat.newsapp.R;
import com.wahidhidayat.newsapp.models.User;

public class LoginActivity extends AppCompatActivity {


    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    TextView tvContinue;
    ProgressBar progressBar;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvContinue = findViewById(R.id.tv_continue);
        signInButton = findViewById(R.id.btn_google);
        progressBar = findViewById(R.id.pb_login);

        progressBar.setVisibility(View.INVISIBLE);
        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent, 101);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        tvContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressBar.setVisibility(View.GONE);
                            final FirebaseUser user = auth.getCurrentUser();
                            assert user != null;
                            final String userId = user.getUid();
                            final String userName = user.getDisplayName();

                            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");

                            userReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Boolean isInDb = false;
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        User userModel = dataSnapshot.getValue(User.class);
                                        if (userId.equals(userModel.getId())) {
                                            isInDb = true;
                                        }
                                    }

                                    if (isInDb.equals(false)) {
                                        // create new user
                                        register(userId, userName);
                                    } else {
                                        // login user
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user
                        }
                    }
                });
    }

    private void register(String userId, String userName) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        User user = new User(userId, userName);

        reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
