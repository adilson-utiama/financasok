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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.asuprojects.walletok.MainActivity;
import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.DespesaDAO;
import com.asuprojects.walletok.helper.MoneyUtil;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.enums.CategoriaDespesa;
import com.asuprojects.walletok.model.enums.Pagamento;
import com.asuprojects.walletok.util.BigDecimalConverter;
import com.asuprojects.walletok.util.CalendarConverter;
import com.asuprojects.walletok.util.CalendarUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DespesaActivity extends AppCompatActivity{

    public static final String EDITAR_DESPESA = "EDITAR_DESPESA";
    public static final double VALOR_MINIMO = 0.1;
    private TextInputEditText descricao;
    private EditText valor;
    private AppCompatSpinner spinner;
    private AppCompatButton btnData;

    private List<String> listaCategorias;

    private DespesaDAO dao;
    private Despesa despesa;

    private String current = "";
    private double valorDecimal;

    private Pagamento pagamento = Pagamento.DINHEIRO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa);

        dao = new DespesaDAO(getApplicationContext());

        cofiguraComponentes();

        Intent intent = getIntent();
        if(intent.hasExtra(EDITAR_DESPESA)){
            intentEdicaoDespesa(intent);
        }

    }

    private void cofiguraComponentes() {
        configuraToolbar();
        descricao = findViewById(R.id.campo_descricao_receita);
        configuraCampoValor();
        configuraSpinnerCategoria();
        configuraFabSalvar();
        configuraBtnData();
    }

    private void configuraBtnData() {
        btnData = findViewById(R.id.campo_data_receita);
        btnData.setText(CalendarUtil.toStringFormatadaPelaRegiao(Calendar.getInstance()));
        btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerBuilder builder = new DatePickerBuilder(DespesaActivity.this, listener)
                        .headerColor(R.color.colorPrimary)
                        .selectionColor(R.color.colorPrimary)
                        .date(Calendar.getInstance())
                        .pickerType(CalendarView.ONE_DAY_PICKER);
                DatePicker datePicker = builder.build();
                datePicker.show();

            }
        });
    }

    private void configuraFabSalvar() {
        FloatingActionButton btnSalvar = findViewById(R.id.btn_salvar_receita);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(despesa == null){
                    despesa = new Despesa();
                }
                if(valorEhValido()){
                    salvaDespesa();
                }

            }
        });
    }

    private void salvaDespesa() {
        String dataBtn = btnData.getText().toString();
//        String[] split = dataBtn.split("/");
//        int dia = Integer.parseInt(split[0]);
//        int mes = Integer.parseInt(split[1]) - 1;
//        int ano = Integer.parseInt(split[2]);
        Calendar dt = CalendarUtil.stringToCalendar(dataBtn);
//        dt.set(ano, mes, dia);

        despesa.setDescricao(descricao.getText().toString());
        despesa.setData(dt);
        int position = spinner.getSelectedItemPosition();
        despesa.setCategoriaDespesa(CategoriaDespesa.toEnum(listaCategorias.get(position)));
        despesa.setValor(BigDecimal.valueOf(valorDecimal));
        despesa.setPagamento(pagamento);

        dao.insertOrUpdate(despesa);

        Intent intent = new Intent(DespesaActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void configuraSpinnerCategoria() {
        listaCategorias = new ArrayList<>();
        for(CategoriaDespesa c : CategoriaDespesa.values()){
            listaCategorias.add(c.getDescricao());
        }
        spinner = findViewById(R.id.campo_spinner_receita);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaCategorias);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void configuraCampoValor() {
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
                    String formatted =  MoneyUtil.formatar((parsed/100));
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

    private void configuraToolbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.appbar_titulo_despesa);
    }

    private void intentEdicaoDespesa(Intent intent) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        despesa = (Despesa) intent.getSerializableExtra(EDITAR_DESPESA);
        descricao.setText(despesa.getDescricao());
        valor.setText(BigDecimalConverter.toStringFormatado(despesa.getValor()));
        btnData.setText(despesa.getDataFormatada());

        if(despesa.getPagamento().equals(Pagamento.DINHEIRO)){
            radioGroup.check(R.id.pag_dinheiro);
        }else if(despesa.getPagamento().equals(Pagamento.CARTAO)){
            radioGroup.check(R.id.pag_cartao);
        }else{
            radioGroup.check(R.id.pag_outros);
        }

        int index = listaCategorias.indexOf(despesa.getCategoriaDespesa().getDescricao());
        spinner.setSelection(index, true);
        getSupportActionBar().setTitle(R.string.appbar_titulo_edicao);
    }

    private boolean valorEhValido() {
        if(valor.getText().toString().isEmpty()){
            Toast.makeText(DespesaActivity.this, R.string.msg_erro_valor_em_branco, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(valorDecimal < VALOR_MINIMO){
            Toast.makeText(DespesaActivity.this, R.string.msg_erro_valor_baixo, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.pag_dinheiro:
                if (checked)
                    pagamento = Pagamento.DINHEIRO;
                    break;
            case R.id.pag_cartao:
                if (checked)
                    pagamento = Pagamento.CARTAO;
                    break;
            case R.id.pag_outros:
                if(checked)
                    pagamento = Pagamento.OUTROS;
                    break;
        }
    }

    private OnSelectDateListener listener = new OnSelectDateListener() {
        @Override
        public void onSelect(List<Calendar> calendars) {
            Calendar calendar = calendars.get(0);
//            int dia = calendar.get(Calendar.DAY_OF_MONTH);
//            int mes = calendar.get(Calendar.MONTH);
//            int ano = calendar.get(Calendar.YEAR);
//            StringBuilder builder = new StringBuilder();
//            builder.append(dia).append("/").append(mes + 1).append("/").append(ano);
            String dataAtual = CalendarUtil.toStringFormatadaPelaRegiao(calendar);
            btnData.setText(dataAtual);
        }
    };

    @Override
    protected void onDestroy() {
        dao.close();
        super.onDestroy();
    }

}
