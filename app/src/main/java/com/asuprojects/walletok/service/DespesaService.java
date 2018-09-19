package com.asuprojects.walletok.service;

import android.content.Context;

import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.model.Despesa;

import java.util.List;

public class DespesaService {

    private DespesaDAO dao;

    public DespesaService(Context context){
        dao = new DespesaDAO(context);
    }

    public DespesaDAO getDao() {
        return dao;
    }

//    public List<Despesa> getDespesasDoMes(int mes){
//        dao.getAll()
//    }
}
