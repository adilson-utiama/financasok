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

    private UsuarioDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dao = new UsuarioDAO(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        inputLayoutUser = findViewById(R.id.input_layout_user);
        inputUser = findViewById(R.id.input_text_user);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputPassword = findViewById(R.id.input_text_password);
        checkbox = findViewById(R.id.checkBox_manter_conectado);

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
                    Toast.makeText(LoginActivity.this, "Usuario ou senha invalido!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void esqueciSenha(View view){
        String usuarioNome = preferences.getString(getString(R.string.usuario), "");
        Usuario user = dao.findBy(usuarioNome);
        mostrarDialogPerguntaSecreta(user);
    }

    private void mostrarDialogPerguntaSecreta(final Usuario user) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_pergunta_secreta_view, null);
        final TextView pergunta = view.findViewById(R.id.textView_pergunta);
        final EditText resposta = view.findViewById(R.id.editText_resposta);
        pergunta.setText(user.getPergunta());
        dialog.setView(view);
        dialog.setTitle("Pergunta Secreta");
        dialog.setPositiveButton("Verficar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               if(user.getResposta().equalsIgnoreCase(resposta.getText().toString())){
                   startActivity(new Intent(LoginActivity.this, CadastroSenhaActivity.class));
                   finish();
               } else {
                   Toast.makeText(LoginActivity.this, "Resposta Inválida.", Toast.LENGTH_SHORT).show();
               }
            }
        });
        dialog.setNegativeButton("Cancelar", null);
        dialog.show();

    }

    private boolean logarUsuario() {
        String usuario = inputUser.getText().toString();
        String senha = inputPassword.getText().toString();

        Usuario user = dao.findBy(usuario);
         if(user != null && user.getSenha().trim().equals(senha)){
            return true;
        }
        return false;
    }
}
