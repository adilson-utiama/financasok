package com.asuprojects.walletok.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.model.Tipo;

public class ListaVaziaFragment extends Fragment {

    private ImageView imageView;
    private TextView texto;

    private Tipo tipo = Tipo.NENHUM;

    public ListaVaziaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_vazia, container, false);

        imageView = view.findViewById(R.id.iconListaVazia);
        texto = view.findViewById(R.id.textview_listaVazia);

        switch(tipo.getCodigo()){
            case 1:
                imageView.setImageResource(R.drawable.ic_unhappy);
                texto.setText("Nenhuma Receita");
                break;
            case 2:
                imageView.setImageResource(R.drawable.ic_happy);
                texto.setText("Nenhuma Despesa");
                break;
            case 3:
                imageView.setImageResource(R.drawable.ic_sem_registro);
                texto.setText("Sem registro de dados");
                break;
            default:
                imageView.setImageResource(R.drawable.ic_sem_registro);
                texto.setText("Vazio");
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
