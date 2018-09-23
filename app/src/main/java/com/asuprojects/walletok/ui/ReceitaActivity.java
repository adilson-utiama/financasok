package com.asuprojects.walletok.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.asuprojects.walletok.MainActivity;
import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.ReceitaDAO;
import com.asuprojects.walletok.helper.MoneyUtil;
import com.asuprojects.walletok.model.enums.CategoriaReceita;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.util.BigDecimalConverter;
import com.asuprojects.walletok.util.CalendarConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReceitaActivity extends AppCompatActivity {

    private EditText valor;
    private AppCompatSpinner spinnerReceita;
    private AppCompatButton btnData;
    private TextInputEditText campoDescricao;
    private FloatingActionButton btnSalvar;

    private ReceitaDAO dao;

    private String current = "";
    private double valorDecimal;

    private List<String> listaCategorias;

    private Receita receita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);

        dao = new ReceitaDAO(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Adiciona Receita");

        campoDescricao = findViewById(R.id.campo_descricao_receita);

        valor = findViewById(R.id.campo_valor_receita);
        valor.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    valor.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[R$,.]", "");
                    double parsed = Double.parseDouble(cleanString);
                    String formatted = MoneyUtil.formatar((parsed/100));
                    Log.i("TEXT_WATCH", "onTextChanged: " + formatted);
                    String emDouble = MoneyUtil.valorEmDouble(formatted);
                    Log.i("TEXT_WATCH", "onTextChanged: " + emDouble);
                    valorDecimal = Double.parseDouble(emDouble);
                    current = formatted;
                    valor.setText(formatted);
                    valor.setSelection(formatted.length());
                    valor.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        spinnerReceita = findViewById(R.id.campo_spinner_receita);
        listaCategorias = new ArrayList<>();
        for(CategoriaReceita c : CategoriaReceita.values()){
            listaCategorias.add(c.getDescricao());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReceita.setAdapter(adapter);



        btnSalvar = findViewById(R.id.btn_salvar_receita);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(receita == null){
                    receita = new Receita();
                }

                if(valorEhValido()){
                    String dataBtn = btnData.getText().toString();
                    String[] split = dataBtn.split("/");
                    int dia = Integer.parseInt(split[0]);
                    int mes = Integer.parseInt(split[1]) - 1;
                    int ano = Integer.parseInt(split[2]);
                    Calendar dt = Calendar.getInstance();
                    dt.set(ano, mes, dia);
                    receita.setData(dt);
                    receita.setDescricao(campoDescricao.getText().toString());
                    receita.setValor(BigDecimal.valueOf(valorDecimal));
                    int position = spinnerReceita.getSelectedItemPosition();
                    receita.setCategoriaReceita(CategoriaReceita.toEnum(listaCategorias.get(position)));

                    dao.insertOrUpdate(receita);

                    startActivity(new Intent(ReceitaActivity.this, MainActivity.class));
                }

            }
        });



        btnData = findViewById(R.id.campo_data_receita);
        setDataAtual();
        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerBuilder builder = new DatePickerBuilder(ReceitaActivity.this, listener)
                        .headerColor(R.color.colorPrimary)
                        .selectionColor(R.color.colorPrimary)
                        .date(Calendar.getInstance())
                        .pickerType(CalendarView.ONE_DAY_PICKER);

                DatePicker datePicker = builder.build();
                datePicker.show();

            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra("EDITAR_RECEITA")){
            receita = (Receita) intent.getSerializableExtra("EDITAR_RECEITA");
            campoDescricao.setText(receita.getDescricao());
            valor.setText(BigDecimalConverter.toStringFormatado(receita.getValor()));
            btnData.setText(receita.getDataFormatada());

            int index = listaCategorias.indexOf(receita.getCategoriaReceita().getDescricao());
            spinnerReceita.setSelection(index, true);

            getSupportActionBar().setTitle("Edição");
        }


    }

    private boolean valorEhValido() {
        if(valor.getText().toString().isEmpty()){
            Toast.makeText(ReceitaActivity.this, "Valor está em Branco", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(valorDecimal < 1.0){
            Toast.makeText(ReceitaActivity.this, "Valor muito baixo", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setDataAtual() {
        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int ano = calendar.get(Calendar.YEAR);
        StringBuilder builder = new StringBuilder();
        builder.append(dia).append("/").append(mes + 1).append("/").append(ano);
        btnData.setText(builder.toString());
    }

    private OnSelectDateListener listener = new OnSelectDateListener() {
        @Override
        public void onSelect(List<Calendar> calendars) {
            Calendar calendar = calendars.get(0);
            int dia = calendar.get(Calendar.DAY_OF_MONTH);
            int mes = calendar.get(Calendar.MONTH);
            int ano = calendar.get(Calendar.YEAR);
            StringBuilder builder = new StringBuilder();
            builder.append(dia).append("/").append(mes + 1).append("/").append(ano);
            btnData.setText(builder.toString());
        }
    };

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }
}
