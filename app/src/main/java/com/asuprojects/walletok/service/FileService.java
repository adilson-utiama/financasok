package com.asuprojects.walletok.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

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

    public String realizarBackup(Context context) throws IOException {
        service = new DBService(context);
        List<Despesa> despesas = service.getDespesaDAO().getAll();
        List<Receita> receitas = service.getReceitaDAO().listAll();
        Map<String, List<?>> mapa = new HashMap<>();
        mapa.put("Despesas", despesas);
        mapa.put("Receitas", receitas);

        File file = null;

        if(isExternalStorageWritable()){
            File diretorio = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS) + dir);
            if(!diretorio.exists()){
                diretorio.mkdirs();
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(context.getString(R.string.backup_path), diretorio.getAbsolutePath());
            editor.apply();

            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS) + dir, filename);

            ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(file));
            saida.writeObject(mapa);
            saida.close();

        } else {
            throw new RuntimeException("Não foi possivel realizar o Backup");
        }

        service.close();

        return file.exists() ? file.getAbsolutePath() : "Caminho do arquivo desconhecido.";
    }

    public boolean restaurarDados(Context context, Uri dataUri){
        service = new DBService(context);

        try {

            InputStream inputStream = context.getContentResolver().openInputStream(dataUri);

            ObjectInputStream entrada = new ObjectInputStream(inputStream);
            Map<String, List<?>> dados = (Map<String, List<?>>) entrada.readObject();

            List<Despesa> despesas = (List<Despesa>) dados.get("Despesas");
            for(Despesa d : despesas){
                long id = d.get_id();
                Despesa despesa = service.getDespesaDAO().findOne(id);
                if(despesa != null){
                    if(d.get_id() != despesa.get_id() && !d.getData().equals(despesa.getData())){
                        d.set_id(0);
                        service.getDespesaDAO().insertOrUpdate(d);
                        Log.i("PERSIST", "restaurarDados: despesa restaurada: " + d.toString());
                    }
                } else {
                    service.getDespesaDAO().insertOrUpdate(d);
                }

            }

            List<Receita> receitas = (List<Receita>) dados.get("Receitas");
            for(Receita r : receitas){
                long id = r.get_id();
                Receita receita = service.getReceitaDAO().findOne(id);
                if(receita != null){
                    if(r.get_id() != receita.get_id() && !r.getData().equals(receita.getData())){
                        r.set_id(0);
                        service.getReceitaDAO().insertOrUpdate(r);
                        Log.i("PERSIST", "restaurarDados: receita restaurada: " + r.toString());
                    }
                } else {
                    service.getReceitaDAO().insertOrUpdate(r);
                }
            }


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

            if(extensao.equals(Extensao.CSV)){

                fileName += extensao.getExtensao();

                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), fileName);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(context.getString(R.string.export_file_path), file.getAbsolutePath());
                editor.apply();

                Log.i("TESTE", "exportarDados: " + file.getAbsolutePath());

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

            if(extensao.equals(Extensao.JSON)){
                Map<String, List<?>> mapa = new HashMap<>();
                mapa.put("Despesas", despesas);
                mapa.put("Receitas", receitas);

                fileName += extensao.getExtensao();
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), fileName);
                FileOutputStream outputStream = new FileOutputStream(file);
                PrintStream ps = new PrintStream(outputStream);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String dados = gson.toJson(mapa);

                ps.println(dados);

                outputStream.close();
                ps.close();

            }


        } else {
            throw new RuntimeException("Falha ao exportar os dados");
        }

        service.close();
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
