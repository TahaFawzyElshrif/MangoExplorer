package com.example.mangoexplorer.Compress_utils;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.example.mangoexplorer.Utils;
import com.example.mangoexplorer.explorer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class huff extends Algorithm implements Serializable {
    public StringBuffer code;
    public String original_extension;

    public huff(StringBuffer code) {
        this.code = code;
    }

    public huff(StringBuffer code, String original_extension) {
        this.code = code;
        this.original_extension = original_extension;
    }

    public huff() {
    }

    private static Map<String, StringBuffer> compressText(AppCompatActivity currentActivity, StringBuffer text) {
        StringBuffer CompressedBuffer = new StringBuffer("");
        PyObject obj = Utils.startPython(currentActivity, Algorithm.huffman);
        String compressed = obj.callAttr("encode", text.toString()).toString();
        CompressedBuffer.append(compressed);

        StringBuffer alphacode = new StringBuffer(obj.callAttr("getAlgorithmCode", text.toString()).toString());

        Map<String, StringBuffer> compression = new HashMap<>();
        compression.put("Code", CompressedBuffer);
        compression.put("alpha_code", alphacode);
        return compression;
    }

    private static StringBuffer decompressText(AppCompatActivity currentActivity, StringBuffer alphaCode, String code) {
        StringBuffer CompressedBuffer = new StringBuffer("");
        PyObject obj = Utils.startPython(currentActivity, Algorithm.huffman);
        String compressed = obj.callAttr("decode", code, alphaCode.toString()).toString();
        CompressedBuffer.append(compressed);
        return CompressedBuffer;
    }



    public static void writeCompressUtilityJSON(StringBuffer alpha_code, String main_extension, String filePath) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("code_table_string", alpha_code.toString());
        jsonNode.put("extension", main_extension);

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

        return data;
    }

    public static void Compress(AppCompatActivity context, File fileToCompress) throws Exception {
        StringBuffer data_file = Algorithm.readFile(fileToCompress);

        StringBuffer compressed = new StringBuffer("01");//--->padding with 1 ,to make decompress possible when code start by 0

        Map<String, StringBuffer> compression = huff.compressText(context, data_file);

        StringBuffer compression_data = compression.get("Code");
        StringBuffer alpha_code = compression.get("alpha_code");
        compressed.append(compression_data);

        String path_compressed = fileToCompress.getParent() + "/" + fileToCompress.getName().split("\\.")[0] + "/";
        new File(path_compressed).mkdir();//this code just to create folders for last code


        SaveCompressed(compressed, path_compressed + "compressedFile."+huffman, fileToCompress.getPath());
        huff.writeCompressUtilityJSON(alpha_code, fileToCompress.getName().split("\\.")[1], path_compressed + "inf_huff.json");

        Utils.showDialog(context, "INF file has been created ,note removing this file may affect decompressing later");
        setTextAfterCompress(context, compression_data.toString(), data_file.toString(), alpha_code.toString());


    }

    public static void DeCompress(AppCompatActivity context, File fileTodeCompress) throws Exception {
        String path_file = fileTodeCompress.getParent();
        String name_ext_selectedfile = fileTodeCompress.getName();
        String exension = name_ext_selectedfile.split("\\.")[1];
        String compressdata = Algorithm.OpenCompessed(path_file + "/compressedFile." + exension);
        Map<String, StringBuffer> map=readCompressUtilityJSON(path_file + "/inf_huff.json");//.get("code_table_string")
        StringBuffer decompressed_data = huff.decompressText(context, map.get("code_table_string"), compressdata);
        Algorithm.saveDecompressed(decompressed_data, fileTodeCompress.getParent() + "/" + "decompressed." + map.get("extension"), fileTodeCompress.getPath());

    }
}
