package com.example.mangoexplorer;


import static com.example.mangoexplorer.Compress_utils.Algorithm.golamb;
import static com.example.mangoexplorer.Compress_utils.Algorithm.lzw;
import static com.example.mangoexplorer.Compress_utils.Algorithm.ren_length;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.widget.Toast;

import com.example.mangoexplorer.Compress_utils.Algorithm;
import com.example.mangoexplorer.Compress_utils.huff;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

public class explorer extends AppCompatActivity {
    LinearLayout layout_middle;
    ConstraintLayout background;
    int selected_item_id = -10000; //any number can't be id
    Button back, refresh, add, remove, share, info, unselect, star, sort, settings, compress;
    TextView path;
    File selected_file;
    queue recent, freq, starred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        recent = Utils.getQueue(explorer.this, Utils.file_recent, Utils.length_recent);
        freq = Utils.getQueue(explorer.this, Utils.file_freq, Utils.length_freq);
        starred = Utils.getQueue(explorer.this, Utils.file_recent, Utils.length_star);
        layout_middle = findViewById(R.id.layout_middle);
        background = findViewById(R.id.background);
        path = findViewById(R.id.path);
        back = findViewById(R.id.back);
        star = findViewById(R.id.star);
        refresh = findViewById(R.id.refresh);
        add = findViewById(R.id.add);
        remove = findViewById(R.id.remove);
        share = findViewById(R.id.share);
        sort = findViewById(R.id.sort);
        settings = findViewById(R.id.settings);
        info = findViewById(R.id.info);
        unselect = findViewById(R.id.unselect);
        compress = findViewById(R.id.compress);

