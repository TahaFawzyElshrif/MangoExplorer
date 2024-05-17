package com.example.mangoexplorer.Compress_utils;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.example.mangoexplorer.Utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;

public class run  extends Algorithm implements Serializable {
    public static StringBuffer compressText(AppCompatActivity context,StringBuffer data){
        StringBuffer CompressedBuffer = new StringBuffer("");
        PyObject obj = Utils.startPython(context, Algorithm.ren_length);
        String compressed = obj.callAttr("encode", data.toString()).toString();
        CompressedBuffer.append(compressed);
        return CompressedBuffer;
    }
    public static void Compress(AppCompatActivity context, File fileToCompress) throws Exception{
        StringBuffer data_file = Algorithm.readFile(fileToCompress);

        StringBuffer str_string_freq=compressText(context, data_file);
        StringBuffer encoded_str_string_freq=new StringBuffer(padIfNot8(8,"01"));
        encoded_str_string_freq.append(convertString(str_string_freq.toString()));

        String path_compressed = fileToCompress.getParent() + "/" + fileToCompress.getName().split("\\.")[0] + "/";
        new File(path_compressed).mkdir();//this code just to create folders for last code


        SaveCompressed(encoded_str_string_freq, path_compressed + "compressedFile."+ren_length+"_"+fileToCompress.getName().split("\\.")[1], fileToCompress.getPath());
        setTextAfterCompress(context, encoded_str_string_freq.toString().substring(encoded_str_string_freq.toString().indexOf('1')+1), data_file.toString(), null,false);

    }
    private static String convertString(String s){
        String new_s="";
        for(int i=0;i<s.length();i++){
            new_s+=padIfNot8(8,Integer.toBinaryString((int) s.charAt(i)));
        }
        return new_s;
    }
    private static String getBack(String s){
        String new_s="";
        for(int i=0;i<s.length()/8;i++){
            String segment=s.substring(i*8,i*8+8);
            new_s+=((char) Integer.parseInt(segment, 2))+"";
        }
        return new_s;
    }


    private static StringBuffer decompressText(AppCompatActivity currentActivity,  String str_string_freq) {
        StringBuffer CompressedBuffer = new StringBuffer("");
        PyObject obj = Utils.startPython(currentActivity, Algorithm.ren_length);
        String compressed = obj.callAttr("decode", str_string_freq).toString();
        CompressedBuffer.append(compressed);
        return CompressedBuffer;
    }
    public static void DeCompress(AppCompatActivity context, File fileTodeCompress) {
        String org_exension = fileTodeCompress.getName().split("\\.")[1].split("_")[1];

        String encoded_str_string_freq = Algorithm.OpenCompessed(fileTodeCompress.getPath());
        StringBuffer str_string_freq=new StringBuffer(getBack(encoded_str_string_freq));

        StringBuffer decompressed_data = decompressText(context, str_string_freq.toString());
        Algorithm.saveDecompressed(decompressed_data, fileTodeCompress.getParent() + "/" + "decompressed." + org_exension, fileTodeCompress.getPath());

    }

}
