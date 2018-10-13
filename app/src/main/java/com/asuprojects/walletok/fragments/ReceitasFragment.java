package com.asuprojects.walletok.fragments;


import android.media.tv.TvView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.helper.MoneyUtil;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.model.Tipo;
import com.asuprojects.walletok.util.StringUtils;

import java.util.Calendar;
import java.util.List;

public class ReceitasFragment extends Fragment
        implements AdapterView.OnItemSelectedListener {

    private TextView valorTotalReceita;

    private AppCompatSpinner spinnerMes;

    private List<Receita> receitas;

    private ReceitaDAO dao;


    public ReceitasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dao = new ReceitaDAO(getContext());

        View view = inflater.inflate(R.layout.fragment_receitas, container, false);

        String mes = StringUtils.mesParaString(Calendar.getInstance().get(Calendar.MONTH) + 1);
        receitas = dao.getAllReceitasFrom(mes);

        valorTotalReceita = view.findViewById(R.id.textview_total_receita);
        valorTotalReceita.setText(MoneyUtil.valorTotalFrom(receitas));

        spinnerMes = view.findViewById(R.id.spinner_mes);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.meses, R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMes.setAdapter(arrayAdapter);
        spinnerMes.setOnItemSelectedListener(this);

        Calendar instance = Calendar.getInstance();
        spinnerMes.setSelection(instance.get(Calendar.MONTH));

        trocaFragment(receitas);

        return view;
    }

    private void trocaFragment(List<Receita> lista){
        FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
        if(lista.isEmpty()){
            ListaVaziaFragment listaVaziaFragment = new ListaVaziaFragment();
            listaVaziaFragment.setTipo(Tipo.RECEITA);
            tx.replace(R.id.frameLayoutRececita, listaVaziaFragment);
            tx.commit();
        } else {
            ListaReceitaFragment listaReceitaFragment = new ListaReceitaFragment();
            listaReceitaFragment.carregaReceitas(lista);
            tx.replace(R.id.frameLayoutRececita, listaReceitaFragment);
            tx.commit();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        receitas = dao.getAllReceitasFrom(StringUtils.mesParaString(position + 1));
        valorTotalReceita.setText(MoneyUtil.valorTotalFrom(receitas));
        trocaFragment(receitas);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
