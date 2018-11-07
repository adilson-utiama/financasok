package com.asuprojects.walletok.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Tipo;

import java.util.Calendar;
import java.util.List;

public class ResumoFragment extends Fragment {

    public static final String DATA_ATUAL = "DATA_ATUAL";
    private List<Despesa> despesasDoMes;
    private DespesaDAO daoDespesa;

    private ImageView arrowLeft;
    private ImageView arrowRight;
    private TextView centerText;

    private int mesSelecao = 0;
    private int anoAtual = 0;
    private Calendar dataAtual;

    public ResumoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resumo, container, false);

        dataAtual = Calendar.getInstance();
        if(savedInstanceState != null) {
             dataAtual = (Calendar) savedInstanceState.getSerializable(DATA_ATUAL);
        }
        mesSelecao = dataAtual.get(Calendar.MONTH);
        anoAtual = dataAtual.get(Calendar.YEAR);

        final String[] meses = view.getResources().getStringArray(R.array.meses);

        centerText = view.findViewById(R.id.resumo_tx_center_mes_sel);
        centerText.setText(meses[mesSelecao].concat(" / ").concat(String.valueOf(anoAtual)));

        arrowLeft = view.findViewById(R.id.resumo_arrow_left);
        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mesSelecao <= 0){
                    mesSelecao = 11;
                    anoAtual -= 1;
                } else {
                    mesSelecao -= 1;
                }
                centerText.setText(meses[mesSelecao].concat(" / ").concat(String.valueOf(anoAtual)));
                dataAtual.set(anoAtual, mesSelecao + 1, 0);
                selecaoMes(dataAtual);
            }
        });
        arrowRight = view.findViewById(R.id.resumo_arrow_right);
        arrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mesSelecao >= 11){
                    mesSelecao = 0;
                    anoAtual += 1;
                } else {
                    mesSelecao += 1;
                }
                centerText.setText(meses[mesSelecao].concat(" / ").concat(String.valueOf(anoAtual)));
                dataAtual.set(anoAtual, mesSelecao + 1, 0);
                selecaoMes(dataAtual);
            }
        });

        daoDespesa = new DespesaDAO(getContext());

        despesasDoMes = daoDespesa.getAllDespesasFrom(String.valueOf(mesSelecao + 1));

        if(despesasDoMes != null){
            trocaFragment(despesasDoMes);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(DATA_ATUAL, dataAtual);
        super.onSaveInstanceState(outState);
    }

    private void trocaFragment(List<Despesa> lista){
        FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
        if(lista.isEmpty()){
            ListaVaziaFragment listaVaziaFragment = new ListaVaziaFragment();
            listaVaziaFragment.setTipo(Tipo.GRAFICO);
            tx.replace(R.id.frameLayoutGrafico, listaVaziaFragment);
            tx.commit();
        } else {
            GraficoFragment graficoFragment = new GraficoFragment();
            graficoFragment.dataSelecionada(dataAtual);
            tx.replace(R.id.frameLayoutGrafico, graficoFragment);
            tx.commit();
        }
    }

    private void selecaoMes(Calendar data){
        despesasDoMes = daoDespesa.getAllDespesasFrom(data);
        trocaFragment(despesasDoMes);
    }

}
