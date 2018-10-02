package com.asuprojects.walletok.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.asuprojects.walletok.MainActivity;
import com.asuprojects.walletok.R;

public class SplahScreenActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private int tempo = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah_screen);

        preferences =  PreferenceManager.getDefaultSharedPreferences(this);

        boolean desativarAbertura = preferences.getBoolean(getString(R.string.desativa_tela_abertura), false);
        if(desativarAbertura){
            tempo = 0;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean primeiroUso =  preferences.getBoolean(getString(R.string.primeiro_uso), true);
                Log.i("ATIVO", "run: Primeiro Uso " + primeiroUso);
                if(primeiroUso){
                    Intent intent = new Intent(SplahScreenActivity.this, FirstTimeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    boolean manterAtivo = preferences.getBoolean(getString(R.string.manter_conectado), false);
                    if(manterAtivo){
                        startActivity(new Intent(SplahScreenActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Intent intent = new Intent(SplahScreenActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }

            }
        }, tempo);
    }
}
