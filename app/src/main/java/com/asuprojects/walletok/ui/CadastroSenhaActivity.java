package com.asuprojects.walletok.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.UsuarioDAO;
import com.asuprojects.walletok.model.Usuario;

public class CadastroSenhaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextInputLayout inputLayoutUsuario;
    private TextInputEditText inputEditTextUsuario;
    private TextInputLayout inputLayoutSenha;
    private TextInputEditText inputEditTextSenha;
    private TextInputLayout inputLayoutRepeteSenha;
    private TextInputEditText inputEditTextRepeteSenha;
    private AppCompatSpinner spinnerPergunta;
    private TextInputEditText inputEditTextRespostaPergunta;
    private AppCompatButton btnCadastro;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private UsuarioDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_senha);

        dao = new UsuarioDAO(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        inputLayoutUsuario = findViewById(R.id.first_input_layout_user);
        inputEditTextUsuario = findViewById(R.id.first_input_user);
        String usuarioNome = preferences.getString(getString(R.string.usuario), "");
        if(!usuarioNome.isEmpty() || !usuarioNome.equals("")){
            inputEditTextUsuario.setText(usuarioNome);
            inputEditTextUsuario.setEnabled(false);
        }

        inputLayoutSenha = findViewById(R.id.first_input_layout_password);
        inputEditTextSenha = findViewById(R.id.first_input_password);

        inputLayoutRepeteSenha = findViewById(R.id.first_input_layout_password_repeat);
        inputEditTextRepeteSenha = findViewById(R.id.first_input_password_repeat);

        inputEditTextRespostaPergunta = findViewById(R.id.input_pergunta_resposta);
        spinnerPergunta = findViewById(R.id.spinner_pergunta);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.perguntas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPergunta.setAdapter(adapter);
        spinnerPergunta.setOnItemSelectedListener(this);

        btnCadastro = findViewById(R.id.btn_cadastro);
        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor = preferences.edit();
                editor.putString(getString(R.string.usuario), inputEditTextUsuario.getText().toString());
                editor.apply();

                //TODO validacao de entrada de dados do usuario

                Usuario usuario = new Usuario();
                usuario.setUsuario(inputEditTextUsuario.getText().toString());
                usuario.setSenha(inputEditTextSenha.getText().toString());
                usuario.setPergunta(spinnerPergunta.getSelectedItem().toString());
                usuario.setResposta(inputEditTextRespostaPergunta.getText().toString());

                dao.insert(usuario);

                Intent intent = new Intent(CadastroSenhaActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Relacionado ao Spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int posicao, long id) {
        String perguntaSelecionada = (String) adapterView.getItemAtPosition(posicao);
        Log.i("SPINNER_SELECAO: ", perguntaSelecionada);
    }

    //Relacionado ao Spinner
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}