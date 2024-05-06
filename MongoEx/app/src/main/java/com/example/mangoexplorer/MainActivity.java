package com.example.mangoexplorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView logo;
    ConstraintLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //background=findViewById(R.id.background);
        //background.setBackgroundResource(Utils.getBackFromId());
        logo=findViewById(R.id.logo);
        loadSharedPrefrences();
        batteryListen();
        setAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               Utils.goToActivity(MainActivity.this,chooseActivity.class);
               finish();
            }
        }, 2700);

    }

    private void batteryListen() {
        //low battery:
        LowBatteryReceiver lowBatteryReceiver = new LowBatteryReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(lowBatteryReceiver, filter);
        //charging:
        chargingReceiver charging = new chargingReceiver();
        IntentFilter filter2 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(charging, filter2);

    }

    private void setAnimation() {
        if(!Utils.better_performance) {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_circle);
            findViewById(R.id.circle).startAnimation(animation);
        }
    }
    private void loadSharedPrefrences() {
        SharedPreferences sharedPreferences = getSharedPreferences(Utils.PREF_NAME, Context.MODE_PRIVATE);
        Utils.typeOfSort = sharedPreferences.getString("sort type", Utils.SortType);
        Utils.id_theme = sharedPreferences.getInt("theme id", 4);
        Utils.better_performance=sharedPreferences.getBoolean("perfomance_enhanced", false);
        Utils.show_welcome_window=sharedPreferences.getBoolean("Show welcome",true);

    }
}