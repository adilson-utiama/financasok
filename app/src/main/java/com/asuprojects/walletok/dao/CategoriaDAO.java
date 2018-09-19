package com.asuprojects.walletok.dao;

import android.content.Context;

import com.asuprojects.walletok.database.BancoSQLite3;

public class CategoriaDAO {

    private BancoSQLite3 banco;

    public CategoriaDAO(Context context){
        this.banco = new BancoSQLite3(context);
    }

    //TODO Listar todas categorias de acordo com tipo

    //TODO Apagar categoria (Se categoria estiver em uso, nao podera apagar)

    //TODO Adicionar categoria
}
