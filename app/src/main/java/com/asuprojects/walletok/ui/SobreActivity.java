package com.asuprojects.walletok.ui;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String backupPath = preferences.getString(getString(R.string.backup_path), "");
        if(!backupPath.isEmpty() || !backupPath.equals("")){
            caminhoBackup.setText(backupPath);
        }

        String exportedPath = preferences.getString(getString(R.string.export_file_path), "");
        if(!exportedPath.isEmpty() || !exportedPath.equals("")){
            caminhoExportados.setText(exportedPath);
        }


    }
}
