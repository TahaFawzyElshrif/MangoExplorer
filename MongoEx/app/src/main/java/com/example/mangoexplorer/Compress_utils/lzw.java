package com.example.mangoexplorer.Compress_utils;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.example.mangoexplorer.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

public class lzw extends Algorithm implements Serializable {
    public static StringBuffer getCodeLZW(int[] values) {// each int is 4byte
        StringBuffer code = new StringBuffer();
        code.append(Algorithm.padIfNot8(32,"01"));//for safety padding
        for (int i = 0; i < values.length; i++) {
            code.append( Algorithm.padIfNot8(32, Integer.toBinaryString((values[i] >= 0 ? +(values[i]) : (values[i] + 256)))));
        }
        return code;
    }
    public static int[] getArrayBack(String code){
    //    code=code.substring(code.indexOf('1')+1);;//remove padding
        int[] numbers=new int[code.length()/32];
        for (int i=0;i<numbers.length;i++){
            String bits_int=code.substring(i*32, i*32+32);
            numbers[i]=Integer.parseInt(bits_int, 2);
        }
        return numbers;
    }

    private static int[] CodeStringToArray(String text) {
        String[] parts = text.substring(1, text.length() - 1).split("\\,");
        int[] numeric_parts = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].strip();
            numeric_parts[i] = Integer.parseInt(parts[i]);
        }

        return numeric_parts;
    }
    public static  StringBuffer compressText(AppCompatActivity currentActivity, StringBuffer text) {
        StringBuffer CompressedBuffer = new StringBuffer("");
        PyObject obj = Utils.startPython(currentActivity, Algorithm.lzw);
        String compressed = obj.callAttr("encode", text.toString()).toString();
        CompressedBuffer.append(compressed);
        return CompressedBuffer;
    }
    private static StringBuffer decompressText(AppCompatActivity currentActivity,  String code) {
        StringBuffer CompressedBuffer = new StringBuffer("");
        PyObject obj = Utils.startPython(currentActivity, Algorithm.lzw);
        String compressed = obj.callAttr("decode", code).toString();
        CompressedBuffer.append(compressed);
        return CompressedBuffer;
    }

    public static void Compress(AppCompatActivity context, File fileToCompress) throws Exception{
        StringBuffer data_file = Algorithm.readFile(fileToCompress);

        StringBuffer encoded=compressText(context, data_file);

        String path_compressed = fileToCompress.getParent() + "/" + fileToCompress.getName().split("\\.")[0] + "/";
        new File(path_compressed).mkdir();//this code just to create folders for last code

        int[] encoded_list_integers=CodeStringToArray(encoded.toString());
        StringBuffer code=getCodeLZW(encoded_list_integers);//already padded
        SaveCompressed(code, path_compressed + "compressedFile."+lzw+"_"+fileToCompress.getName().split("\\.")[1], fileToCompress.getPath());
        setTextAfterCompress(context, code.toString().substring(code.toString().indexOf('1')+1), data_file.toString(), null,false);

    }

    private static String Array2PythonList(int[] array){
        return Arrays.toString(array).replace("{","[").replace("}","]");
    }
    public static void DeCompress(AppCompatActivity context, File fileTodeCompress) {
        String org_exension = fileTodeCompress.getName().split("\\.")[1].split("_")[1];

        String compressdata = Algorithm.OpenCompessed(fileTodeCompress.getPath());

        int[] code_numbers=getArrayBack(compressdata);
        String python_code_numbers=Array2PythonList(code_numbers);
        StringBuffer decompressed_data = decompressText(context, python_code_numbers);
        Algorithm.saveDecompressed(decompressed_data, fileTodeCompress.getParent() + "/" + "decompressed." + org_exension, fileTodeCompress.getPath());

    }
}
