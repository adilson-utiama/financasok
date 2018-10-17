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
import android.widget.Toast;

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

                if(validaCampos()){
                    editor = preferences.edit();
                    editor.putString(getString(R.string.usuario), inputEditTextUsuario.getText().toString().trim());
                    editor.apply();

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

            }
        });
    }

    private boolean validaCampos() {
        String inputUsuario = inputEditTextUsuario.getText().toString().trim();
        String inputSenha = inputEditTextSenha.getText().toString().trim();
        String inputSenhaRepete = inputEditTextRepeteSenha.getText().toString().trim();
        String inputRespostaPergunta = inputEditTextRespostaPergunta.getText().toString().trim();

        if(inputUsuario.isEmpty() || inputUsuario.equals("")) {
            inputLayoutUsuario.setError("Obrigatório preencher campo usuario");
            inputLayoutUsuario.setErrorEnabled(true);
            return false;
        }
        if(inputUsuario.length() < 6 || inputUsuario.length() > 15) {
            inputLayoutUsuario.setError("Nome usuario deve conter de 6 a no maximo 15 caracteres");
            inputLayoutUsuario.setErrorEnabled(true);
            return false;
        }
        if(inputSenha.isEmpty() || inputSenha.equals("")) {
            inputLayoutSenha.setError("Senha não pode estar em branco");
            inputLayoutSenha.setErrorEnabled(true);
            return false;
        }
        if(inputSenha.length() < 6) {
            inputLayoutSenha.setError("Senha precisa conter no minimo 6 caracteres");
            inputLayoutSenha.setErrorEnabled(true);
            return false;
        }
        if(inputSenhaRepete.isEmpty() || inputSenhaRepete.equals("")) {
            inputLayoutRepeteSenha.setError("Necessario redigitar a senha");
            inputLayoutRepeteSenha.setErrorEnabled(true);
            return false;
        }
        if(!inputSenha.contentEquals(inputSenhaRepete)) {
            inputLayoutRepeteSenha.setError("Senha não corresponde. Digite Novamente");
            inputLayoutRepeteSenha.setErrorEnabled(true);
            return false;
        }
        if(inputRespostaPergunta.isEmpty() || inputRespostaPergunta.equals("")){
            Toast.makeText(this, "Por favor, informe uma resposta a pergunta.", Toast.LENGTH_SHORT).show();
            return false;
        }

        inputLayoutUsuario.setErrorEnabled(false);
        inputLayoutSenha.setErrorEnabled(false);
        inputLayoutRepeteSenha.setErrorEnabled(false);

        return true;
    }

    //Relacionado ao Spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int posicao, long id) {
        String perguntaSelecionada = (String) adapterView.getItemAtPosition(posicao);
    }

    //Relacionado ao Spinner
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
