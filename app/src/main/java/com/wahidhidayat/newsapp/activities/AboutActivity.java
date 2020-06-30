package com.wahidhidayat.newsapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.wahidhidayat.newsapp.BuildConfig;
import com.wahidhidayat.newsapp.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .setDescription(getString(R.string.about_us_desc))
                .addItem(new Element(getString(R.string.version) + " " +BuildConfig.VERSION_NAME, R.drawable.ic_info_black_24dp))
                .addGroup(getString(R.string.connect_with_us))
                .addEmail(getString(R.string.my_email))
                .addGitHub(getString(R.string.my_github))
                .addTwitter(getString(R.string.my_twitter))
                .create();

        setContentView(aboutPage);
    }
}