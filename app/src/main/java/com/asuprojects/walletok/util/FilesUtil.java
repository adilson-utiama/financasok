package com.asuprojects.walletok.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.database.TabelaDespesa;
import com.asuprojects.walletok.database.TabelaReceita;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Receita;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

public class FilesUtil {

    private String separador = ",";
    private String extensao = ".csv";

    public void importarDados(Context context, Uri dataUri){

        //TODO Fazer a leitura do arquivo e gravar dados no banco
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(dataUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String linha = reader.readLine();
            Log.i("READER", "importarDados: " + linha);
            while(linha != null) {
                linha = reader.readLine();
                if(linha != null) {
                    String[] valores = linha.split(",");
                    for(int i = 0; i < valores.length; i++){
                        Log.i("READER", "importarDados: " + valores[i]);
                        //TODO preencher objeto com os dados
                    }
                }

            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


        //TODO conferir se dados ja exstem no banco

    }

    public void exportarDados(Context context) throws IOException {

        gravarBackupDespesas("despesas", context);
        gravarBackupReceitas("receitas", context);


        //TODO Unificar desepsas, receitas no mesmo arquivo .csv

        //TODO Determinar conteudo no arquivo .csv
    }

    public void gravarBackupDespesas(String nome, Context context) throws IOException {

        DespesaDAO dao = new DespesaDAO(context);

        if(isExternalStorageWritable()){
            List<Despesa> despesas = dao.getAll();
            String nomeComExtensao = nome + extensao;

            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), nomeComExtensao);

            FileOutputStream outputStream = new FileOutputStream(file);
            PrintStream ps = new PrintStream(outputStream);
            ps.println(colunasDespesa());

            for (Despesa d : despesas){
                ps.println(despesaLinha(d));
            }
            outputStream.close();
            ps.close();
        } else {
            throw new RuntimeException("Falha ao realizar backup");
        }


    }

    public void gravarBackupReceitas(String nome, Context context) throws IOException {

        ReceitaDAO dao = new ReceitaDAO(context);
        if(isExternalStorageWritable()){
            List<Receita> receitas = dao.listAll();
            String nomeComExtensao = nome + extensao;

            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), nomeComExtensao);

            FileOutputStream outputStream = new FileOutputStream(file);
            PrintStream ps = new PrintStream(outputStream);
            ps.println(colunasReceita());

            for (Receita r : receitas){
                ps.println(receitaLinha(r));
            }
            outputStream.close();
            ps.close();
        } else {
            throw new RuntimeException("falha ao realizar backup");
        }


    }

    private String receitaLinha(Receita r) {
        StringBuilder builder = new StringBuilder();
        builder.append(r.getDataFormatada()).append(separador)
                .append(r.getCategoriaReceita()).append(separador)
                .append(r.getDescricao()).append(separador)
                .append(r.getValor().doubleValue());
        return builder.toString();
    }

    private String colunasDespesa() {
        StringBuilder builder = new StringBuilder();
        builder.append(TabelaDespesa.COLUNA_DATA).append(separador)
                .append(TabelaDespesa.COLUNA_CATEGORIA).append(separador)
                .append(TabelaDespesa.COLUNA_DESCRICAO).append(separador)
                .append(TabelaDespesa.COLUNA_PAGAMENTO).append(separador)
                .append(TabelaDespesa.COLUNA_VALOR);
        return builder.toString();
    }

    private String colunasReceita() {
        StringBuilder builder = new StringBuilder();
        builder.append(TabelaReceita.COLUNA_DATA).append(separador)
                .append(TabelaReceita.COLUNA_CATEGORIA).append(separador)
                .append(TabelaReceita.COLUNA_DESCRICAO).append(separador)
                .append(TabelaReceita.COLUNA_VALOR);
        return builder.toString();
    }

    private String despesaLinha(Despesa d){
        StringBuilder builder = new StringBuilder();
        builder.append(d.getDataFormatada()).append(separador)
                .append(d.getCategoriaDespesa()).append(separador)
                .append(d.getDescricao()).append(separador)
                .append(d.getPagamento()).append(separador)
                .append(d.getValor().doubleValue());
        return builder.toString();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
