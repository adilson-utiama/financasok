package com.asuprojects.walletok.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.adapters.DespesaAdapter;
import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.helper.MoneyUtil;
import com.asuprojects.walletok.helper.RecyclerItemClickListener;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.ui.DespesaActivity;
import com.asuprojects.walletok.util.BigDecimalConverter;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class ListaDespesaFragment extends Fragment {

    public static final String EDITAR_DESPESA = "EDITAR_DESPESA";
    private static final String DATA_ATUAL = "DATA_ATUAL";
    private DespesaAdapter adapter;

    private DespesaDAO daoDespesa;
    private List<Despesa> despesas;
    private List<Receita> receitas;

    private TextView totalDespesas;
    private TextView totalReceitas;

    private LinearLayoutCompat containerDespesas;

    private Calendar dataAtual;
    private ProgressBar barraDespesas;

    public ListaDespesaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_despesa, container, false);

        if(savedInstanceState != null){
            dataAtual = (Calendar) savedInstanceState.getSerializable(DATA_ATUAL);
        }

        containerDespesas = view.findViewById(R.id.lista_despesas_container);
        containerDespesas.setAlpha(0);
        containerDespesas.animate().setDuration(500).alpha(1F).setListener(null);

        daoDespesa = new DespesaDAO(getContext());
        despesas = daoDespesa.getAllDespesasFrom(dataAtual);

        configuraRecyclerView(view);

        barraDespesas = view.findViewById(R.id.barra_desepsas);
        totalReceitas = view.findViewById(R.id.textview_total_receita);
        totalDespesas = view.findViewById(R.id.textview_total_despesa);

        receitas = new ReceitaDAO(getActivity()).getAllReceitasFrom(dataAtual);

        calculaValorDisponivel();

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(DATA_ATUAL, dataAtual);
        super.onSaveInstanceState(outState);
    }

    private void calculaValorDisponivel() {
        BigDecimal despesasTotal = MoneyUtil.valorTotalBigDecimalFrom(despesas);
        BigDecimal receitaTotal = MoneyUtil.valorTotalBigDecimalFrom(receitas);
        receitaTotal = receitaTotal.subtract(despesasTotal);
        totalReceitas.setText(BigDecimalConverter.toStringFormatado(receitaTotal));
        totalDespesas.setText(MoneyUtil.valorTotalFrom(despesas));
        barraDespesas.setMax(MoneyUtil.valorTotalBigDecimalFrom(receitas).intValue());
        barraDespesas.setProgress(MoneyUtil.valorTotalBigDecimalFrom(despesas).intValue());
        if (receitaTotal.doubleValue() < 0.0){
            barraDespesas.setProgressDrawable(getResources().getDrawable(R.drawable.barra_vermelha));
        } else {
            barraDespesas.setProgressDrawable(getResources().getDrawable(R.drawable.barra_despesas));
        }
    }

    private void configuraRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_despesa);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DespesaAdapter(getActivity(), despesas);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Despesa despesa = despesas.get(position);
                        Despesa despesaEditar = daoDespesa.findOne(despesa.get_id());
                        Intent intent = new Intent(view.getContext(), DespesaActivity.class);
                        intent.putExtra(EDITAR_DESPESA, despesaEditar);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Despesa despesa = despesas.get(position);
                        mostrarDialogDeRemocao(despesa, position);

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));
    }

    public void carregaDespesas(Calendar data){
        dataAtual = data;
        despesas = new DespesaDAO(getActivity()).getAllDespesasFrom(data);
        if(adapter != null){
            adapter.setDespesas(despesas);
        } else {
            adapter = new DespesaAdapter(getActivity(), despesas);
        }
    }

    private void mostrarDialogDeRemocao(final Despesa despesa, final int posicao){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.DialogCustom);
        dialog.setTitle(R.string.dialog_titulo_deletar_tarefa);
        dialog.setMessage(getString(R.string.dialog_msg_apagar) + " '" + despesa.getDescricao() + "' ?");
        dialog.setPositiveButton(R.string.opcao_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                daoDespesa.delete(despesa.get_id());
                despesas.remove(posicao);
                adapter.notifyItemRemoved(posicao);
                calculaValorDisponivel();
            }
        });
        dialog.setNegativeButton(R.string.opcao_nao, null);
        dialog.show();
    }

}
