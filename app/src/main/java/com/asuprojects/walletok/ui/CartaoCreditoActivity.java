package com.asuprojects.walletok.ui;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.Toast;

import com.asuprojects.walletok.R;

public class CartaoCreditoActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutEmissor;
    private TextInputEditText inputTextEmissor;
    private AppCompatSpinner spinnerBandeira;
    private AppCompatEditText inputDiaFatura;
    private AppCompatEditText inputLimiteCredito;
    private AppCompatButton btnAdicionaCartao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartao_credito);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Adicionar Cartão Credito");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        inputLayoutEmissor = findViewById(R.id.inputLayout_emissor);
        inputTextEmissor = findViewById(R.id.editText_emissor);
        spinnerBandeira = findViewById(R.id.spinner_bandeira_cartao);
        inputDiaFatura = findViewById(R.id.editText_dia_fatura);
        inputLimiteCredito = findViewById(R.id.editText_limite_credito);
        
        btnAdicionaCartao = findViewById(R.id.btn_adiciona_cartao);
        btnAdicionaCartao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CartaoCreditoActivity.this, "Adicionando Cartão...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
