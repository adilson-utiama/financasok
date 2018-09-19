package com.asuprojects.walletok.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.adapters.ReceitaAdapter;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.helper.RecyclerItemClickListener;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.ui.DespesaActivity;
import com.asuprojects.walletok.ui.ReceitaActivity;
import com.asuprojects.walletok.util.StringUtils;

import java.util.Calendar;
import java.util.List;

public class ReceitasFragment extends Fragment
        implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private ReceitaAdapter adapter;

    private AppCompatSpinner spinnerMes;

    private List<Receita> receitas;

    private ReceitaDAO dao;


    public ReceitasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dao = new ReceitaDAO(getContext());
        receitas = dao.listAll();

        View view = inflater.inflate(R.layout.fragment_receitas, container, false);

        String mes = StringUtils.mesParaString(Calendar.getInstance().get(Calendar.MONTH) + 1);
        receitas = dao.getAllReceitasFrom(mes);

        recyclerView = view.findViewById(R.id.recyclerView_receita);

        adapter = new ReceitaAdapter(receitas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        spinnerMes = view.findViewById(R.id.spinner_mes);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.meses, R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMes.setAdapter(arrayAdapter);
        spinnerMes.setOnItemSelectedListener(this);

        Calendar instance = Calendar.getInstance();
        spinnerMes.setSelection(instance.get(Calendar.MONTH));

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        receitas = dao.getAllReceitasFrom(StringUtils.mesParaString(position + 1));
        adapter.setReceitas(receitas);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