        background.setBackgroundResource(Utils.getBackFromId());
        setAnimation();
        LinearLayout root_item = Utils.getItem(explorer.this, "Files", R.drawable.storage);
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
        setCompress();

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
                        Utils.id_theme = id;
                        background.setBackgroundResource(Utils.getBackFromId());
                        Utils.rewriteSharedPrefrences(explorer.this);
                    }
                });

                builder.setNeutralButton("Enhanced performance (remove animation and sorting)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.better_performance = !Utils.better_performance;
                        Utils.rewriteSharedPrefrences(explorer.this);
                        Utils.showDialog(explorer.this, Utils.better_performance ? "Better Performance is Applied" : "Better Performance is Disabled");
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
                if (Utils.better_performance) {
                    Utils.showDialog(explorer.this, "BetterPerformance is applied" +
                            "\nCan't apply sorting right now.");
                    return;
                }
                final String[] options = {Utils.Sortname, Utils.SortType, Utils.SortDate, Utils.SortSize};

                AlertDialog.Builder builder = new AlertDialog.Builder(explorer.this);
                builder.setTitle("Choose an theme");

                builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedOption = options[which];
                        Utils.typeOfSort = selectedOption;
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
                if ((selected_file != null) && (!selected_file.isDirectory())) {//---->this version only for star files
                    Utils.addToQueueAndSave(explorer.this, Utils.file_starred, starred,
                            new String[]{selected_file.getName(), selected_file.getPath()});
                    Toast.makeText(explorer.this, "starred"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void SetFileDetails() {
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_file != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(explorer.this);
                    LayoutInflater inflater = LayoutInflater.from(explorer.this);
                    View customLayout = inflater.inflate(R.layout.dialog_info, null);


                    TextView input = customLayout.findViewById(R.id.label);
                    input.setText("File:" + selected_file.getName()
                            + "\nlast modified : " + selected_file.lastModified()
                            + "\npath : " + selected_file.getPath()
                            + "\nsize : " + selected_file.length() + " Byte"
                            + "\nPremessions (read:" + selected_file.canRead() + ",write:" + selected_file.canWrite() + ",execute:" + selected_file.canExecute()
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
                        if (!userInput.equals("")) {
                            File newFile = new File(path.getText() + "/" + userInput);
                            try {
                                if (userInput.contains(".")) {//file
                                    newFile.createNewFile();
                                } else {//directory
                                    newFile.mkdir();
                                }
                                newFile.setReadable(true);
                                newFile.setWritable(true);
                                refreshLayout();
                            } catch (IOException e) {
                                Utils.showDialog(explorer.this, "Can't create ,error :" + e.toString() + "\n" + "/" + userInput);
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
                if (selected_item_id != -10000) {//more safe in remove ,as  if some conflict not remove wrong file

                    AlertDialog.Builder builder = new AlertDialog.Builder(explorer.this);
                    builder.setMessage("Do you want to remove the file " + selected_file.getName() + " ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    layout_middle.removeView(findViewById(selected_item_id));
                                    selected_file.delete();
                                    selected_file = null;
                                    selected_item_id = -10000;
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
                if (selected_file != null) {//true to check any one: path string or or selected view
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

    private void setCompress() {

        compress.setOnClickListener(new View.OnClickListener() {
            AlertDialog.Builder builder = new AlertDialog.Builder(explorer.this);
            LayoutInflater inflater = LayoutInflater.from(explorer.this);
            View customLayout = inflater.inflate(R.layout.activity_before_compress, null);

            @Override
            public void onClick(View view) {

                if (selected_file != null) {
                    if (selected_file.isDirectory()) {
                        Utils.showDialog(explorer.this, "This version support compression for only Files of text data");
                        return;
                    }
                    String extension = selected_file.getName().split("\\.")[1];
                    if (Utils.isContain(Algorithm.acceptable_txt_extensions, extension)) {

                        TextView input = customLayout.findViewById(R.id.name);
                        input.setText(selected_file.getPath());
                        builder.setView(customLayout);
                        builder.create().show();

                        ((Button) customLayout.findViewById(R.id.compress)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {//can't put code in the java class of before_compressed,as activity not created ,so code won't invoked
                                String[] compressAlgos = GetCheckedAlgorithms();
                                for (int i = 0; i < compressAlgos.length; i++) {
                                    try {
                                        switch (compressAlgos[i]) {
                                            case Algorithm.huffman: {
                                                huff.Compress(explorer.this, selected_file);
                                                break;
                                            }
                                            case lzw: {
                                                com.example.mangoexplorer.Compress_utils.lzw.Compress(explorer.this, selected_file);
                                                break;
                                            }
                                            case golamb: {
                                                com.example.mangoexplorer.Compress_utils.golamb.Compress(explorer.this, selected_file);
                                                break;
                                            }case ren_length: {
                                                com.example.mangoexplorer.Compress_utils.run.Compress(explorer.this, selected_file);
                                                break;
                                            }

                                        }
                                        refreshLayout();

                                    } catch (Exception ex) {
                                        Utils.showDialog(explorer.this, "Failed to compress \n see logs for details");
                                        Log.e("error compressing", ex.toString());
                                    }
                                }

                            }

                        });
                    } else if (Algorithm.isAcceptable_producied(extension)) {
                        //----> decompress ,decompress is done using same button
                        AlertDialog.Builder builder = new AlertDialog.Builder(explorer.this);
                        builder.setMessage("This is Compressed File ,DeCompress ?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {

                                            if (extension.equals(Algorithm.huffman)) {
                                                huff.DeCompress(explorer.this, selected_file);

                                            } else if (extension.startsWith(lzw)) {
                                                com.example.mangoexplorer.Compress_utils.lzw.DeCompress(explorer.this, selected_file);
                                            } else if (extension.startsWith(golamb)) {
                                                com.example.mangoexplorer.Compress_utils.golamb.DeCompress(explorer.this, selected_file);
                                            }else if (extension.startsWith(ren_length)) {
                                                com.example.mangoexplorer.Compress_utils.run.DeCompress(explorer.this, selected_file);
                                            }

                                            refreshLayout();
                                        } catch (Exception ex) {
                                            Utils.showDialog(explorer.this, "Failed to decompress \n see logs for details");
                                            Log.e("error decompressing", ex.toString());
                                        }
                                    }
                                })
                                .show();
                    } else {
                        Utils.showDialog(explorer.this, "This version support compression for only text data");
                    }
                }
            }


            private String[] GetCheckedAlgorithms() {
                ArrayList<String> SelectedAlgorithms = new ArrayList<String>();
                if (((CheckBox) customLayout.findViewById(R.id.run)).isChecked()) {
                    SelectedAlgorithms.add(Algorithm.ren_length);
                }
                if (((CheckBox) customLayout.findViewById(R.id.huff)).isChecked()) {
                    SelectedAlgorithms.add(Algorithm.huffman);
                }
                if (((CheckBox) customLayout.findViewById(R.id.Golamb)).isChecked()) {
                    SelectedAlgorithms.add(Algorithm.golamb);
                }
                if (((CheckBox) customLayout.findViewById(R.id.arth)).isChecked()) {
                    SelectedAlgorithms.add(Algorithm.arthimetric);
                }
                if (((CheckBox) customLayout.findViewById(R.id.lzw)).isChecked()) {
                    SelectedAlgorithms.add(lzw);
                }
                return (String[]) SelectedAlgorithms.toArray(new String[SelectedAlgorithms.size()]);
            }
        });
    }

    private void setTheSelectless() {
        unselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (findViewById(selected_item_id) != null) {//selected_item_id!=-10000){
                    findViewById(selected_item_id).setBackgroundColor(0);
                    selected_item_id = -10000;
                    selected_file = null;
                }
            }
        });
    }

    private void setLongSelecting(LinearLayout Item, File file) {
        Item.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public boolean onLongClick(View view) {
                try {
                    if (findViewById(selected_item_id) != null) {//this to make only one selected
                        //this code must be at first to remove background at view last selected
                        findViewById(selected_item_id).setBackgroundColor(0);
                        selected_item_id = -10000;
                        selected_file = null;

                    }
                    Item.setBackgroundColor(androidx.cardview.R.color.cardview_shadow_end_color);
                    selected_item_id = Item.getId();
                    selected_file = file;
                } catch (Exception ex) {
                    Log.e("Error,Null Pointer", ex.toString());
                }
                return false;
            }
        });

    }


    private void checkPremessoins(LinearLayout root_item) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                setContet_layout_middle(Environment.getExternalStorageDirectory(), root_item);//new File("/storage/emulated/0/"), root_item);
            }


            @Override
            public void onPermissionDenied(@NonNull List<String> deniedPermissions) {
                Toast.makeText(explorer.this, "Permission Denied ,can't access files\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("You should add Access file premession ")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE//,Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
                .check();
    }

    void refreshLayout() {
        layout_middle.removeAllViews();
        setAnimation();

        File lstDirectory = new File(path.getText().toString());

        setContet_layout_middleAction(lstDirectory, layout_middle);

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


    private void setContet_layout_middleAction(File rootDirectory, LinearLayout root_item) {
        if (rootDirectory.isDirectory()) {
            path.setText(rootDirectory.getPath());
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
                    layout_middle.addView(child_item);
                    child_item.setId(View.generateViewId());//to be unique
                    setLongSelecting(child_item, i);

                    setContet_layout_middle(i, child_item);
                }
            }

        } else {//it's file
            openFile(rootDirectory);

        }
    }

    private void setContet_layout_middle(File rootDirectory, LinearLayout root_item) {

        root_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContet_layout_middleAction(rootDirectory, root_item);

            }
        });
    }


    private void openFile(File i) {
        Uri fileUri = FileProvider.getUriForFile(this, "com.example.mangoexplorer.fileprovider", i);

        // Check if there is an app that can handle the intent
        if (isIntentSafe(this, new Intent(Intent.ACTION_VIEW, fileUri))) {
            Utils.addToQueueAndSave(explorer.this, Utils.file_freq, freq, new String[]{i.getName(), i.getPath()});
            Utils.addToQueueAndSave(explorer.this, Utils.file_recent, recent, new String[]{i.getName(), i.getPath()});

            Intent intent = new Intent(Intent.ACTION_VIEW);
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

    private String getMimeTypeFromExtension(@NonNull File file) {
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
    }

    private void setAnimation() {
        if (!Utils.better_performance) {
            Animation animation = AnimationUtils.loadAnimation(explorer.this, R.anim.layouts);
            findViewById(R.id.card_middle).startAnimation(animation);
        }
    }
}
