package com.asuprojects.walletok.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.helper.MoneyUtil;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.model.Tipo;

import java.util.Calendar;
import java.util.List;

public class ReceitasFragment extends Fragment {

    private TextView valorTotalReceita;
    private List<Receita> receitas;
    private ReceitaDAO dao;

    private ImageView arrowLeft;
    private ImageView arrowRight;
    private TextView centerText;

    private int mesSelecao = 0;
    private int anoAtual = 0;
    private Calendar dataAtual;
    private String[] meses;

    public ReceitasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receitas, container, false);

        dao = new ReceitaDAO(getContext());

        dataAtual = Calendar.getInstance();
        mesSelecao = dataAtual.get(Calendar.MONTH);
        anoAtual = dataAtual.get(Calendar.YEAR);
        meses = view.getResources().getStringArray(R.array.meses);

        arrowLeft = view.findViewById(R.id.receitas_arrow_left);
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
        arrowRight = view.findViewById(R.id.receitas_arrow_right);
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

        receitas = dao.getAllReceitasFrom(dataAtual);

        centerText = view.findViewById(R.id.receitas_tx_center_mes_sel);
        centerText.setText(meses[mesSelecao].concat(" / ").concat(String.valueOf(anoAtual)));

        preencheValorTotalReceitas(view);
        trocaFragment(receitas);

        return view;
    }

    private void preencheValorTotalReceitas(View view) {
        valorTotalReceita = view.findViewById(R.id.textview_total_receita);
        valorTotalReceita.setText(MoneyUtil.valorTotalFrom(receitas));
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

    private void selecaoMes(Calendar data){
        receitas = dao.getAllReceitasFrom(data);
        String valorTotal = MoneyUtil.valorTotalFrom(receitas);
        valorTotalReceita.setText(valorTotal);
        centerText.setText(meses[mesSelecao].concat(" / ").concat(String.valueOf(anoAtual)));
        trocaFragment(receitas);
    }

}
