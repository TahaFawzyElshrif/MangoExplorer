package com.example.mangoexplorer.Compress_utils;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.example.mangoexplorer.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class arth  extends Algorithm implements Serializable {
    private static  String compressText(AppCompatActivity currentActivity, StringBuffer text) {
        PyObject obj = Utils.startPython(currentActivity, Algorithm.arthimetric);
        String compressed = obj.callAttr("ArithmeticCompress", text.toString()).toString();
        return compressed;
    }
    private static StringBuffer decompressText(AppCompatActivity currentActivity,  String code,StringBuffer prob_dic,String length) {
        StringBuffer CompressedBuffer = new StringBuffer("");
        PyObject obj = Utils.startPython(currentActivity, Algorithm.arthimetric);
        String compressed = obj.callAttr("ArithmeticDecompress", code,prob_dic.toString(),length).toString();
        CompressedBuffer.append(compressed);
        return CompressedBuffer;
    }
    public static void writeCompressUtilityJSON(AppCompatActivity currentActivity, StringBuffer text,String compressed_text,String main_extension, String filePath) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();

        PyObject obj = Utils.startPython(currentActivity,"general");
        String prob_dic = obj.callAttr("calc_probability", text.toString()).toString();
        jsonNode.put("code_table_string", prob_dic);

        jsonNode.put("Text_length", text.toString().length());
        jsonNode.put("Compress", compressed_text);

        jsonNode.put("extension", main_extension);
        jsonNode.put("Arthimetric_file", "5%yyUi");// random value to compare to know it's arthimetric file

        File file = new File(filePath);
        objectMapper.writeValue(file, jsonNode);
        file.setWritable(false);//to save from unexpected change
        file.setReadable(false);//for better security

    }
    public static Map<String, StringBuffer> readCompressUtilityJSON(String filePath) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(new File(filePath));
        Map<String, StringBuffer> data = new HashMap<>();

        StringBuffer code_table_string = new StringBuffer(jsonNode.get("code_table_string").asText());
        data.put("code_table_string", code_table_string);
        String extension = jsonNode.get("extension").asText();
        data.put("extension", new StringBuffer(extension));
        String Compress = jsonNode.get("Compress").asText();
        data.put("Compress", new StringBuffer(Compress));
        String Text_length = jsonNode.get("Text_length").asText();
        data.put("Text_length", new StringBuffer(Text_length));
        data.put("Arthimetric_file", new StringBuffer("5%yyUi"));// random value to compare to know it's arthimetric file


        return data;
    }
    public static void Compress(AppCompatActivity context, File fileToCompress) throws Exception {
        StringBuffer data_file = Algorithm.readFile(fileToCompress);
        String compressed_data=compressText(context,data_file);

        String path_compressed = fileToCompress.getParent() + "/" + fileToCompress.getName().split("\\.")[0] + "/";
        new File(path_compressed).mkdir();//this code just to create folders for last code

        writeCompressUtilityJSON(context, data_file,compressed_data,fileToCompress.getName().split("\\.")[1], path_compressed + "compressed.json");
        Utils.showDialog(context, "INF file has been created ,note removing this file may affect decompressing later");
        setTextAfterCompress(context, compressed_data, data_file.toString(), null,true);

    }
    public static void DeCompress(AppCompatActivity context, File fileTodeCompress) throws Exception {
        String path_file = fileTodeCompress.getParent();
        Map<String, StringBuffer> compress_data=readCompressUtilityJSON(fileTodeCompress.getPath());
        StringBuffer code_table_string=compress_data.get("code_table_string");
        String extension=compress_data.get("extension").toString();
        String Compress=compress_data.get("Compress").toString();
        String Text_length=compress_data.get("Text_length").toString();
        StringBuffer decompressed_data=decompressText(context, Compress,code_table_string,Text_length);
        Algorithm.saveDecompressed(decompressed_data, path_file+ "/" + "decompressed." + extension, path_file);

    }

}
