package com.asuprojects.walletok.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.adapters.ReceitaAdapter;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.helper.MoneyUtil;
import com.asuprojects.walletok.helper.RecyclerItemClickListener;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.ui.ReceitaActivity;

import java.util.List;

public class ListaReceitaFragment extends Fragment {

    public static final String EDITAR_RECEITA = "EDITAR_RECEITA";
    private ReceitaAdapter adapter;

    private List<Receita> receitas;
    private ReceitaDAO dao;

    private TextView valorTotalReceita;
    private LinearLayoutCompat listaReceitaContainer;

    public ListaReceitaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista_receita, container, false);

        listaReceitaContainer = view.findViewById(R.id.lista_receitas_container);
        listaReceitaContainer.setAlpha(0);
        listaReceitaContainer.animate().setDuration(500).alpha(1F).setListener(null);

        dao = new ReceitaDAO(getContext());

        configuraRecyclerView(view);

        valorTotalReceita = view.findViewById(R.id.textview_total_receita);
        valorTotalReceita.setText(MoneyUtil.valorTotalFrom(receitas));

        return view;
    }

    private void configuraRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_receita);
        adapter = new ReceitaAdapter(getActivity(), receitas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        edicaoTarefa(view, position);
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
    }

    private void edicaoTarefa(View view, int position) {
        Receita receita  = receitas.get(position);
        Receita receitaEditar = dao.findOne(receita.get_id());
        Intent intent = new Intent(view.getContext(), ReceitaActivity.class);
        intent.putExtra(EDITAR_RECEITA, receitaEditar);
        startActivity(intent);
    }

    public void carregaReceitas(List<Receita> lista){
        receitas = lista;
        if(adapter != null){
            adapter.setReceitas(receitas);
        } else {
            adapter = new ReceitaAdapter(getActivity(), receitas);
        }
    }

    private void mostrarDialogDeRemocao(final Receita receita, final int posicao){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.DialogCustom);
        dialog.setTitle(R.string.dialog_titulo_apagar_receita);
        dialog.setMessage(getString(R.string.dialog_msg_apagar) + " '" + receita.getDescricao() + "' ?");
        dialog.setPositiveButton(getString(R.string.opcao_sim), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dao.delete(receita.get_id());
                receitas.remove(posicao);
                adapter.notifyItemRemoved(posicao);
                valorTotalReceita.setText(MoneyUtil.valorTotalFrom(receitas));
            }
        });
        dialog.setNegativeButton(getString(R.string.opcao_nao), null);
        dialog.show();
    }
}
