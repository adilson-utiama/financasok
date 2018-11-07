package com.asuprojects.walletok.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.helper.MoneyUtil;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.model.Tipo;
import com.asuprojects.walletok.model.enums.Pagamento;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraficoFragment extends Fragment
        implements OnChartValueSelectedListener {

    public static final String MES_SELECAO = "MES";
    private PieChart chart;
    private List<Despesa> despesasDoMes;
    private DespesaDAO daoDespesa;
    private List<Receita> receitasDoMes;
    private ReceitaDAO daoReceita;

    private List<PieEntry> entries;

    private Calendar dataSelecionada;

    private TextView valorTotal;
    private TextView valorDisponivel;
    private TextView valorTotalCredito;
    private TextView valorDespesasProximoMes;

    private LinearLayoutCompat graficoContainer;

    public GraficoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grafico, container, false);

        graficoContainer = view.findViewById(R.id.grafico_container);
        graficoContainer.setAlpha(0);
        graficoContainer.animate().setDuration(500).alpha(1F).setListener(null);

        daoDespesa = new DespesaDAO(getContext());
        daoReceita = new ReceitaDAO(getContext());

        if(savedInstanceState != null){
            dataSelecionada = (Calendar) savedInstanceState.getSerializable(MES_SELECAO);
        }

        chart = view.findViewById(R.id.pieChart);

        valorTotal = view.findViewById(R.id.valor_total);
        valorDisponivel = view.findViewById(R.id.valor_disponivel);
        valorTotalCredito = view.findViewById(R.id.valor_total_credito);
        valorDespesasProximoMes = view.findViewById(R.id.total_despesas_proximo_mes);

        despesasDoMes = daoDespesa.getAllDespesasFrom(dataSelecionada);

        Calendar proximoMes = Calendar.getInstance();
        proximoMes.setTime(dataSelecionada.getTime());
        proximoMes.add(Calendar.MONTH, 1);
        BigDecimal despesasProximoMes = MoneyUtil.valorTotalBigDecimalFrom(daoDespesa.getAllDespesasFrom(proximoMes));
        valorDespesasProximoMes.setText(BigDecimalConverter.toStringFormatado(despesasProximoMes));

        receitasDoMes = daoReceita.getAllReceitasFrom(dataSelecionada);

        BigDecimal totalCredito = valorTotalPorTipoPagamento(despesasDoMes, Pagamento.CARTAO);
        valorTotalCredito.setText(BigDecimalConverter.toStringFormatado(totalCredito));

        calculaValorTotal();
        gerarGrafico();

        return view;
    }

    private BigDecimal valorTotalPorTipoPagamento(List<Despesa> despesas, Pagamento formaPagamento) {
        BigDecimal total = BigDecimal.ZERO;
        for(Despesa d : despesas){
            if(d.getPagamento().equals(formaPagamento)){
                total = total.add(d.getValor());
            }
        }
        return total;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(MES_SELECAO, dataSelecionada);
        super.onSaveInstanceState(outState);
    }

    public void dataSelecionada(Calendar data) {
        this.dataSelecionada = data;
        Log.i("DATA_SEL", "dataSelecionada: " + data.getTime());
    }

    private void gerarGrafico() {
        chart.removeAllViews();
        entries = preencherValoresGrafico(despesasDoMes);
        configuraSetaValoresGrafico(entries);
        chart.refreshDrawableState();
    }

    private void calculaValorTotal() {
        BigDecimal totalDespesas = totalFromDespesas(despesasDoMes);
        String totalFormatado = BigDecimalConverter.toStringFormatado(totalDespesas);
        if(!totalDespesas.equals(BigDecimal.ZERO)){
            valorTotal.setText(totalFormatado);
        } else {
            valorTotal.setText(R.string.despesa_total_zero);
        }
        BigDecimal totalDisponivel = totalFromReceitas(receitasDoMes);
        totalDisponivel = totalDisponivel.subtract(totalDespesas);
        if(totalDisponivel.doubleValue() < BigDecimal.ZERO.doubleValue()){
            valorDisponivel.setTextColor(Color.RED);
        }
        String disponivel = BigDecimalConverter.toStringFormatado(totalDisponivel);
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

    private void configuraSetaValoresGrafico(List<PieEntry> entries) {
        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.grafico_label_titulo));
        dataSet.setColors(gerarCores());

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.setOnChartValueSelectedListener(this);
        chart.setUsePercentValues(true);
        chart.setCenterTextColor(Color.WHITE);
        chart.setRotationEnabled(false);
        chart.setEntryLabelColor(Color.rgb(89,89,89));
        chart.setDrawHoleEnabled(true);
        chart.setCenterTextSize(18);
        chart.setCenterTextColor(Color.rgb(89,89,89));

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
        String valorTotalStr = label.concat("\n").concat(valorFormatadado);
        chart.setCenterText(valorTotalStr);
    }

    @Override
    public void onNothingSelected() { }


}
