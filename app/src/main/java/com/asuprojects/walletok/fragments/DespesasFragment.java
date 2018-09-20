package com.asuprojects.walletok.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Tipo;
import com.asuprojects.walletok.util.StringUtils;

import java.util.Calendar;
import java.util.List;

public class DespesasFragment extends Fragment
                implements AdapterView.OnItemSelectedListener {



    private AppCompatSpinner spinnerMes;

    private List<Despesa> despesas;

    private DespesaDAO daoDespesa;

    public DespesasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_despesas, container, false);

        daoDespesa = new DespesaDAO(getContext());

        String mes = StringUtils.mesParaString(Calendar.getInstance().get(Calendar.MONTH) + 1);
        despesas = daoDespesa.getAllTarefasFrom(mes);

        spinnerMes = view.findViewById(R.id.spinner_mes);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.meses, R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMes.setAdapter(arrayAdapter);
        spinnerMes.setOnItemSelectedListener(this);

        Calendar instance = Calendar.getInstance();
        spinnerMes.setSelection(instance.get(Calendar.MONTH));

        trocaFragment(despesas);

        return view;
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        despesas = daoDespesa.getAllTarefasFrom(StringUtils.mesParaString(position + 1));
        trocaFragment(despesas);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void trocaFragment(List<Despesa> listaDespesa){
        FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
        if(despesas.isEmpty()){
            ListaVaziaFragment listaVaziaFragment = new ListaVaziaFragment();
            listaVaziaFragment.setTipo(Tipo.DESPESA);
            tx.replace(R.id.frameLayoutDespesa, listaVaziaFragment);
            tx.commit();
        } else {
            ListaDespesaFragment listaDespesaFragment = new ListaDespesaFragment();
            listaDespesaFragment.carregaDespesas(despesas);
            tx.replace(R.id.frameLayoutDespesa, listaDespesaFragment);
            tx.commit();
        }
    }


}
