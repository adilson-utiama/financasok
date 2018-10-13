package com.asuprojects.walletok.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.asuprojects.walletok.R;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.sobre);
        }

        TextView caminhoBackup = findViewById(R.id.textView_diretorio_backup);
        TextView caminhoExportados = findViewById(R.id.textView_diretorio_exportados);


    }
}
