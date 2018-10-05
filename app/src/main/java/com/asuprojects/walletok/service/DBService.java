package com.asuprojects.walletok.service;

import android.content.Context;

import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Receita;

import java.util.List;

public class DBService {

    private DespesaDAO despesaDAO;
    private ReceitaDAO receitaDAO;

    public DBService(Context context){
        despesaDAO = new DespesaDAO(context);
        receitaDAO = new ReceitaDAO(context);
    }

    public DespesaDAO getDespesaDAO() {
        return despesaDAO;
    }

    public ReceitaDAO getReceitaDAO() {
        return receitaDAO;
    }
}
