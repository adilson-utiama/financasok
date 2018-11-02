package com.asuprojects.walletok.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.asuprojects.walletok.MainActivity;
import com.asuprojects.walletok.R;
import com.asuprojects.walletok.model.enums.Extensao;
import com.asuprojects.walletok.service.FileService;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.io.IOException;

public class ExportarArquivoActivity extends AppCompatActivity {

    private TextView nomeArquivo;
    private RadioGroup formatos;
    private AppCompatButton btnSelPasta;
    private TextView caminhoArquivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exportar_arquivo);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Exportar Arquivo");
        }

        nomeArquivo = findViewById(R.id.nome_arquivo);
        formatos = findViewById(R.id.radioGroup);
        caminhoArquivo = findViewById(R.id.tx_caminho_arquivo);

        btnSelPasta = findViewById(R.id.btn_selecao_pasta);
        btnSelPasta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChooserDialog(ExportarArquivoActivity.this)
                        .withFilter(true, false)
                        .withFileIconsRes(false, R.drawable.ic_file, R.mipmap.ic_pasta)
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String caminho, File file) {
                                caminhoArquivo.setText(caminho);
                            }
                        }).build().show();
            }
        });

        AppCompatButton btnExportar = findViewById(R.id.btn_exportar);
        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Extensao ext = null;
                try {
                    String arquivo = nomeArquivo.getText().toString().trim();
                    String caminho = caminhoArquivo.getText().toString().trim();

                    if(arquivo.isEmpty() || arquivo.equals("")){
                        Toast.makeText(ExportarArquivoActivity.this, "Necessario informar nome do arquivo", Toast.LENGTH_SHORT).show();
                    } else if(caminho.isEmpty() || caminho.equals("")){
                        Toast.makeText(ExportarArquivoActivity.this, "Necessario informar pasta de Destino", Toast.LENGTH_SHORT).show();
                    } else {
                        int checkedRadioButtonId = formatos.getCheckedRadioButtonId();
                        if(checkedRadioButtonId == R.id.csv_format){ ext = Extensao.CSV; }
                        if(checkedRadioButtonId == R.id.json_format){ ext = Extensao.JSON; }
                        new FileService(ExportarArquivoActivity.this).exportarDados(caminho, arquivo, ext);
                        Toast.makeText(ExportarArquivoActivity.this, getString(R.string.msg_sucesso_exportar), Toast.LENGTH_LONG).show();
                        finish();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
