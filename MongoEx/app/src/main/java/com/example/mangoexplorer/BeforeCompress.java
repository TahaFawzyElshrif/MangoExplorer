package com.example.mangoexplorer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class BeforeCompress extends AppCompatActivity {
    Button Compress;
    TextView name;
    CheckBox huff,golamb,lzw,arth,run;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_compress);
        Compress=findViewById(R.id.compress);
        name=findViewById(R.id.name);
        huff=findViewById(R.id.huff);
        golamb=findViewById(R.id.Golamb);
        lzw=findViewById(R.id.lzw);
        arth=findViewById(R.id.arth);
        run=findViewById(R.id.run);
        Utils.showDialog(BeforeCompress.this, "selected_file");

        SetCompress();
    }



    private void SetCompress() {
        Compress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File selected_file=new File(name.getText().toString());
                Utils.showDialog(BeforeCompress.this, selected_file.exists()+"");
            }
        });

    }
    private String[] GetCheckedAlgorithms(){
        return null;
    }
}