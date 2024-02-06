package com.example.mangoexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;


public class Utils {
    public static final String recent="recent";
    public static final String starred="starred";
    public static final String freq="frequently";

    public static final String file_recent="recent.csv";
    public static final String file_starred="starred.csv";
    public static final String file_freq="freq.csv";//for large data
    public static final int length_freq=100;
    public static final int length_star=150;

    public static final int length_recent=20;
    public static final String Sortname="name";
    public static final String SortType="type";
    public static final String SortSize="size";
    public static final String SortDate="date";
    public static String typeOfSort=SortType;
    public static final String PREF_NAME="Mango_pref";
    public static int id_theme=4;//id is same as images name but -1

    public static LinearLayout getItem(Context parent, String name, int drawableIcon) {
        //example of use :LinearLayout item= Utils.getItem(MainActivity.this,"folder 1",R.drawable.folder)
        LinearLayout icon_layout = getLinearLayout(parent);
        getIcon(parent, drawableIcon, icon_layout);
        TextView iconlbl = getTextView(parent, name);
        icon_layout.addView(iconlbl);
        return icon_layout;
    }

    @NonNull
    private static TextView getTextView(Context parent, String name) {
        TextView iconlbl = new TextView(parent);
        LinearLayout.LayoutParams icoParmas = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);
        icoParmas.setMargins(16, 0, 0, 0);
        iconlbl.setLayoutParams(icoParmas);
        iconlbl.setText(name);
        iconlbl.setGravity(Gravity.CENTER_VERTICAL);
        iconlbl.setTextSize(18);
        iconlbl.setTextColor(ContextCompat.getColor(parent, android.R.color.black));
        return iconlbl;
    }

    @NonNull
    private static LinearLayout getLinearLayout(Context parent) {
        LinearLayout icon_layout = new LinearLayout(parent);
        LinearLayout.LayoutParams icon_layout_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);
        icon_layout_params.setMargins(16, 16, 16, 0);
        icon_layout.setLayoutParams(icon_layout_params);
        return icon_layout;
    }

    private static void getIcon(Context parent, int drawableIcon, LinearLayout icon_layout) {
        ImageView icon = new ImageView(parent);
        icon.setImageResource(drawableIcon);
        icon.setLayoutParams(new LinearLayout.LayoutParams(80, 60));
        icon_layout.addView(icon);
    }

    public static int getFileImage(File file) {
        String[] file_splits = file.getName().split("\\.");
        String extension = file_splits[file_splits.length - 1];//last one is the extension ,to be more general
        if (extension.equals("png") || extension.equals("jpg") || extension.equals("gif") || extension.equals("bmp")
                || extension.equals("jpeg") || extension.equals("tiff")) {
            return R.drawable.img;
        } else if (extension.equals("mp4") || extension.equals("avi") || extension.equals("3gp")) {
            return R.drawable.video;
        } else if (extension.equals("m4a") || extension.equals("mp3") || extension.equals("wav")) {
            return R.drawable.music;
        } else if (extension.equals("docx") || extension.equals("doc") || extension.equals("pdf")) {
            return R.drawable.document;
        } else if (extension.equals("dat") || extension.equals("bin")) {
            return R.drawable.binary;
        } else if (extension.equals("ppt") || extension.equals("pptx")) {
            return R.drawable.powerpoint;
        } else if (extension.equals("csv") || extension.equals("xlsx")) {
            return R.drawable.excel;
        } else if (extension.equals("db") || extension.equals("sql")) {
            return R.drawable.db;
        } else if (extension.equals("txt")) {
            return R.drawable.txt;
        } else if (extension.equals("apk")) {
            return R.drawable.apk;
        }

        return R.drawable.general_file;
    }

    public static void goToActivity(AppCompatActivity from, Class dir) {
        if (from == null || dir == null) {
            Log.e("error", "null page");
            return;
        } else {
            Intent go = new Intent(from, dir);
            from.startActivity(go);
        }
    }

    public static void showDialog(AppCompatActivity currentActivity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public static queue getQueue(Context context, String fileName,int length) {
        queue data = new queue(length);
        try {
            /*file stored as filename,path */
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.openFileInput(fileName)));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                data.enque(values);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            try {
                FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                fos.close();
            } catch (Exception ioException) {
            }
        } catch (Exception e) {
            Log.e("error",e.toString());
            return null;
        }
        return data;//if no file ,so not created and it's empty

    }
    public static void  addToQueueAndSave(Context context,String fileName, queue queue,String[] item) {
        queue.enque(item);
        try {
            BufferedWriter fos = new BufferedWriter(new FileWriter(context.getFilesDir() + "/" + fileName, true));
            fos.write(item[0]+","+item[1]);
            fos.newLine();
            fos.close();
        } catch (Exception e) {
            Log.e("error",e.toString());

        }

    }

    public static void  goToActivityWithData(AppCompatActivity from, Class dir,String type) {
        if (from == null || dir == null) {
            Log.e("error", "null page");
        } else {
            Intent go = new Intent(from, dir);
            go.putExtra("queue_type", type);
            from.startActivity(go);
        }
    }

    public static File[] sorted(File[] files){
        Arrays.sort(files,new FileNameComparator(Utils.typeOfSort));
        return files;
    }

    public static void rewriteSharedPrefrences(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sort type",typeOfSort);
        editor.putInt("theme id",id_theme );
        editor.apply();
    }
    public static int getBackFromId(){
        switch (id_theme){
            case 0:
                return R.drawable.b1;
            case 1:
                return R.drawable.b2;
            case 2:
                return R.drawable.b3;
            case 3:
                return R.drawable.b4;
        }
        return R.drawable.b5;
    }

    static class FileNameComparator implements Comparator<File> {
        String typeOfSort;
        FileNameComparator(String typeOfSort){this.typeOfSort=typeOfSort;}
        @Override
        public int compare(File file1, File file2) {
            switch(typeOfSort){
                case Utils.SortDate:{
                    return Long.compare(file1.lastModified(), file2.lastModified());
                }case Utils.SortType:{
                    if (file1.isDirectory() && !file2.isDirectory()) {
                        return -1;  // Directory comes first
                    } else if (!file1.isDirectory() && file2.isDirectory()) {
                        return 1;   // Directory comes first
                    }
                    return file1.getName().compareTo(file2.getName());//between same folders and files sort in name
                }case Utils.SortSize:{
                    return  Long.compare(file1.length(), file2.length());
                }
            }
            return file1.getName().compareTo(file2.getName());//default is name
        }
    }

}

