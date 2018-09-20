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

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.adapters.DespesaAdapter;
import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.helper.RecyclerItemClickListener;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.ui.DespesaActivity;

import java.util.List;

public class ListaDespesaFragment extends Fragment {

    private RecyclerView recyclerView;
    private DespesaAdapter adapter;

    private DespesaDAO daoDespesa;
    private List<Despesa> despesas;

    public ListaDespesaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_despesa, container, false);

        daoDespesa = new DespesaDAO(getContext());

        recyclerView = view.findViewById(R.id.recyclerview_despesa);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DespesaAdapter(despesas);
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
                        intent.putExtra("EDITAR_DESPESA", despesaEditar);
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

        return view;
    }

    public void carregaDespesas(List<Despesa> lista){
        despesas = lista;
        if(adapter != null){
            adapter.setDespesas(despesas);
        } else {
            adapter = new DespesaAdapter(despesas);
        }
    }

    private void mostrarDialogDeRemocao(final Despesa despesa, final int posicao){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.DialogCustom);
        dialog.setTitle("Remoção de Despesa");
        dialog.setMessage("APAGAR " + despesa.getDescricao() + " ?");
        dialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                daoDespesa.delete(despesa.get_id());
                despesas.remove(posicao);
                adapter.notifyItemRemoved(posicao);
            }
        });
        dialog.setNegativeButton("NÂO", null);
        dialog.show();
    }

}
