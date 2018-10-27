package com.asuprojects.walletok.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.util.BigDecimalConverter;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraficoFragment extends Fragment
        implements OnChartValueSelectedListener {

    public static final String MES_SELECAO = "MES";
    private TextView despesaCategoriaSelecao;
    private PieChart chart;
    private List<Despesa> despesasDoMes;

    private List<PieEntry> entries;

    private DespesaDAO dao;
    private String mesSelecao;

    public GraficoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grafico, container, false);

        dao = new DespesaDAO(getContext());

        if(savedInstanceState != null){
            mesSelecao = (String) savedInstanceState.getSerializable(MES_SELECAO);
        }

        despesaCategoriaSelecao = view.findViewById(R.id.despesaCategoriaSelecao);
        chart = view.findViewById(R.id.pieChart);

        this.despesasDoMes = dao.getAllDespesasFrom(mesSelecao);

        gerarGrafico();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(MES_SELECAO, mesSelecao);
        super.onSaveInstanceState(outState);
    }

    public void carregaLista(String mes) {
        this.mesSelecao = mes;

    }

    private void gerarGrafico() {
        chart.removeAllViews();
        entries = preencherValoresGrafico(despesasDoMes);
        configuraSetaValoresGrafico(entries);
        chart.refreshDrawableState();
    }

    private void configuraSetaValoresGrafico(List<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.grafico_label_titulo));
        dataSet.setColors(gerarCores());

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawHoleEnabled(false);
        chart.setUsePercentValues(true);
        chart.setCenterTextColor(Color.WHITE);
        chart.setRotationEnabled(false);
        chart.setEntryLabelColor(Color.rgb(89,89,89));

        Legend l = chart.getLegend();
        l.setEnabled(false);

        Description description = chart.getDescription();
        description.setText(getString(R.string.grafico_label_titulo));
        description.setEnabled(false);
    }

    private List<PieEntry> preencherValoresGrafico(List<Despesa> lista) {
        List<PieEntry> entradas = new ArrayList<>();
        Map<String, Float> valores = new HashMap<>();
        for(Despesa d : lista){
            if(valores.get(d.getCategoriaDespesa().getDescricao()) != null){
                valores.put(d.getCategoriaDespesa().getDescricao(),
                        valores.get(d.getCategoriaDespesa().getDescricao()).floatValue() + d.getValor().floatValue());
            }else{
                valores.put(d.getCategoriaDespesa().getDescricao(), d.getValor().floatValue());
            }
        }
        for(Map.Entry<String, Float> v : valores.entrySet()){
            entradas.add(new PieEntry(v.getValue(), v.getKey()));
        }
        return entradas;
    }

    private List<Integer> gerarCores(){
        List<Integer> cores = new ArrayList<>();
        int azul = Color.rgb(95,170,255);
        int verde = Color.rgb(92, 196, 114);
        int caqui = Color.rgb(185, 196, 92);
        int laranja = Color.rgb(240, 195, 90);
        int vermelho = Color.rgb(235, 70, 70);
        int roxo = Color.rgb(215, 150, 250);
        int rosa = Color.rgb(250, 150, 215);
        int amarelo = Color.rgb(230, 220, 50);
        cores.addAll(Arrays.asList(azul, verde, laranja, vermelho, roxo, rosa, amarelo, caqui));
        return cores;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        String label = ((PieEntry) e).getLabel();
        float value = ((PieEntry) e).getValue();
        String valorFormatadado = BigDecimalConverter.toStringFormatado(value);
        String valorTotalStr = label.concat(" : ").concat(valorFormatadado);
        despesaCategoriaSelecao.setText(valorTotalStr);
    }

    @Override
    public void onNothingSelected() {
        despesaCategoriaSelecao.setText("");
    }


}
