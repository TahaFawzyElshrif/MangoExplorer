package com.example.mangoexplorer.Compress_utils;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.example.mangoexplorer.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

public class golamb  extends Algorithm implements Serializable {
    private static  StringBuffer compressText(AppCompatActivity currentActivity, StringBuffer text,int M) {
        StringBuffer CompressedBuffer = new StringBuffer("01");

        PyObject obj = Utils.startPython(currentActivity, Algorithm.golamb);
        String compressed = obj.callAttr("encode", text.toString(),M).toString();
        CompressedBuffer.append(compressed);
        return CompressedBuffer;
    }

    private static boolean isAcceptableNumber(StringBuffer text) {
        try {
            Long d = Long.parseLong(text.toString());
            if (d<0){
                Log.e("error in number","can't cast negative");
                return false;

            }
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }
    private static StringBuffer decompressText(AppCompatActivity currentActivity,  String code,int m) {
        StringBuffer CompressedBuffer = new StringBuffer("");
        PyObject obj = Utils.startPython(currentActivity, Algorithm.golamb);
        String compressed = obj.callAttr("decode", code,m).toString();
        CompressedBuffer.append(compressed);
        return CompressedBuffer;
    }
    public static void Compress(AppCompatActivity context, File fileToCompress) throws Exception{
        StringBuffer data_file = Algorithm.readFile(fileToCompress);
        if (!isAcceptableNumber(data_file)){
            Utils.showDialog(context,"For Golamb must be numeric text");
            return ;
        }
        int M= (int) Math.round(Math.sqrt(Integer.parseInt(data_file.toString())));
        StringBuffer code=compressText(context, data_file,M);

        String path_compressed = fileToCompress.getParent() + "/" + fileToCompress.getName().split("\\.")[0] + "/";
        new File(path_compressed).mkdir();//this code just to create folders for last code


        SaveCompressed(code, path_compressed + "compressedFile."+golamb+"_"+M+"_"+fileToCompress.getName().split("\\.")[1], fileToCompress.getPath());
        setTextAfterCompress(context, code.toString(), data_file.toString(), null,false);

    }

    public static void DeCompress(AppCompatActivity context, File fileTodeCompress) {
        int M = Integer.parseInt(fileTodeCompress.getName().split("\\.")[1].split("_")[1]);
        String org_exension = fileTodeCompress.getName().split("\\.")[1].split("_")[2];

        String compressdata = Algorithm.OpenCompessed(fileTodeCompress.getPath());
        Utils.showDialog(context,compressdata);
        StringBuffer decompressed_data = decompressText(context, compressdata,M);
        Algorithm.saveDecompressed(decompressed_data, fileTodeCompress.getParent() + "/" + "decompressed." + org_exension, fileTodeCompress.getPath());

    }
}
