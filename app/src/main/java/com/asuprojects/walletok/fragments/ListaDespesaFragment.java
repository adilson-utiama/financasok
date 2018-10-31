package com.asuprojects.walletok.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.adapters.DespesaAdapter;
import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.helper.MoneyUtil;
import com.asuprojects.walletok.helper.RecyclerItemClickListener;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.ui.DespesaActivity;

import java.util.List;

public class ListaDespesaFragment extends Fragment {

    public static final String EDITAR_DESPESA = "EDITAR_DESPESA";
    private DespesaAdapter adapter;

    private DespesaDAO daoDespesa;
    private List<Despesa> despesas;

    private TextView totalDespesas;

    public ListaDespesaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_despesa, container, false);

        daoDespesa = new DespesaDAO(getContext());

        configuraRecyclerView(view);

        totalDespesas = view.findViewById(R.id.textview_total_despesa);
        totalDespesas.setText(MoneyUtil.valorTotalFrom(despesas));

        return view;
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

    public void carregaDespesas(List<Despesa> lista){
        despesas = lista;
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
                totalDespesas.setText(MoneyUtil.valorTotalFrom(despesas));
            }
        });
        dialog.setNegativeButton(R.string.opcao_nao, null);
        dialog.show();
    }

}
