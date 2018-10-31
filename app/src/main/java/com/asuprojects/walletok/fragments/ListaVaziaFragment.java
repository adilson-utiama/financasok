package com.asuprojects.walletok.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.model.Tipo;

public class ListaVaziaFragment extends Fragment {

    private Tipo tipo = Tipo.NENHUM;
    private LinearLayoutCompat listaVaziaContainer;

    public ListaVaziaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_vazia, container, false);

        listaVaziaContainer = view.findViewById(R.id.lista_vazia_container);
        listaVaziaContainer.setAlpha(0);
        listaVaziaContainer.animate().setDuration(600).alpha(1F).setListener(null);

        ImageView imageView = view.findViewById(R.id.iconListaVazia);
        TextView texto = view.findViewById(R.id.textview_listaVazia);

        switch(tipo.getCodigo()){
            case 1:
                imageView.setImageResource(R.drawable.ic_unhappy);
                texto.setText(R.string.lista_vazia_titulo_receita);
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_happy);
                texto.setText(R.string.lista_vazia_titulo_despesa);
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_sem_registro);
                texto.setText(R.string.lista_vazia_titulo_sem_registro);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_sem_registro);
                texto.setText(R.string.lista_vazia_titulo_vazio);
                break;
        }

        return view;
    }

    public void setTipo(Tipo tipoRecebido){
        this.tipo = tipoRecebido;
    }

    public Tipo getTipo() {
        return this.tipo;
    }
}
