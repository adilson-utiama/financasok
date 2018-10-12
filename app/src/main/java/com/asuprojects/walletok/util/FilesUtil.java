package com.asuprojects.walletok.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.asuprojects.walletok.MainActivity;
import com.asuprojects.walletok.database.TabelaDespesa;
import com.asuprojects.walletok.database.TabelaReceita;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.model.enums.Extensao;
import com.asuprojects.walletok.service.DBService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilesUtil {

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

            file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS) + dir, filename);
            Log.i("FILE_SAVED", "realizarBackup: " + file.getAbsolutePath());

            ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(file));
            saida.writeObject(mapa);
            saida.close();

        } else {
            throw new RuntimeException("Não foi possivel realizar o Backup");
        }

        return file.exists() ? file.getAbsolutePath() : "Caminho do arquivo desconhecido.";
    }

    public void restaurarDados(Context context, Uri dataUri){

        String uriRealPath = getUriRealPath(context, dataUri);
        Log.i("REAL_PATH", "restaurarDados: " + uriRealPath);

        String type = context.getContentResolver().getType(dataUri);
        Log.i("REAL_PATH", "restaurarDados: " + type);

        //TODO Fazer a leitura do arquivo e gravar dados no banco
        try {

            InputStream inputStream = context.getContentResolver().openInputStream(dataUri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String linha = reader.readLine();
            while(linha != null) {
                linha = reader.readLine();
                if(linha != null) {
//                    String[] valores = linha.split(",");
//                    for(int i = 0; i < valores.length; i++){
                        Log.i("READER", "restaurarDados: " + linha);
                        //TODO preencher objeto com os dados
//                    }
                }

            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

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

                //TODO verificar dados

                ps.println(dados);

                outputStream.close();
                ps.close();

            }


        } else {
            throw new RuntimeException("Falha ao exportar os dados");
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

    /* Get uri related content real local file path. */
    private String getUriRealPath(Context ctx, Uri uri)
    {
        String ret = "";

        if( isAboveKitKat() )
        {
            // Android OS above sdk version 19.
            ret = getUriRealPathAboveKitkat(ctx, uri);
        }else
        {
            // Android OS below sdk version 19
            ret = getImageRealPath(ctx.getContentResolver(), uri, null);
        }

        return ret;
    }

    private String getUriRealPathAboveKitkat(Context ctx, Uri uri)
    {
        String ret = "";

        if(ctx != null && uri != null) {

            if(isContentUri(uri))
            {
                if(isGooglePhotoDoc(uri.getAuthority()))
                {
                    ret = uri.getLastPathSegment();
                }else {
                    ret = getImageRealPath(ctx.getContentResolver(), uri, null);
                }
            }else if(isFileUri(uri)) {
                ret = uri.getPath();
            }else if(isDocumentUri(ctx, uri)){

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if(isMediaDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if("image".equals(docType))
                        {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        }else if("video".equals(docType))
                        {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        }else if("audio".equals(docType))
                        {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(ctx.getContentResolver(), mediaContentUri, whereClause);
                    }

                }else if(isDownloadDoc(uriAuthority))
                {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(ctx.getContentResolver(), downloadUriAppendId, null);

                }else if(isExternalStoreDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if("primary".equalsIgnoreCase(type))
                        {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    private boolean isAboveKitKat()
    {
        boolean ret = false;
        ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    private boolean isDocumentUri(Context ctx, Uri uri)
    {
        boolean ret = false;
        if(ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private boolean isContentUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("content".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private boolean isFileUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("file".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }


    /* Check whether this document is provided by ExternalStorageProvider. */
    private boolean isExternalStoreDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.externalstorage.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private boolean isDownloadDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.downloads.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    private boolean isMediaDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.media.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by google photos. */
    private boolean isGooglePhotoDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.google.android.apps.photos.content".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause)
    {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if(cursor!=null)
        {
            boolean moveToFirst = cursor.moveToFirst();
            if(moveToFirst)
            {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if( uri== MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Images.Media.DATA;
                }else if( uri==MediaStore.Audio.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Audio.Media.DATA;
                }else if( uri==MediaStore.Video.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }


}
