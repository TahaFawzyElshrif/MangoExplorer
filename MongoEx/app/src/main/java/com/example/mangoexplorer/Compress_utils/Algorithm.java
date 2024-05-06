package com.example.mangoexplorer.Compress_utils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.example.mangoexplorer.R;
import com.example.mangoexplorer.Utils;
import com.example.mangoexplorer.explorer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class Algorithm {
    public final static String huffman = "huff";
    public final static String lzw = "LZW";
    public final static String ren_length = "run";
    public final static String golamb = "golamb";
    public final static String arthimetric = "arth";

    public static final String[] acceptable_txt_extensions = new String[]{"txt", "csv", "json"};

    public static boolean isAcceptable_producied(String extension){
        if (Utils.isContain(new String[]{huffman,arthimetric, ren_length, golamb},extension)){
            return true;
        }else if(extension.startsWith(lzw)){
            return true;
        }else if(extension.startsWith(golamb)){
            return true;
        }else if(extension.startsWith(ren_length)){
            return true;
        }
        return false;
    }
    public static StringBuffer readFile(File file) {
        StringBuffer text = new StringBuffer("");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e) {
            Log.e("Error opening file ", e.toString());
        }
        return text;
    }

    public static void SaveCompressed(StringBuffer Text, String path,String maim_file) throws Exception{
            saveStringToBinaryFile(Text.toString(), path,maim_file);

    }

    public static String padIfNot8(int next_full_eights,String s){
        int current=s.length();
        int remain=next_full_eights-current;
        String new_s=s;
        for(int i=0;i<remain;i++){
            new_s = String.join("", "0", new_s);

        }
        return new_s;
    }
    private static void saveStringToBinaryFile(String data, String filename ,String maim_file) throws Exception {
//code may produce error if one bit ,in fact this won't happen (especially with padding) so no problem
        int next_full_n_byte = (int) Math.ceil(data.length() * 1.0 / 8);
        int next_full_eights = next_full_n_byte * 8;

        String data_padded=padIfNot8(next_full_eights,data);


        byte[] byteArray = new byte[next_full_n_byte];
        for (int i = 0; i < byteArray.length; i++) {
            String byteString = data_padded.substring(i * 8, (i + 1) * 8);
            byteArray[i] = (byte) Integer.parseInt(byteString, 2);

        }


        FileOutputStream fileOutputStream = new FileOutputStream(filename);
        fileOutputStream.write(byteArray);
        copyFileInfo(maim_file ,filename);
        fileOutputStream.close();

    }

    public static void copyFileInfo(String from ,String to) {
        File old_file=new File(from);
        File new_file=new File(to);
        new_file.setExecutable(old_file.canExecute());
        new_file.setReadable(old_file.canRead());
        new_file.setWritable(old_file.canWrite());
        new_file.setLastModified(old_file.lastModified());

    }


    public static String OpenCompessed(String file_name_path) {
        try {
            File file = new File(file_name_path);
            long fileSize = file.length();

            FileInputStream fileinputStream = new FileInputStream(file_name_path);

            byte[] byteArray = new byte[(int) fileSize];
            fileinputStream.read(byteArray);

            String original = "";
            for (int i =0 ;i<byteArray.length;i++){
                original+=padIfNot8(8,Integer.toBinaryString(byteArray[i]>=0?byteArray[i]:byteArray[i]+256));

            }        //padding not necessary in decoded ,as already will be removed

            return original.substring(original.indexOf('1')+1);
        } catch (Exception ex) {
            Log.e("Error in Opening ", ex.toString());
        }
        return "";
    }


    public static void saveDecompressed(StringBuffer text,String path,String path_comp){

        try {
            FileOutputStream fos = new FileOutputStream(path);
            byte[] bytes = text.toString().getBytes();
            fos.write(bytes);
            copyFileInfo(path_comp,path);
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }








    public static void writeCompressUtilityJSON(StringBuffer[] compress_info,String Algorithm,String filePath) throws Exception{
        switch(Algorithm){
            case com.example.mangoexplorer.Compress_utils.Algorithm.huffman:{
                //huff obj=new huff(compress_info[0],compress_info[1].toString());
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode jsonNode = objectMapper.createObjectNode();
                jsonNode.put("code_table_string", compress_info[0].toString());
                jsonNode.put("extension", compress_info[1].toString());

                File file = new File( filePath);
                objectMapper.writeValue(file, jsonNode);


                break;
            }
        }
    }

    public static huff readCompressUtility(String Algorithm,String filePath) throws Exception{
        switch(Algorithm){
            case com.example.mangoexplorer.Compress_utils.Algorithm.huffman:{
                FileInputStream fileInputStream = new FileInputStream(filePath);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                huff obj=(huff)objectInputStream.readObject();
                objectInputStream.close();
                fileInputStream.close();
                return obj;
            }
        }
        throw new Exception("No algorithm selected");

    }
    protected static void setTextAfterCompress(AppCompatActivity context,String data,String original_data,String alpha_code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View customLayout = inflater.inflate(R.layout.activity_after, null);
        TextView label = customLayout.findViewById(R.id.label);
        TextView bits = customLayout.findViewById(R.id.bits);

        String text_info="";//make seperate method
        PyObject obj = Utils.startPython(context, "general");
        text_info +="\n Entropy Of Data :\n"+ obj.callAttr("calc_entropy",original_data).toString();
        text_info +="\n\n Probability of each char :\n"+ obj.callAttr("calc_probability",original_data).toString();
        text_info +="\n\n Compression ratio :\n"+ obj.callAttr("calc_CR_DATA",original_data,data).toString();

        if(  alpha_code!=null) {
            text_info += "\n\n Average Length :\n" + obj.callAttr("calc_avg_length", alpha_code, original_data).toString();
            text_info += "\n\n Efficency :\n" + obj.callAttr("calc_efficiency", alpha_code, original_data).toString();
        }



        label.setText(text_info);
        bits.setText(data.toString());
        builder.setView(customLayout);
        builder.create().show();
        builder.setCancelable(true);
        builder.setView(customLayout);

    }
}

