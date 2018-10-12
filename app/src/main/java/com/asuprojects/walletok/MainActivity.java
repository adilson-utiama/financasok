package com.asuprojects.walletok;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.asuprojects.walletok.adapters.TabAdapter;
import com.asuprojects.walletok.fragments.DespesasFragment;
import com.asuprojects.walletok.fragments.ReceitasFragment;
import com.asuprojects.walletok.fragments.ResumoFragment;
import com.asuprojects.walletok.model.enums.Extensao;
import com.asuprojects.walletok.ui.ConfiguracoesActivity;
import com.asuprojects.walletok.ui.DespesaActivity;
import com.asuprojects.walletok.ui.ReceitaActivity;
import com.asuprojects.walletok.ui.SobreActivity;
import com.asuprojects.walletok.util.FilesUtil;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    private static final int LOAD_FILE = 200;
    private static final int WRITE_FILE = 100;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WalletOk");

        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this,
                        drawerLayout, toolbar,
                        R.string.open_drawer, R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        DespesasFragment despesasFragment = new DespesasFragment();
        ReceitasFragment receitasFragment = new ReceitasFragment();
        ResumoFragment resumoFragment = new ResumoFragment();

        TabAdapter abasAdapter = new TabAdapter(getSupportFragmentManager());
        abasAdapter.adicionar(despesasFragment, "Despesas");
        abasAdapter.adicionar(receitasFragment, "Receitas");
        abasAdapter.adicionar(resumoFragment, "Resumo");

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(abasAdapter);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionMenu fabMenu =  findViewById(R.id.fab_menu);
        fabMenu.setClosedOnTouchOutside(true);

        FloatingActionButton fabReceita = findViewById(R.id.fab_adiciona_receita);
        fabReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ReceitaActivity.class));
            }
        });

        FloatingActionButton fabDespesa = findViewById(R.id.fab_adiciona_despesa);
        fabDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DespesaActivity.class);
                startActivity(intent);
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.item_configuracoes:
                startActivity(new Intent(this, ConfiguracoesActivity.class));
                return true;
            case R.id.item_sair:
                mostrarDialogSair();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogSair() {
        AlertDialog.Builder dialogSair = new AlertDialog.Builder(this);
        dialogSair.setMessage("Deseja sair/deslogar do App?")
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref = PreferenceManager
                                .getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean(getString(R.string.manter_conectado), false);
                        editor.commit();

                        finishAffinity();
                    }
                })
                .setNegativeButton("NÂO", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_importar: {

                mostrarDialogImportarDados();


                break;
            }
            case R.id.menu_configuracoes: {
                startActivity(new Intent(MainActivity.this, ConfiguracoesActivity.class));
                break;
            }
            case R.id.menu_exportar: {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED){

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_FILE);

                    }

                } else {
                    mostrarDialogBackup();
                }
                break;
            }
            case R.id.menu_sobre: {
                startActivity(new Intent(MainActivity.this, SobreActivity.class));
                break;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void mostrarDialogImportarDados() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Importar Dados")
                .setMessage("Este procedimento ira apagar todos os dados atualmente gravados\n Continuar?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("*/*");

                        if(intent.resolveActivity(getPackageManager()) != null){
                            startActivityForResult(intent, LOAD_FILE);
                        }
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_FILE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mostrarDialogBackup();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    private void mostrarDialogBackup() {
        View view = getLayoutInflater().inflate(R.layout.view_exportar_dados_dialog, null);
        final TextView nomeArquivo = view.findViewById(R.id.nome_arquivo);
        final RadioGroup formatos = view.findViewById(R.id.radioGroup);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Exportar dados")
                //.setMessage("O backup sera realizado no formato .csv")
                .setView(view)
                .setPositiveButton("EXPORTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FilesUtil util = new FilesUtil();
                        Extensao ext = null;
                        try {

                            String arquivo = nomeArquivo.getText().toString();
                            int checkedRadioButtonId = formatos.getCheckedRadioButtonId();
                            if(checkedRadioButtonId == R.id.csv_format){
                                ext = Extensao.CSV;
                            }
                            if(checkedRadioButtonId == R.id.json_format){
                                ext = Extensao.JSON;
                            }

                            //TODO implementar nome arquivo e determinar formato de saida para metodos seguinte

                            util.exportarDados(arquivo, ext, MainActivity.this);
                            Toast.makeText(MainActivity.this, "Backup realizado com Sucesso! \n" + arquivo, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                      }
                })
                .setNegativeButton("CANCELAR", null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == LOAD_FILE){
                Uri dataUri = data.getData();
                new FilesUtil().importarDados(MainActivity.this, dataUri);
                Log.i("RESULT", "onActivityResult: " + dataUri);
            }
        }
    }
}
