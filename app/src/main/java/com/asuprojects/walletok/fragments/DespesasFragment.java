package com.asuprojects.walletok.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.asuprojects.walletok.MainActivity;
import com.asuprojects.walletok.R;
import com.asuprojects.walletok.adapters.DespesaAdapter;
import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.helper.RecyclerItemClickListener;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.ui.DespesaActivity;
import com.asuprojects.walletok.util.BigDecimalConverter;
import com.asuprojects.walletok.util.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class DespesasFragment extends Fragment
                implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private DespesaAdapter adapter;

    private AppCompatSpinner spinnerMes;

    private List<Despesa> despesas;

    private DespesaDAO daoDespesa;

    public DespesasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_despesas, container, false);

        daoDespesa = new DespesaDAO(getContext());

        String mes = StringUtils.mesParaString(Calendar.getInstance().get(Calendar.MONTH) + 1);
        despesas = daoDespesa.getAllTarefasFrom(mes);

        spinnerMes = view.findViewById(R.id.spinner_mes);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.meses, R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerMes.setAdapter(arrayAdapter);
        spinnerMes.setOnItemSelectedListener(this);

        Calendar instance = Calendar.getInstance();
        spinnerMes.setSelection(instance.get(Calendar.MONTH));

        recyclerView = view.findViewById(R.id.recyclerview);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        despesas = daoDespesa.getAllTarefasFrom(StringUtils.mesParaString(position + 1));
        adapter.setDespesas(despesas);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


//    @Override
//    public void onDestroy() {
//        daoDespesa.close();
//        daoReceita.close();
//        super.onDestroy();
//    }
}
