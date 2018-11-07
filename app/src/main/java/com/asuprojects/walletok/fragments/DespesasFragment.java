package com.asuprojects.walletok.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.helper.MoneyUtil;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Tipo;
import com.asuprojects.walletok.util.StringUtils;

import java.util.Calendar;
import java.util.List;

public class DespesasFragment extends Fragment {

    public static final String DATA_ATUAL = "DATA_ATUAL";
    private List<Despesa> despesas;
    private DespesaDAO daoDespesa;

    private ImageView arrowLeft;
    private ImageView arrowRight;
    private TextView centerText;

    private int mesSelecao = 0;
    private int anoAtual = 0;
    private Calendar dataAtual;
    private String[] meses;

    public DespesasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_despesas, container, false);

        dataAtual = Calendar.getInstance();
        if(savedInstanceState != null){
            dataAtual = (Calendar) savedInstanceState.getSerializable(DATA_ATUAL);
        }

        mesSelecao = dataAtual.get(Calendar.MONTH);
        anoAtual = dataAtual.get(Calendar.YEAR);
        meses = view.getResources().getStringArray(R.array.meses);

        daoDespesa = new DespesaDAO(getContext());
        despesas = daoDespesa.getAllDespesasFrom(dataAtual);

        arrowLeft = view.findViewById(R.id.despesas_arrow_left);
        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mesSelecao <= 0){
                    mesSelecao = 11;
                    anoAtual -= 1;
                } else {
                    mesSelecao -= 1;
                }
                dataAtual.set(anoAtual, mesSelecao + 1, 0);
                selecaoMes(dataAtual);
            }
        });
        arrowRight = view.findViewById(R.id.despesas_arrow_right);
        arrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mesSelecao >= 11){
                    mesSelecao = 0;
                    anoAtual += 1;
                } else {
                    mesSelecao += 1;
                }
                dataAtual.set(anoAtual, mesSelecao + 1, 0);
                selecaoMes(dataAtual);
            }
        });

        centerText = view.findViewById(R.id.despesas_tx_center_mes_sel);
        centerText.setText(meses[mesSelecao].concat(" / ").concat(String.valueOf(anoAtual)));
        trocaFragment();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(DATA_ATUAL, dataAtual);
        super.onSaveInstanceState(outState);
    }

    private void selecaoMes(Calendar data){
        despesas = daoDespesa.getAllDespesasFrom(data);
        centerText.setText(meses[mesSelecao].concat(" / ").concat(String.valueOf(anoAtual)));
        trocaFragment();
    }

    private void trocaFragment(){
        FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
        if(despesas.isEmpty()){
            ListaVaziaFragment listaVaziaFragment = new ListaVaziaFragment();
            listaVaziaFragment.setTipo(Tipo.DESPESA);
            tx.replace(R.id.frameLayoutDespesa, listaVaziaFragment);
            tx.commit();
        } else {
            ListaDespesaFragment listaDespesaFragment = new ListaDespesaFragment();
            listaDespesaFragment.carregaDespesas(dataAtual);
            tx.replace(R.id.frameLayoutDespesa, listaDespesaFragment);
            tx.commit();
        }
    }


}
