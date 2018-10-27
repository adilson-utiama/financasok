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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.UsuarioDAO;
import com.asuprojects.walletok.model.Usuario;

public class CadastroSenhaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int MIN_CARACTERES = 6;
    public static final int MAX_CARACTERES = 15;
    private TextInputLayout inputLayoutUsuario;
    private TextInputEditText inputEditTextUsuario;
    private TextInputLayout inputLayoutSenha;
    private TextInputEditText inputEditTextSenha;
    private TextInputLayout inputLayoutRepeteSenha;
    private TextInputEditText inputEditTextRepeteSenha;
    private AppCompatSpinner spinnerPergunta;
    private TextInputEditText inputEditTextRespostaPergunta;

    private SharedPreferences preferences;

    private UsuarioDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_senha);

        dao = new UsuarioDAO(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        configuraComponentes();
        verificaUsuarioVazio();
        configuraBotaoCadastro();
    }

    private void configuraBotaoCadastro() {
        AppCompatButton btnCadastro = findViewById(R.id.btn_cadastro);
        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validaCampos()){
                    salvaNomeUsuarioEmPreferences();
                    salvaUsuarioNoBanco();
                }
            }
        });
    }

    private void configuraComponentes() {
        inputLayoutUsuario = findViewById(R.id.first_input_layout_user);
        inputEditTextUsuario = findViewById(R.id.first_input_user);

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
    }

    private void salvaUsuarioNoBanco() {
        Usuario usuario = new Usuario();
        usuario.setUsuario(inputEditTextUsuario.getText().toString().trim());
        usuario.setSenha(inputEditTextSenha.getText().toString().trim());
        usuario.setPergunta(spinnerPergunta.getSelectedItem().toString().trim());
        usuario.setResposta(inputEditTextRespostaPergunta.getText().toString().trim());

        dao.insert(usuario);

        Intent intent = new Intent(CadastroSenhaActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void salvaNomeUsuarioEmPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.usuario), inputEditTextUsuario.getText().toString().trim());
        editor.apply();
    }

    private void verificaUsuarioVazio() {
        String usuarioNome = preferences.getString(getString(R.string.usuario), "");
        if(!usuarioNome.isEmpty() || !usuarioNome.equals("")){
            inputEditTextUsuario.setText(usuarioNome);
            inputEditTextUsuario.setEnabled(false);
        }
    }

    private boolean validaCampos() {
        String inputUsuario = inputEditTextUsuario.getText().toString().trim();
        String inputSenha = inputEditTextSenha.getText().toString().trim();
        String inputSenhaRepete = inputEditTextRepeteSenha.getText().toString().trim();
        String inputRespostaPergunta = inputEditTextRespostaPergunta.getText().toString().trim();

        if(inputUsuario.isEmpty() || inputUsuario.equals("")) {
            inputLayoutUsuario.setError(getString(R.string.msg_erro_validacao_campo_obrigatorio));
            inputLayoutUsuario.setErrorEnabled(true);
            return false;
        }
        if(inputUsuario.length() < MIN_CARACTERES || inputUsuario.length() > MAX_CARACTERES) {
            inputLayoutUsuario.setError(getString(R.string.msg_erro_validacao_caracteres_size));
            inputLayoutUsuario.setErrorEnabled(true);
            return false;
        }
        if(inputSenha.isEmpty() || inputSenha.equals("")) {
            inputLayoutSenha.setError(getString(R.string.msg_erro_senha_vazia));
            inputLayoutSenha.setErrorEnabled(true);
            return false;
        }
        if(inputSenha.length() < MIN_CARACTERES) {
            inputLayoutSenha.setError(getString(R.string.msg_erro_validacao_senha_caracteres));
            inputLayoutSenha.setErrorEnabled(true);
            return false;
        }
        if(inputSenhaRepete.isEmpty() || inputSenhaRepete.equals("")) {
            inputLayoutRepeteSenha.setError(getString(R.string.msg_erro_senha_nao_corresponde));
            inputLayoutRepeteSenha.setErrorEnabled(true);
            return false;
        }
        if(!inputSenha.contentEquals(inputSenhaRepete)) {
            inputLayoutRepeteSenha.setError(getString(R.string.msg_erro_digite_novamente));
            inputLayoutRepeteSenha.setErrorEnabled(true);
            return false;
        }
        if(inputRespostaPergunta.isEmpty() || inputRespostaPergunta.equals("")){
            Toast.makeText(this, R.string.msg_erro_resposta_pergunta_vazia, Toast.LENGTH_SHORT).show();
            return false;
        }

        inputLayoutUsuario.setErrorEnabled(false);
        inputLayoutSenha.setErrorEnabled(false);
        inputLayoutRepeteSenha.setErrorEnabled(false);

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int posicao, long id) {
        String perguntaSelecionada = (String) adapterView.getItemAtPosition(posicao);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
