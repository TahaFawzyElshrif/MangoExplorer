package com.example.mangoexplorer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;

public class ShowQueued extends AppCompatActivity {
    LinearLayout  lay_middle;
    TextView title;
    queue  queue;
    String type;
    Button share,info,unselect,settings;
    int selected_item_id=-10000;
    String  path_selected;
    ConstraintLayout background;
    File selected_file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_queued);
        lay_middle=findViewById(R.id.lay_middle);
        title=findViewById(R.id.title);
        share = findViewById(R.id.share);
        info = findViewById(R.id.info);
        unselect=findViewById(R.id.unselect);
        settings=findViewById(R.id.settings);

        background=findViewById(R.id.background);
        background.setBackgroundResource(Utils.getBackFromId());

        type= getIntent().getStringExtra("queue_type");
        title.setText(type);

        getMainQueue();
        setLayMiddle();

        setTheSelectless();
        setShare();
        SetFileDetails();
        setSort();
        setSettings();


    }

    private void setSettings() {
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] options = {"theme 1", "theme 2", "theme 3", "theme 4", "theme 5"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ShowQueued.this);
                builder.setTitle("Choose an theme");

                builder.setSingleChoiceItems(options, 4, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String selectedOption = options[id];
                        Utils.id_theme=id;
                        background.setBackgroundResource(Utils.getBackFromId());
                        Utils.rewriteSharedPrefrences(ShowQueued.this);
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });



                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    private void setSort() {
    }

    private void setTheSelectless() {
        unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_item_id!=-10000){
                    findViewById(selected_item_id).setBackgroundColor(0);
                    selected_item_id=-10000;
                    path_selected=null;
                    selected_file=null;
                }
            }
        });
    }
    private void setShare() {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_file!=null) {//true to check any one: path string or or selected view
                    File file = selected_file;
                    Uri selectedFileUri = FileProvider.getUriForFile(ShowQueued.this, "com.example.mangoexplorer.fileprovider", file);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("*/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, selectedFileUri);
                    startActivity(Intent.createChooser(shareIntent, "Share file using:"));
                }

            }
        });
    }
    private void SetFileDetails() {
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_file!=null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShowQueued.this);
                    LayoutInflater inflater = LayoutInflater.from(ShowQueued.this);
                    View customLayout = inflater.inflate(R.layout.dialog_info, null);


                    TextView input =customLayout.findViewById(R.id.label);
                    input.setText("File:" + selected_file.getName()
                            +"\nlast modified : "+selected_file.lastModified()
                            +"\npath : "+selected_file.getPath()
                            +"\nsize : "+selected_file.length()+" Byte"
                            +"\nPremessions (read:"+selected_file.canRead()+",write:"+selected_file.canWrite()+",execute:"+selected_file.canExecute()
                    );
                    builder.setView(customLayout)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    input.setTextSize(20);

                    builder.create().show();
                }
            }
        });
    }
    void getMainQueue(){
        switch (type) {
            case (Utils.recent): {
                queue=Utils.getQueue(ShowQueued.this,Utils.file_recent,Utils.length_recent);
                break;
            }
            case (Utils.freq): {
                queue=Utils.getQueue(ShowQueued.this,Utils.file_freq,Utils.length_freq);
                break;
            } default:{//Utils.starred
                queue=Utils.getQueue(ShowQueued.this,Utils.file_starred,Utils.length_recent);
                break;
            }
        }

    }
    private void setLayMiddle() {
        setAnimation();
        if(!type.equals(Utils.freq)) {
            //this part if only it's starred or recently
            if(type.equals(Utils.starred)){
                Utils.showDialog(ShowQueued.this,"this version have max 150 starred file ,then replace oldest one");
            }
            for (String[] name_path : (String[][]) queue.printOrdered()) {
                ItemAddViewSetButtons(name_path);
            }
        }else {
            //if in frequently use the freqmap
            Map<String[],Integer> sorted=queue.sortByValue(queue.freqMap());

            for (String[] name_path : sorted.keySet()) {
                ItemAddViewSetButtons(name_path);
            }
        }
    }

    private  void ItemAddViewSetButtons(String[] name_path) {
        if (name_path != null) {
            String name = name_path[0];
            String path = name_path[1];
            LinearLayout view=Utils.getItem(ShowQueued.this, name, Utils.getFileImage(new File(path)));
            lay_middle.addView(view);
            view.setId(View.generateViewId());//to be unique
            setLongSelecting(view,new File(path));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFile(new File(path));
                }
            });
        }
    }

    private void openFile(File i) {
        Uri fileUri = FileProvider.getUriForFile(this, "com.example.mangoexplorer.fileprovider", i);

        // Check if there is an app that can handle the intent
        if (isIntentSafe(this, new Intent(Intent.ACTION_VIEW, fileUri))) {
            Intent intent= new Intent(Intent.ACTION_VIEW);
            String mimeType = getMimeTypeFromExtension(i);//get type e.g.: audio
            intent.setDataAndType(fileUri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            Utils.showDialog(ShowQueued.this, "no exist application in your phone to open this type \n try google it !");

        }

    }
    private boolean isIntentSafe(Context context, Intent intent) {//check if there exist application to open file
        PackageManager packageManager = context.getPackageManager();
        return intent.resolveActivity(packageManager) != null;
    }

    private String getMimeTypeFromExtension(File file) {
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
    }

    private void setLongSelecting(LinearLayout Item,File file) {
        Item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(selected_item_id!=-10000){//this to make only one selected
                    //this code must be at first to remove background at view last selected
                    findViewById(selected_item_id).setBackgroundColor(0);
                }
                Item.setBackgroundColor(androidx.cardview.R.color.cardview_shadow_end_color);
                selected_item_id=Item.getId();
                path_selected=file.getPath();
                selected_file=file;
                return false;
            }
        });

    }
    private void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(ShowQueued.this, R.anim.layouts);
        findViewById(R.id.card_middle).startAnimation(animation);
    }
}