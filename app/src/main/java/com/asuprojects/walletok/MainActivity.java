package com.asuprojects.walletok;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.asuprojects.walletok.service.FileService;
import com.asuprojects.walletok.ui.ConfiguracoesActivity;
import com.asuprojects.walletok.ui.DespesaActivity;
import com.asuprojects.walletok.ui.ReceitaActivity;
import com.asuprojects.walletok.ui.SobreActivity;
import com.asuprojects.walletok.util.FilesUtil;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    private static final int LOAD_FILE = 200;
    private static final int WRITE_FILE = 100;
    private ViewPager viewPager;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TabAdapter abasAdapter;

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

        abasAdapter = new TabAdapter(getSupportFragmentManager());
        abasAdapter.adicionar(despesasFragment, "Despesas");
        abasAdapter.adicionar(receitasFragment, "Receitas");
        abasAdapter.adicionar(resumoFragment, "Resumo");


        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(abasAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
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
            case R.id.menu_backup: {
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
            case R.id.menu_restaurar: {
                mostrarDialogRestaurarDados();
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
                    mostrarDialogExportar();
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

    private void mostrarDialogBackup() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Backup")
                .setMessage("Este procedimento ira realizar backup dos dados.\nContinuar?")
                .setPositiveButton("Prosseguir", new DialogInterface.OnClickListener() {

                    String caminhoArquivo = null;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            caminhoArquivo = new FileService().realizarBackup(MainActivity.this);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Erro ao realizar o Backup", Toast.LENGTH_LONG).show();
                        }

                        Toast.makeText(MainActivity.this,
                                "Backup Realizado com Sucesso!\nSalvo em: " + caminhoArquivo,
                                Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogRestaurarDados() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Importar Dados")
                .setMessage("Este procedimento ira apagar todos os dados atualmente gravados\n Continuar?")
                .setPositiveButton("Prosseguir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("application/octet-stream");

                        if(intent.resolveActivity(getPackageManager()) != null){
                            startActivityForResult(Intent.createChooser(intent, "Selecione o Arquivo"), LOAD_FILE);
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_FILE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mostrarDialogExportar();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

        }
    }

    private void mostrarDialogExportar() {
        View view = getLayoutInflater().inflate(R.layout.view_exportar_dados_dialog, null);
        final TextView nomeArquivo = view.findViewById(R.id.nome_arquivo);
        final RadioGroup formatos = view.findViewById(R.id.radioGroup);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Exportar dados")
                .setView(view)
                .setPositiveButton("EXPORTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileService util = new FileService();
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
                            util.exportarDados(arquivo, ext, MainActivity.this);
                            Toast.makeText(MainActivity.this, "Dados exportados realizado com Sucesso! \n" + arquivo, Toast.LENGTH_LONG).show();
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
                boolean result = new FileService().restaurarDados(MainActivity.this, dataUri);
                if(result){
                    Toast.makeText(this, "Dados restaurados com Sucesso!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
