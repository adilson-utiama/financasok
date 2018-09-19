package com.asuprojects.walletok.fragments;


import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class ResumoFragment extends Fragment
        implements OnChartValueSelectedListener, AdapterView.OnItemSelectedListener {

    private PieChart chart;

    private TextView valorTotal;
    private TextView valorDisponivel;

    private AppCompatSpinner spinnerMes;

    private DespesaDAO daoDespesa;
    private ReceitaDAO daoReceita;

    private TextView despesaCategoriaSelecao;

    private String mesSelecao;
    private List<Despesa> despesasDoMes;
    private List<Receita> receitasDoMes;

    private List<PieEntry> entries;

    public ResumoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resumo, container, false);

        daoDespesa = new DespesaDAO(getContext());
        daoReceita = new ReceitaDAO(getContext());

        valorTotal = view.findViewById(R.id.valor_total);
        valorDisponivel = view.findViewById(R.id.valor_disponivel);
        despesaCategoriaSelecao = view.findViewById(R.id.despesaCategoriaSelecao);

        spinnerMes = view.findViewById(R.id.spinner_resumoMes);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.meses, R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMes.setAdapter(arrayAdapter);
        spinnerMes.setOnItemSelectedListener(this);

        int mes = Calendar.getInstance().get(Calendar.MONTH);
        mesSelecao = StringUtils.mesParaString(mes + 1);
        spinnerMes.setSelection(mes);

        despesasDoMes = daoDespesa.getAllTarefasFrom(mesSelecao);
        receitasDoMes = daoReceita.getAllReceitasFrom(mesSelecao);

        chart = view.findViewById(R.id.pieChart);

        calculaValorTotal();

        gerarGrafico();

//        entries = new ArrayList<>();
//        entries.add(new PieEntry(15L, "Compras"));

        return view;
    }

    private void gerarGrafico() {
        chart.removeAllViews();
        entries = preencherValoresGrafico(despesasDoMes);
        configuraSetaValoresGrafico(entries);
        chart.refreshDrawableState();
        if(despesasDoMes.size() == 0){
            despesaCategoriaSelecao.setText("");
            Toast.makeText(getContext(), "Não há dados para visualizar", Toast.LENGTH_LONG).show();
        }
    }

    private void configuraSetaValoresGrafico(List<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, "Despesas");
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
//        chart.setCenterText("Despesas");
//        chart.setCenterTextColor(android.R.color.white);
//        chart.setHoleColor(R.color.backgroud);
//        chart.setHoleRadius(0);
        chart.setEntryLabelColor(Color.rgb(89,89,89));

        Legend l = chart.getLegend();
        l.setEnabled(false);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setXEntrySpace(7f);
//        l.setYEntrySpace(0f);
//        l.setYOffset(5f);

        Description description = chart.getDescription();
        description.setText("Despesas");
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

    private void calculaValorTotal() {
        BigDecimal totalDespesas = totalFromDespesas(despesasDoMes);
        Log.i("CALCULO", "calculaValorTotal: " + totalDespesas.doubleValue());
        if(!totalDespesas.equals(BigDecimal.ZERO)){
            valorTotal.setText("Despesa Total: " + BigDecimalConverter.toStringFormatado(totalDespesas));
        } else {
            valorTotal.setText("Despesa Total: " + "R$ 0,00");
        }
        BigDecimal totalDisponivel = totalFromReceitas(receitasDoMes);
        totalDisponivel = totalDisponivel.subtract(totalDespesas);
        if(totalDisponivel.doubleValue() < BigDecimal.ZERO.doubleValue()){
            valorDisponivel.setTextColor(Color.RED);
        }else{
            valorDisponivel.setTextColor(Color.WHITE);
        }
        valorDisponivel.setText("Disponivel: " + BigDecimalConverter.toStringFormatado(totalDisponivel));

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
        //TODO corrigir problema de formatacao - long para double

        despesaCategoriaSelecao.setText(label + " : " + valorFormatadado);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mesSelecao = StringUtils.mesParaString(position + 1);
        despesasDoMes = daoDespesa.getAllTarefasFrom(mesSelecao);
        receitasDoMes = daoReceita.getAllReceitasFrom(mesSelecao);
        calculaValorTotal();
        gerarGrafico();
    }

    @Override
    public void onNothingSelected() {
        despesaCategoriaSelecao.setText("");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

//    @Override
//    public void onDetach() {
//        daoDespesa.close();
//        daoReceita.close();
//        super.onDetach();
//    }

    //    @Override
//    public void onDestroy() {
//        daoDespesa.close();
//        daoReceita.close();
//        super.onDestroy();
//    }
}
