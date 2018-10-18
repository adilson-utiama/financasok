package com.asuprojects.walletok.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.database.TabelaDespesa;
import com.asuprojects.walletok.database.TabelaReceita;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.model.enums.Extensao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileService {

    private DBService service;

    private String separador = ",";
    private String filename = "backup.bkp";
    private String dir = "/Backup_app/";
    private final String despesa = "Despesas";
    private final String receita = "Receitas";

    public String realizarBackup(Context context) throws IOException {
        service = new DBService(context);
        List<Despesa> despesas = service.getDespesaDAO().getAll();
        List<Receita> receitas = service.getReceitaDAO().listAll();
        Map<String, List<?>> mapa = new HashMap<>();
        mapa.put(despesa, despesas);
        mapa.put(receita, receitas);

        File file = null;

        if(isExternalStorageWritable()){
            File diretorio = criaDiretorioBackup();
            salvaCaminhoBackupEmPreferences(context, diretorio);

            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS) + dir, filename);
            ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(file));
            saida.writeObject(mapa);
            saida.close();

        } else {
            Toast.makeText(context, context.getString(R.string.msg_falha_backup), Toast.LENGTH_SHORT).show();
        }

        service.close();

        return file.exists() ? file.getAbsolutePath() : context.getString(R.string.msg_caminho_arquivo_desconhecido);
    }

    public boolean restaurarDados(Context context, Uri dataUri){
        service = new DBService(context);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(dataUri);
            ObjectInputStream entrada = new ObjectInputStream(inputStream);
            Map<String, List<?>> dados = (Map<String, List<?>>) entrada.readObject();
            salvaDespesasNoBanco(dados);
            salvaReceitasNoBanco(dados);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        service.close();

        return true;
    }

    public void exportarDados(String fileName, Extensao extensao, Context context) throws IOException {

        service = new DBService(context);
        List<Despesa> despesas = service.getDespesaDAO().getAll();
        List<Receita> receitas = service.getReceitaDAO().listAll();

        if(isExternalStorageWritable()){

            fileName += extensao.getExtensao();
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), fileName);

            salvaCaminhoExportadoEmPreferences(context, file);

            if(extensao.equals(Extensao.CSV)){
                salvaEmCSV(file, despesas, receitas);
            }
            if(extensao.equals(Extensao.JSON)){
                salvaEmJSON(file, despesas, receitas);
            }

        } else {
            throw new RuntimeException(context.getString(R.string.msg_falha_exportar_arquivo));
        }

        service.close();
    }

    @NonNull
    private void salvaEmCSV(File file, List<Despesa> despesas, List<Receita> receitas) throws IOException {

        FileOutputStream outputStream = new FileOutputStream(file);
        PrintStream ps = new PrintStream(outputStream);
        ps.println(colunasDespesa());
        for (Despesa d : despesas){
            ps.println(despesaLinha(d));
        }
        ps.println(colunasReceita());
        for (Receita r : receitas) {
            ps.println(receitaLinha(r));
        }
        outputStream.close();
        ps.close();
    }

    private void salvaEmJSON(File file, List<Despesa> despesas, List<Receita> receitas) throws IOException {
        Map<String, List<?>> mapa = new HashMap<>();
        mapa.put(despesa, despesas);
        mapa.put(receita, receitas);

        PrintStream ps = new PrintStream(new FileOutputStream(file));

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String dados = gson.toJson(mapa);

        ps.println(dados);
        ps.close();
    }


    private void salvaReceitasNoBanco(Map<String, List<?>> dados) {
        List<Receita> receitas = (List<Receita>) dados.get(receita);
        for(Receita r : receitas){
            long id = r.get_id();
            Receita receita = service.getReceitaDAO().findOne(id);
            if(receita != null){
                if(r.get_id() != receita.get_id() && !r.getData().equals(receita.getData())){
                    r.set_id(0);
                    service.getReceitaDAO().insertOrUpdate(r);
                }
            } else {
                service.getReceitaDAO().insertOrUpdate(r);
            }
        }
    }

    private void salvaDespesasNoBanco(Map<String, List<?>> dados) {
        List<Despesa> despesas = (List<Despesa>) dados.get(despesa);
        for(Despesa d : despesas){
            long id = d.get_id();
            Despesa despesa = service.getDespesaDAO().findOne(id);
            if(despesa != null){
                if(d.get_id() != despesa.get_id() && !d.getData().equals(despesa.getData())){
                    d.set_id(0);
                    service.getDespesaDAO().insertOrUpdate(d);
                }
            } else {
                service.getDespesaDAO().insertOrUpdate(d);
            }

        }
    }

    private void salvaCaminhoBackupEmPreferences(Context context, File diretorio) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.backup_path), diretorio.getAbsolutePath());
        editor.apply();
    }

    private void salvaCaminhoExportadoEmPreferences(Context context, File file) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(context.getString(R.string.export_file_path), file.getAbsolutePath());
        editor.apply();
    }


    @NonNull
    private File criaDiretorioBackup() {
        File diretorio = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + dir);
        if(!diretorio.exists()){
            diretorio.mkdirs();
        }
        return diretorio;
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
        builder.append("DESPESAS").append(separador)
                .append(TabelaDespesa.COLUNA_DATA).append(separador)
                .append(TabelaDespesa.COLUNA_CATEGORIA).append(separador)
                .append(TabelaDespesa.COLUNA_DESCRICAO).append(separador)
                .append(TabelaDespesa.COLUNA_PAGAMENTO).append(separador)
                .append(TabelaDespesa.COLUNA_VALOR);
        return builder.toString();
    }

    private String colunasReceita() {
        StringBuilder builder = new StringBuilder();
        builder.append("RECEITAS").append(separador)
                .append(TabelaReceita.COLUNA_DATA).append(separador)
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
