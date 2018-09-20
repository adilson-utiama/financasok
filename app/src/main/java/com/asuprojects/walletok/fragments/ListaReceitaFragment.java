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
import com.asuprojects.walletok.adapters.ReceitaAdapter;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.helper.RecyclerItemClickListener;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.ui.ReceitaActivity;

import java.util.List;

public class ListaReceitaFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReceitaAdapter adapter;

    private List<Receita> receitas;
    private ReceitaDAO dao;


    public ListaReceitaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_receita, container, false);

        dao = new ReceitaDAO(getContext());

        recyclerView = view.findViewById(R.id.recyclerView_receita);

        adapter = new ReceitaAdapter(receitas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Receita receita  = receitas.get(position);
                        Receita receitaEditar = dao.findOne(receita.get_id());
                        Intent intent = new Intent(view.getContext(), ReceitaActivity.class);
                        intent.putExtra("EDITAR_RECEITA", receitaEditar);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Receita receita = receitas.get(position);
                        mostrarDialogDeRemocao(receita, position);

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }));


        return view;
    }

    public void carregaReceitas(List<Receita> lista){
        receitas = lista;
        if(adapter != null){
            adapter.setReceitas(receitas);
        } else {
            adapter = new ReceitaAdapter(receitas);
        }
    }

    private void mostrarDialogDeRemocao(final Receita receita, final int posicao){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.DialogCustom);
        dialog.setTitle("Remoção de Receita");
        dialog.setMessage("APAGAR " + receita.getDescricao() + " ?");
        dialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dao.delete(receita.get_id());
                receitas.remove(posicao);
                adapter.notifyItemRemoved(posicao);
                //calculaValorTotal();
            }
        });
        dialog.setNegativeButton("NÂO", null);
        dialog.show();
    }
}
