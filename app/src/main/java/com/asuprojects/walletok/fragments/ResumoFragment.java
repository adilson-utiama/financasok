package com.asuprojects.walletok.fragments;


import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.model.Tipo;
import com.asuprojects.walletok.util.BigDecimalConverter;
import com.asuprojects.walletok.util.StringUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResumoFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    public static final String MES_SELECAO = "MES";
    private TextView valorTotal;
    private TextView valorDisponivel;

    private String mesSelecao;

    private DespesaDAO daoDespesa;
    private ReceitaDAO daoReceita;

    private List<Despesa> despesasDoMes;
    private List<Receita> receitasDoMes;

    public ResumoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resumo, container, false);

        if(savedInstanceState != null) {
            if(savedInstanceState.getSerializable(MES_SELECAO) != null){
                mesSelecao = (String) savedInstanceState.getSerializable(MES_SELECAO);
            }
        }

        daoDespesa = new DespesaDAO(getContext());
        daoReceita = new ReceitaDAO(getContext());

        valorTotal = view.findViewById(R.id.valor_total);
        valorDisponivel = view.findViewById(R.id.valor_disponivel);

        AppCompatSpinner spinnerMes = view.findViewById(R.id.spinner_resumoMes);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.meses, R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMes.setAdapter(arrayAdapter);
        spinnerMes.setOnItemSelectedListener(this);

        int mes = Calendar.getInstance().get(Calendar.MONTH);
        mesSelecao = StringUtils.mesParaString(mes + 1);
        spinnerMes.setSelection(mes);

        despesasDoMes = daoDespesa.getAllDespesasFrom(mesSelecao);
        receitasDoMes = daoReceita.getAllReceitasFrom(mesSelecao);

        calculaValorTotal();

        if(despesasDoMes != null){
            trocaFragment(despesasDoMes);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(MES_SELECAO, mesSelecao);
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
            graficoFragment.carregaLista(mesSelecao);
            tx.replace(R.id.frameLayoutGrafico, graficoFragment);
            tx.commit();
        }
    }


    private void calculaValorTotal() {
        BigDecimal totalDespesas = totalFromDespesas(despesasDoMes);
        String totalFormatado = getString(R.string.despesa_total_label).concat(" ")
                .concat(BigDecimalConverter.toStringFormatado(totalDespesas));;
        if(!totalDespesas.equals(BigDecimal.ZERO)){
            valorTotal.setText(totalFormatado);
        } else {
            valorTotal.setText(R.string.despesa_total_zero);
        }
        BigDecimal totalDisponivel = totalFromReceitas(receitasDoMes);
        totalDisponivel = totalDisponivel.subtract(totalDespesas);
        if(totalDisponivel.doubleValue() < BigDecimal.ZERO.doubleValue()){
            valorDisponivel.setTextColor(Color.RED);
        }else{
            valorDisponivel.setTextColor(Color.WHITE);
        }
        String disponivel = getString(R.string.valor_disponivel_label).concat(" ")
                .concat(BigDecimalConverter.toStringFormatado(totalDisponivel));
        valorDisponivel.setText(disponivel);

    }

    private BigDecimal totalFromDespesas(List<Despesa> despesasDoMes) {
        BigDecimal total = BigDecimal.ZERO;
        for(Despesa d : despesasDoMes){
            total = total.add(d.getValor());
        }
        return total;
    }

    private BigDecimal totalFromReceitas(List<Receita> receitas) {
        BigDecimal total = BigDecimal.ZERO;
        for(Receita c : receitas){
            total = total.add(c.getValor());
        }
        return total;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mesSelecao = StringUtils.mesParaString(position + 1);
        despesasDoMes = daoDespesa.getAllDespesasFrom(mesSelecao);
        receitasDoMes = daoReceita.getAllReceitasFrom(mesSelecao);
        calculaValorTotal();
        trocaFragment(despesasDoMes);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }


}
