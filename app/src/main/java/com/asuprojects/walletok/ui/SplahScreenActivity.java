package com.asuprojects.walletok.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.asuprojects.walletok.MainActivity;
import com.asuprojects.walletok.R;

public class SplahScreenActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah_screen);



        preferences =  PreferenceManager.getDefaultSharedPreferences(this);


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
                    //TODO verifica se a preferencia "manter conectado" esta true, caso sim, nao exibir tela de login

                    boolean manterAtivo = preferences.getBoolean(getString(R.string.manter_conectado), false);
                    Log.i("ATIVO", "run: Manter Conectado " + manterAtivo);
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
        }, 1500);
    }
}
