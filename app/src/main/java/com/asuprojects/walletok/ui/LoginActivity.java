package com.asuprojects.walletok.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asuprojects.walletok.MainActivity;
import com.asuprojects.walletok.R;
import com.asuprojects.walletok.dao.UsuarioDAO;
import com.asuprojects.walletok.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutUser;
    private TextInputEditText inputUser;

    private TextInputLayout inputLayoutPassword;
    private TextInputEditText inputPassword;

    private AppCompatButton btnLogin;
    private AppCompatCheckBox checkbox;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private TextView esqueciSenha;

    private UsuarioDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dao = new UsuarioDAO(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String usuario = preferences.getString(getString(R.string.usuario), "");
        esqueciSenha = findViewById(R.id.textView_esqueciSenha);
        verificarUsuario(usuario);

        configuraComponentes();
        configuraBtnLogin();
    }

    private void configuraComponentes() {
        inputLayoutUser = findViewById(R.id.input_layout_user);
        inputUser = findViewById(R.id.input_text_user);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputPassword = findViewById(R.id.input_text_password);
        checkbox = findViewById(R.id.checkBox_manter_conectado);
    }

    private void configuraBtnLogin() {
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean logado = logarUsuario();
                if(logado){
                    editor = preferences.edit();
                    editor.putBoolean(getString(R.string.manter_conectado), checkbox.isChecked());
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.tx_login_invalido, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verificarUsuario(String usuario) {
        if(usuarioEstaVazio(usuario)){
            esqueciSenha.setText(R.string.tx_nao_tem_senha);
        }
    }

    public void esqueciSenha(View view){
        String usuarioNome = preferences.getString(getString(R.string.usuario), "");
        if(!usuarioEstaVazio(usuarioNome)){
            Usuario user = dao.findBy(usuarioNome);
            mostrarDialogPerguntaSecreta(user);
        } else {
            startActivity(new Intent(this, CadastroSenhaActivity.class));
        }
    }

    private boolean usuarioEstaVazio(String usuario){
        return usuario.isEmpty() || usuario.equals("");
    }

    private void mostrarDialogPerguntaSecreta(final Usuario user) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_pergunta_secreta_view, null);
        final TextView pergunta = view.findViewById(R.id.textView_pergunta);
        final EditText resposta = view.findViewById(R.id.editText_resposta);
        pergunta.setText(user.getPergunta());
        dialog.setView(view);
        dialog.setTitle(R.string.tx_pergunta_secreta);
        dialog.setPositiveButton(R.string.tx_verificar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               if(user.getResposta().equalsIgnoreCase(resposta.getText().toString())){
                   startActivity(new Intent(LoginActivity.this, CadastroSenhaActivity.class));
                   finish();
               } else {
                   Toast.makeText(LoginActivity.this, R.string.tx_resposta_invalida, Toast.LENGTH_SHORT).show();
               }
            }
        });
        dialog.setNegativeButton(R.string.tx_cancelar, null);
        dialog.show();
    }

    private boolean logarUsuario() {
        String usuario = inputUser.getText().toString().trim();
        String senha = inputPassword.getText().toString().trim();
        Usuario user = dao.findBy(usuario);
         if(user != null && user.getSenha().trim().equals(senha)){
            return true;
        }
        return false;
    }
}
