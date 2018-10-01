package com.asuprojects.walletok.util;

import android.content.Context;
import android.os.Environment;

import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.database.TabelaDespesa;
import com.asuprojects.walletok.database.TabelaReceita;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Receita;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class FilesUtil {

    private String separador = ",";
    private String extensao = ".csv";

    public void gravarBackupDespesas(String nome, DespesaDAO dao) throws IOException {

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

    public void gravarBackupReceitas(String nome, ReceitaDAO dao) throws IOException {

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
