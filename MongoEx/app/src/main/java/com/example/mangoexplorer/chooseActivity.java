package com.example.mangoexplorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;

public class chooseActivity extends AppCompatActivity {
    ImageButton explore,recent,starred,freq,downloads,pictures;
ConstraintLayout background;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        explore=findViewById(R.id.explore);
        recent=findViewById(R.id.recent);
        starred=findViewById(R.id.stared);
        freq=findViewById(R.id.freq);
        downloads=findViewById(R.id.downloads);
        pictures=findViewById(R.id.photos);
        background=findViewById(R.id.background);
        background.setBackgroundResource(Utils.getBackFromId());
        setAnimation();
        explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.goToActivity(chooseActivity.this,explorer.class);
            }
        });
        recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.goToActivityWithData(chooseActivity.this,ShowQueued.class,Utils.recent);
            }
        });
        freq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.goToActivityWithData(chooseActivity.this,ShowQueued.class,Utils.freq);
            }
        });
        starred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.goToActivityWithData(chooseActivity.this,ShowQueued.class,Utils.starred);
            }
        });

    }

    private void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(chooseActivity.this, R.anim.layouts);
        findViewById(R.id.card_middle).startAnimation(animation);
    }
}