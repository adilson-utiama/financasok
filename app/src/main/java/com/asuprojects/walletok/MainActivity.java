package com.asuprojects.walletok;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
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
import android.support.v7.widget.LinearLayoutCompat;
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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import net.rdrei.android.dirchooser.DirectoryChooserActivity;
import net.rdrei.android.dirchooser.DirectoryChooserConfig;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final int LOAD_FILE = 200;
    private static final int WRITE_FILE = 100;
    private static final int ADD_RECEITA = 1000;
    private static final int ADD_DESPESA = 2000;
    public static final String TAB_NUMBER = "TAB_NUMBER";
    private static final int REQUEST_DIRECTORY = 300;

    private int tabAtual = 0;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private LinearLayoutCompat container;
    private AdView mAdView;
    private TabAdapter abasAdapter;
    private ViewPager viewPager;

    private String caminhoArquivo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configuraToolbar();

        container = findViewById(R.id.linearLayoutCompat);
        configuraAdView();
        if(isConected()){
            mostraAnuncioAdView();
        }else{
            container.removeView(mAdView);
        }
        configuraNavigationDrawer();
        montaEstruturaDeAbas();
        configuraFabButton();

        Intent intent = getIntent();
        if(intent.hasExtra(TAB_NUMBER)){
            int tab_number = intent.getIntExtra(TAB_NUMBER, 0);
            viewPager.setCurrentItem(tab_number);
        }
    }

    private void configuraFabButton() {
        final FloatingActionMenu fabMenu =  findViewById(R.id.fab_menu);
        fabMenu.setClosedOnTouchOutside(true);
        FloatingActionButton fabReceita = findViewById(R.id.fab_adiciona_receita);
        fabReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReceitaActivity.class);
                startActivityForResult(intent, ADD_RECEITA);
                fabMenu.close(true);
            }
        });
        FloatingActionButton fabDespesa = findViewById(R.id.fab_adiciona_despesa);
        fabDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DespesaActivity.class);
                startActivityForResult(intent, ADD_DESPESA);
                fabMenu.close(true);
            }
        });
    }

    private void montaEstruturaDeAbas() {
        abasAdapter = new TabAdapter(getSupportFragmentManager());
        abasAdapter.adicionar(new DespesasFragment(), getString(R.string.aba_titulo_despesas));
        abasAdapter.adicionar(new ReceitasFragment(), getString(R.string.aba_titulo_receitas));
        abasAdapter.adicionar(new ResumoFragment(), getString(R.string.aba_titulo_resumo));

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(abasAdapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager, true);

        viewPager.setCurrentItem(tabAtual);

    }

    private void configuraNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this,
                        drawerLayout, toolbar,
                        R.string.open_drawer, R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void configuraToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            if(!isConected()){
                container.removeView(mAdView);
            } else {
                container.removeView(mAdView);
                mostraAnuncioAdView();
            }
            mAdView.pause();
        }
        super.onPause();
    }
    @Override
    public void onResume() {
        if (mAdView != null) {
            if(!isConected()){
                container.removeView(mAdView);
            } else {
                container.removeView(mAdView);
                mostraAnuncioAdView();
            }
            mAdView.resume();
        }
        super.onResume();
    }

    private void mostraAnuncioAdView() {
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        container.addView(mAdView, params);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void configuraAdView() {
        MobileAds.initialize(this,
                getString(R.string.admob_app_id));
        mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(getString(R.string.admob_banner_block_id));
    }

    private boolean isConected(){
        ConnectivityManager conmag = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if ( conmag != null ) {
            conmag.getActiveNetworkInfo();
            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                return true;
            }
            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                return true;
            }
        }
        return false;
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
        dialogSair.setMessage(R.string.msg_sair_do_app)
                .setPositiveButton(getString(R.string.opcao_sim), new DialogInterface.OnClickListener() {
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
                .setNegativeButton(getString(R.string.opcao_nao), null)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
        if (mAdView != null) {
            container.removeView(mAdView);
            mAdView.destroy();
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
        dialog.setTitle(R.string.dialog_titulo_backup)
                .setMessage(getString(R.string.dialog_msg_backup))
                .setPositiveButton(getString(R.string.opcao_prosseguir), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            final Intent chooserIntent = new Intent(MainActivity.this, DirectoryChooserActivity.class);
                            final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                                    .newDirectoryName("Backup_App")
                                    .allowReadOnlyDirectory(true)
                                    .allowNewDirectoryNameModification(true)
                                    .build();

                            chooserIntent.putExtra(DirectoryChooserActivity.EXTRA_CONFIG, config);
                            startActivityForResult(chooserIntent, REQUEST_DIRECTORY);
                    }
                })
                .setNegativeButton(getString(R.string.opcao_cancelar), null)
                .show();
    }

    private void mostrarDialogRestaurarDados() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_titulo_restaurar)
                .setMessage(getString(R.string.dialog_msg_restaurar))
                .setPositiveButton(getString(R.string.opcao_prosseguir), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("application/octet-stream");
                        if(intent.resolveActivity(getPackageManager()) != null){
                            startActivityForResult(Intent.createChooser(intent, getString(R.string.intent_chooser_sel_file)), LOAD_FILE);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.opcao_cancelar), null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_FILE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mostrarDialogExportar();
                }
            }

        }
    }

    private void mostrarDialogExportar() {
        View view = getLayoutInflater().inflate(R.layout.view_exportar_dados_dialog, null);
        final TextView nomeArquivo = view.findViewById(R.id.nome_arquivo);
        final RadioGroup formatos = view.findViewById(R.id.radioGroup);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_titulo_exportar)
                .setView(view)
                .setPositiveButton(getString(R.string.opcao_exportar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileService util = new FileService(MainActivity.this);
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
                            util.exportarDados(arquivo, ext);
                            Toast.makeText(MainActivity.this, getString(R.string.msg_sucesso_exportar), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                      }
                })
                .setNegativeButton(getString(R.string.opcao_cancelar), null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("URI", "onActivityResult: Recebendo resultado..." );
        if(resultCode == RESULT_OK){
            if(requestCode == LOAD_FILE){
                Uri dataUri = data.getData();
                boolean result = new FileService(MainActivity.this).restaurarDados(dataUri);
                if(result){
                    Toast.makeText(this, getString(R.string.msg_sucesso_restaurar), Toast.LENGTH_SHORT).show();
                }
            }
        }

        if(requestCode == REQUEST_DIRECTORY){
            if (resultCode == DirectoryChooserActivity.RESULT_CODE_DIR_SELECTED) {
                String caminho = data.getStringExtra(DirectoryChooserActivity.RESULT_SELECTED_DIR);
                try {
                    caminhoArquivo = new FileService(MainActivity.this).realizarBackup(caminho);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, R.string.msg_erro_backup, Toast.LENGTH_LONG).show();
                }
                Toast.makeText(MainActivity.this,
                        getString(R.string.msg_sucesso_backup) + caminhoArquivo,
                        Toast.LENGTH_LONG).show();

            } else {

            }

        }
    }
}
