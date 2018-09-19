package com.asuprojects.walletok.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoSQLite3 extends SQLiteOpenHelper {

    private static final int VERSAO = 1;
    private static final String NOME_BANCO = "financas";

    public BancoSQLite3(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CRIAR_TABELA_DESPESAS);
        db.execSQL(CRIAR_TABELA_USUARIO);
        db.execSQL(CRIAR_TABELA_RECEITAS);
        //db.execSQL(CRIAR_TABELA_CATEGORIA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DELETAR_TABELA_DESPESAS);
        db.execSQL(DELETAR_TABELA_USUARIO);
        db.execSQL(DELETAR_TABELA_RECEIITAS);
        //db.execSQL(DELETAR_TABELA_CATEGORIA);
        onCreate(db);
    }

    private static final String CRIAR_TABELA_RECEITAS =
            "CREATE TABLE " + TabelaReceita.NOME_TABELA + " (" +
                    TabelaReceita.COLUNA_ID + " INTEGER PRIMARY KEY," +
                    TabelaReceita.COLUNA_DESCRICAO + " TEXT, " +
                    TabelaReceita.COLUNA_CATEGORIA + " TEXT, " +
                    TabelaReceita.COLUNA_DATA + " TEXT, " +
                    TabelaReceita.COLUNA_VALOR + " REAL )";

    private static final String DELETAR_TABELA_RECEIITAS =
            "DROP TABLE IF EXISTS " + TabelaReceita.NOME_TABELA;

    private static final String CRIAR_TABELA_DESPESAS =
            "CREATE TABLE " + TabelaDespesa.NOME_TABELA + " (" +
                    TabelaDespesa.COLUNA_ID + " INTEGER PRIMARY KEY," +
                    TabelaDespesa.COLUNA_DESCRICAO + " TEXT, " +
                    TabelaDespesa.COLUNA_CATEGORIA + " TEXT, " +
                    TabelaDespesa.COLUNA_DATA + " TEXT, " +
                    TabelaDespesa.COLUNA_PAGAMENTO + " TEXT, " +
                    TabelaDespesa.COLUNA_VALOR + " REAL )";

    private static final String DELETAR_TABELA_DESPESAS =
            "DROP TABLE IF EXISTS " + TabelaDespesa.NOME_TABELA;

    private static final String CRIAR_TABELA_USUARIO =
            "CREATE TABLE " + TabelaUsuario.NOME_TABELA + " (" +
                    TabelaUsuario.COLUNA_ID + " INTEGER PRIMARY KEY," +
                    TabelaUsuario.COLUNA_USUARIO + " TEXT, " +
                    TabelaUsuario.COLUNA_SENHA + " TEXT, " +
                    TabelaUsuario.COLUNA_PERGUNTA + " TEXT, " +
                    TabelaUsuario.COLUNA_RESPOSTA + " TEXT )";

    private static final String DELETAR_TABELA_USUARIO =
            "DROP TABLE IF EXISTS " + TabelaUsuario.NOME_TABELA;

    private static final String CRIAR_TABELA_CATEGORIA =
            "CREATE TABLE " + TabelaCategoria.NOME_TABELA + " (" +
                    TabelaCategoria.COLUNA_ID + " INTEGER PRIMARY KEY," +
                    TabelaCategoria.COLUNA_TIPO + " INTEGER, " +
                    TabelaCategoria.COLUNA_DESCRICAO + " TEXT )";

    private static final String DELETAR_TABELA_CATEGORIA =
            "DROP TABLE IF EXISTS " + TabelaCategoria.NOME_TABELA;
}
