package com.asuprojects.walletok;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.asuprojects.walletok.adapters.TabAdapter;
import com.asuprojects.walletok.fragments.DespesasFragment;
import com.asuprojects.walletok.fragments.ReceitasFragment;
import com.asuprojects.walletok.fragments.ResumoFragment;
import com.asuprojects.walletok.ui.ConfiguracoesActivity;
import com.asuprojects.walletok.ui.DespesaActivity;
import com.asuprojects.walletok.ui.ReceitaActivity;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{



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
        //TODO adicionar fragents aqui, pagerview
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

        FloatingActionButton fabCategoria = findViewById(R.id.fab_adiciona_categoria);
        /*fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DespesaActivity.class);
                startActivity(intent);
            }
        });*/

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_configuracoes) {
            startActivity(new Intent(this, ConfiguracoesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            case R.id.menu_configuracoes: {
                startActivity(new Intent(MainActivity.this, ConfiguracoesActivity.class));
                break;
            }
            case R.id.menu_backup: {
                Toast.makeText(this, "Menu Backup selecionado...", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.menu_sobre: {
                Toast.makeText(this, "Menu Sobre selecionado...", Toast.LENGTH_SHORT).show();
                break;
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
