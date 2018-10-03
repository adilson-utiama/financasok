package com.asuprojects.walletok.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

import com.asuprojects.walletok.R;

public class ConfiguracoesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        getSupportActionBar().setTitle("Configurações");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.configuracoes, new ConfiguracoesFragment())
                .commit();

    }


    public static class ConfiguracoesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            onAttach(getActivity());
        }



        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.contentEquals(getString(R.string.protecao_por_senha))){
                String usuario = sharedPreferences.getString(getString(R.string.usuario), "");
                if(usuario.isEmpty() || usuario.equals("")){
                    getActivity().startActivity(new Intent(getActivity(), CadastroSenhaActivity.class));
                }
            }
        }
    }
}
