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
import com.asuprojects.walletok.fragments.ReceitasFragment;
import com.asuprojects.walletok.helper.MoneyUtil;
import com.asuprojects.walletok.model.enums.CategoriaReceita;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.util.BigDecimalConverter;
import com.asuprojects.walletok.util.CalendarUtil;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class ReceitaActivity extends AppCompatActivity {

    public static final String EDITAR_RECEITA = "EDITAR_RECEITA";
    public static final double VALOR_MINIMO = 1.0;
    public static final String TAB_NUMBER = "TAB_NUMBER";

    private EditText valor;
    private AppCompatSpinner spinnerReceita;
    private AppCompatButton btnData;
    private TextInputEditText campoDescricao;

    private ReceitaDAO dao;

    private String current = "";
    private double valorDecimal;

    private Receita receita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita);

        dao = new ReceitaDAO(this);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.tx_adicionar_receita);
        }

        campoDescricao = findViewById(R.id.campo_descricao_receita);
        configuraEditTextValor();
        configuraSpinnerCategoria();
        configuraBtnSalvar();
        configuraBtnSelData();

        Intent intent = getIntent();
        eHEdicaoReceita(intent);
    }

    private void configuraEditTextValor() {
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
                    String emDouble = MoneyUtil.valorEmDouble(formatted);
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
    }

    private void configuraSpinnerCategoria() {
        spinnerReceita = findViewById(R.id.campo_spinner_receita);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categoria_receitas,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerReceita.setAdapter(adapter);
    }

    private void configuraBtnSelData() {
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
    }

    private void configuraBtnSalvar() {
        FloatingActionButton btnSalvar = findViewById(R.id.btn_salvar_receita);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(receita == null){
                    receita = new Receita();
                }
                if(valorEhValido()){
                    String dataBtn = btnData.getText().toString();
                    Calendar dt = CalendarUtil.stringToCalendar(dataBtn);
                    receita.setData(dt);
                    receita.setDescricao(campoDescricao.getText().toString());
                    receita.setValor(BigDecimal.valueOf(valorDecimal));
                    int position = spinnerReceita.getSelectedItemPosition();
                    receita.setCategoriaReceita(CategoriaReceita.toEnum(position));

                    dao.insertOrUpdate(receita);

                    Intent intent = new Intent(ReceitaActivity.this, MainActivity.class);
                    intent.putExtra(TAB_NUMBER, 1);
                    startActivity(intent);
                }

            }
        });
    }

    private void eHEdicaoReceita(Intent intent) {
        if(intent.hasExtra(EDITAR_RECEITA)){
            receita = (Receita) intent.getSerializableExtra(EDITAR_RECEITA);
            campoDescricao.setText(receita.getDescricao());
            valor.setText(BigDecimalConverter.toStringFormatado(receita.getValor()));
            btnData.setText(receita.getDataFormatada());

            int index = receita.getCategoriaReceita().getCodigo();
            spinnerReceita.setSelection(index, true);

            getSupportActionBar().setTitle(R.string.appbar_titulo_edicao_receita);
        }
    }

    private boolean valorEhValido() {
        if(valor.getText().toString().isEmpty()){
            Toast.makeText(ReceitaActivity.this, R.string.tx_msg_erro_val_valorembranco, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(valorDecimal < VALOR_MINIMO){
            Toast.makeText(ReceitaActivity.this, R.string.tx_msg_erro_val_valorbaixo, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setDataAtual() {
        String dataAtual = CalendarUtil.toStringFormatadaPelaRegiao(Calendar.getInstance());
        btnData.setText(dataAtual);
    }

    private OnSelectDateListener listener = new OnSelectDateListener() {
        @Override
        public void onSelect(List<Calendar> calendars) {
            Calendar calendar = calendars.get(0);
            String dataSelecionada = CalendarUtil.toStringFormatadaPelaRegiao(calendar);
            btnData.setText(dataSelecionada);
        }
    };

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }
}
