package com.example.mangoexplorer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chaquo.python.PyObject;

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
        showWelcome();

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
      //  PyObject obj = Utils.startPython(chooseActivity.this, "general");
      //  int static_eval = obj.callAttr("text", 5).toInt();
      //  Utils.showDialog(chooseActivity.this,static_eval+"");


    }

    private void setAnimation() {
        if(!Utils.better_performance) {
            Animation animation = AnimationUtils.loadAnimation(chooseActivity.this, R.anim.layouts);
            findViewById(R.id.card_middle).startAnimation(animation);
        }
    }
    private void showWelcome() {
        if (Utils.show_welcome_window) {
            AlertDialog.Builder builder = new AlertDialog.Builder(chooseActivity.this);
            LayoutInflater inflater = LayoutInflater.from(chooseActivity.this);
            View customLayout = inflater.inflate(R.layout.dialog_info, null);


            TextView input = customLayout.findViewById(R.id.label);
            input.setText("Uploaded V1.5\n" +
                    "- Solved problem of refresh-back button \n" +
                    "- Add compression feature for text files\n" +
                    "- Add Improve prefromance feature ");
            builder.setView(customLayout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Utils.show_welcome_window = false;
                    Utils.rewriteSharedPrefrences(chooseActivity.this);
                }
            });
            input.setTextSize(20);

            builder.create().show();
        }
    }
}