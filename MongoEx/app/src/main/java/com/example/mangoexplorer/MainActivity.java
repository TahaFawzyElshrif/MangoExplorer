package com.example.mangoexplorer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Utils.goToActivity(MainActivity.this, explorer.class);
        logo=findViewById(R.id.logo);
        loadSharedPrefrences();
        setAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               Utils.goToActivity(MainActivity.this,chooseActivity.class);
               finish();
            }
        }, 1800);

    }

    private void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_main);
        logo.startAnimation(animation);
    }
    private void loadSharedPrefrences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        Utils.typeOfSort = sharedPreferences.getString("sort type", Utils.SortType);
        Utils.id_theme = sharedPreferences.getInt("theme id", 4);
    }
}