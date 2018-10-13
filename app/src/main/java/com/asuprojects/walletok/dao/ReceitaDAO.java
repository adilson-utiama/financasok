package com.asuprojects.walletok.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.asuprojects.walletok.database.BancoSQLite3;
import com.asuprojects.walletok.database.TabelaDespesa;
import com.asuprojects.walletok.database.TabelaReceita;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.enums.CategoriaReceita;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.util.BigDecimalConverter;
import com.asuprojects.walletok.util.CalendarConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReceitaDAO {

    private BancoSQLite3 banco;

    public ReceitaDAO(Context context){
        this.banco = new BancoSQLite3(context);
    }

    public List<Receita> getAllReceitasFrom(String mes){
        SQLiteDatabase db = banco.getReadableDatabase();
        List<Receita> receitas = new ArrayList<>();

        String query = "SELECT * FROM " + TabelaReceita.NOME_TABELA
                + " WHERE strftime('%m', data) = ?";

        Log.i("QUERY", "getAllTarefasFrom: " + query);

        Cursor cursor = db.rawQuery(query, new String[]{ mes });
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Receita receita = receitaFrom(cursor);
            receitas.add(receita);
            cursor.moveToNext();
        }

        return receitas;
    }

    public List<Receita> listAll(){
        SQLiteDatabase db = banco.getReadableDatabase();
        List<Receita> receitas = new ArrayList<>();

        String query = "SELECT * FROM  " + TabelaReceita.NOME_TABELA + ";";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            Receita receita = receitaFrom(cursor);
            receitas.add(receita);
            cursor.moveToNext();
        }

        return receitas;
    }

    private Receita receitaFrom(Cursor cursor) {

        Receita receita = new Receita();

        try{
            long id = cursor.getLong(cursor.getColumnIndex(TabelaReceita.COLUNA_ID));
            receita.set_id(id);
        } catch (CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        String descricao = cursor.getString(cursor.getColumnIndex(TabelaReceita.COLUNA_DESCRICAO));
        String data = cursor.getString(cursor.getColumnIndex(TabelaReceita.COLUNA_DATA));
        String valor = cursor.getString(cursor.getColumnIndex(TabelaReceita.COLUNA_VALOR));
        String categoria = cursor.getString(cursor.getColumnIndex(TabelaReceita.COLUNA_CATEGORIA));

        receita.setDescricao(descricao);
        receita.setData(CalendarConverter.toCalendar(data));
        receita.setCategoriaReceita(CategoriaReceita.toEnum(categoria));
        receita.setValor(BigDecimalConverter.toBigDecimal(valor));

        return receita;
    }

    public long insertOrUpdate(Receita receita) {
        SQLiteDatabase db = banco.getWritableDatabase();
        ContentValues values = despesaParaContentValues(receita);
        if (receita.get_id() != 0 && idExistsInDatabase(receita.get_id())) {
            return update(receita);
        } else {
            return db.insertOrThrow(TabelaReceita.NOME_TABELA, null, values);
        }

    }

    private boolean idExistsInDatabase(long id) {
        Receita receita = findOne(id);
        if(receita != null){
            return true;
        }
        return false;
    }

    public long update(Receita receita) {
        SQLiteDatabase db = banco.getWritableDatabase();
        ContentValues values = despesaParaContentValues(receita);
        int updateId = db.update(TabelaReceita.NOME_TABELA,
                values,
                " _id = ? ",
                new String[]{String.valueOf(receita.get_id())});

        return updateId;
    }

    private ContentValues despesaParaContentValues(Receita receita) {
        ContentValues values = new ContentValues();
        values.put(TabelaReceita.COLUNA_DESCRICAO, receita.getDescricao());
        values.put(TabelaReceita.COLUNA_CATEGORIA, receita.getCategoriaReceita().getDescricao());
        values.put(TabelaReceita.COLUNA_DATA, CalendarConverter.toStringFormatada(receita.getData()));
        values.put(TabelaReceita.COLUNA_VALOR, receita.getValor().doubleValue());

        return values;
    }

    public BigDecimal valorTotal(){
        BigDecimal total = BigDecimal.ZERO;
        List<Receita> receitas = this.listAll();
        for (Receita r : receitas) {
            total = total.add(r.getValor());
        }
        return total;
    }

    public void close(){
        banco.close();
    }

    public Receita findOne(long id) {
        SQLiteDatabase db = banco.getReadableDatabase();

        String selection = TabelaReceita.COLUNA_ID + " = ?";
        String[] args = {String.valueOf(id)};

        Cursor cursor = db.query(TabelaReceita.NOME_TABELA,
                null,
                selection,
                args,
                null,
                null,
                null);

        Receita receita = null;
        if (cursor != null && cursor.getCount() != 0) {
            cursor.moveToFirst();
            receita = receitaFrom(cursor);
        } else {
            return null;
        }
        cursor.close();

        return receita;
    }

    public long delete(long id) {
        SQLiteDatabase db = banco.getWritableDatabase();

        int idDeletado = db.delete(TabelaReceita.NOME_TABELA,
                " _id = ?",
                new String[]{String.valueOf(id)});

        return idDeletado;
    }

}
