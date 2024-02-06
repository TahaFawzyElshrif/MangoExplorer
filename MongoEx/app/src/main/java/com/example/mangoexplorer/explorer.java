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
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.Manifest;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

public class explorer extends AppCompatActivity {
    LinearLayout layout_middle;
    ConstraintLayout background;
    int selected_item_id=-10000; //any number can't be id
    String  path_selected;//--->at end of path it have file name
    Button back, refresh, add, remove, share,info,unselect,star,sort,settings;
    TextView path;
    File selected_file;
    queue recent,freq,starred;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        recent=Utils.getQueue(explorer.this,Utils.file_recent,Utils.length_recent);
        freq=Utils.getQueue(explorer.this,Utils.file_freq,Utils.length_freq);
        starred=Utils.getQueue(explorer.this,Utils.file_recent,Utils.length_star);
        layout_middle = findViewById(R.id.layout_middle);
        background=findViewById(R.id.background);
        path = findViewById(R.id.path);
        back = findViewById(R.id.back);
        star=findViewById(R.id.star);
        refresh = findViewById(R.id.refresh);
        add = findViewById(R.id.add);
        remove = findViewById(R.id.remove);
        share = findViewById(R.id.share);
        sort = findViewById(R.id.sort);
        settings = findViewById(R.id.settings);
        info = findViewById(R.id.info);
        unselect=findViewById(R.id.unselect);
        background.setBackgroundResource(Utils.getBackFromId());
        setAnimation();
        LinearLayout root_item = Utils.getItem(explorer.this, "Files", R.drawable.folder);
        layout_middle.addView(root_item);
        checkPremessoins(root_item);
        setBack();
        setRefresh();
        setTheSelectless();
        setRemove();
        setShare();
        setAdd();
        SetFileDetails();
        setStar();
        setTheme();
        setSort();

    }



    private void setTheme() {
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] options = {"theme 1", "theme 2", "theme 3", "theme 4", "theme 5"};

                AlertDialog.Builder builder = new AlertDialog.Builder(explorer.this);
                builder.setTitle("Choose an theme");

                builder.setSingleChoiceItems(options, 4, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String selectedOption = options[id];
                        Utils.id_theme=id;
                        background.setBackgroundResource(Utils.getBackFromId());
                        Utils.rewriteSharedPrefrences(explorer.this);
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
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] options = {Utils.Sortname,Utils.SortType,Utils.SortDate,Utils.SortSize};

                AlertDialog.Builder builder = new AlertDialog.Builder(explorer.this);
                builder.setTitle("Choose an theme");

                builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedOption = options[which];
                        Utils.typeOfSort=selectedOption;
                        Utils.rewriteSharedPrefrences(explorer.this);
                        refreshLayout();


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

    private void setStar() {
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((selected_file!=null)&&(!selected_file.isDirectory())){//---->this version only for star files
                    Utils.addToQueueAndSave(explorer.this,Utils.file_starred,starred,
                            new String[]{selected_file.getName(),selected_file.getPath()});
                    Toast.makeText(explorer.this,"starred"
                            ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





    private void SetFileDetails() {
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_file!=null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(explorer.this);
                    LayoutInflater inflater = LayoutInflater.from(explorer.this);
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

    private void setAdd() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(explorer.this);
                builder.setTitle("Enter New file name (. to make file with extension)");
                EditText input = new EditText(explorer.this);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userInput = input.getText().toString();
                        if(!userInput.equals("")) {
                            File newFile=new File(path.getText()+"/"+userInput);
                            try {
                                if(userInput.contains(".")){//file
                                    newFile.createNewFile();
                                }else{//directory
                                    newFile.mkdir();
                                }
                                refreshLayout();
                            } catch (IOException e) {
                                Utils.showDialog(explorer.this,"Can't create ,error :"+e.toString()+"\n"+path_selected+"/"+userInput);
                            }
                        }
                    }
                });
                builder.create().show();

            }
        });
    }

    private void setRemove() {
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_item_id!=-10000) {//more safe in remove ,as  if some conflict not remove wrong file

                    AlertDialog.Builder builder = new AlertDialog.Builder(explorer.this);
                    builder.setMessage("Do you want to remove the file "+selected_file.getName()+" ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    layout_middle.removeView(findViewById(selected_item_id));
                                    selected_file.delete();
                                    selected_file=null;
                                    selected_item_id=-10000;
                                    path_selected=null;
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    builder.create().show();
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
                    Uri selectedFileUri = FileProvider.getUriForFile(explorer.this, "com.example.mangoexplorer.fileprovider", file);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("*/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, selectedFileUri);
                    startActivity(Intent.createChooser(shareIntent, "Share file using:"));
                }

            }
        });
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


    private void checkPremessoins(LinearLayout root_item) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                setContet_layout_middle(new File("/storage/emulated/0/"), root_item);
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(explorer.this, "Permission Denied ,can't access files\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("You should add Access file premession ")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    void refreshLayout(){
        layout_middle.removeAllViews();
        setAnimation();
        File lstDirectory = new File(path.getText().toString());
        if (lstDirectory.listFiles() != null) {
            for (File i : Utils.sorted(lstDirectory.listFiles())) {
                LinearLayout child_item;
                if (i.isDirectory()) {
                    child_item = Utils.getItem(explorer.this, i.getName(), R.drawable.folder);
                    path.setText(i.getPath());

                } else {
                    child_item = Utils.getItem(explorer.this, i.getName(), Utils.getFileImage(i));
                }

                child_item.setId(View.generateViewId());//to be unique
                setLongSelecting(child_item,i);


                layout_middle.addView(child_item);
                setContet_layout_middle(i, child_item);
            }
        }
    }

    private void setRefresh() {
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refreshLayout();
            }
        });
    }
    private void setBack() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPath = path.getText().toString();
                 String newPath = oldPath.equals("/storage/emulated/0") ? "/storage/emulated/0" : oldPath.substring(0, oldPath.lastIndexOf("/"));
                path.setText(newPath);

                refreshLayout();

            }
        });
    }



    private void setContet_layout_middle(File rootDirectory, LinearLayout root_item) {

        root_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rootDirectory.isDirectory()) {
                    layout_middle.removeAllViews();
                    setAnimation();
                    if (rootDirectory.listFiles() != null) {
                        for (File i : Utils.sorted(rootDirectory.listFiles())) {
                            LinearLayout child_item;
                            if (i.isDirectory()) {
                                child_item = Utils.getItem(explorer.this, i.getName(), R.drawable.folder);
                            } else {
                                child_item = Utils.getItem(explorer.this, i.getName(), Utils.getFileImage(i));
                            }
                            setLongSelecting(child_item,i);
                            child_item.setId(View.generateViewId());//to be unique


                            layout_middle.addView(child_item);
                            setContet_layout_middle(i, child_item);
                        }
                    }
                    path.setText(rootDirectory.getPath());

                } else {//it's file
                    openFile(rootDirectory);

                }

            }
        });
    }


    private void openFile(File i) {
        Uri fileUri = FileProvider.getUriForFile(this, "com.example.mangoexplorer.fileprovider", i);

        // Check if there is an app that can handle the intent
        if (isIntentSafe(this, new Intent(Intent.ACTION_VIEW, fileUri))) {
            Utils.addToQueueAndSave(explorer.this,Utils.file_freq,freq,new String[]{i.getName(),i.getPath()});
            Utils.addToQueueAndSave(explorer.this,Utils.file_recent,recent,new String[]{i.getName(),i.getPath()});

            Intent intent= new Intent(Intent.ACTION_VIEW);
            String mimeType = getMimeTypeFromExtension(i);//get type e.g.: audio
            intent.setDataAndType(fileUri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            Utils.showDialog(explorer.this, "no exist application in your phone to open this type \n try google it !");

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
    private void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(explorer.this, R.anim.layouts);
        findViewById(R.id.card_middle).startAnimation(animation);
    }
}
