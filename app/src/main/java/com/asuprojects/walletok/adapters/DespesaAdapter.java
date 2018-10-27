package com.asuprojects.walletok.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.model.Despesa;
import com.asuprojects.walletok.model.enums.Pagamento;

import java.util.List;

public class DespesaAdapter extends RecyclerView.Adapter {


    private List<Despesa> despesas;
    private Context ctx;

    public DespesaAdapter(Context ctx,  List<Despesa> despesas) {
        this.ctx = ctx;
        this.despesas = despesas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_despesa, parent, false);
        DespesaViewHolder viewHolder = new DespesaViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Despesa despesa = despesas.get(position);
        DespesaViewHolder despesaHolder = (DespesaViewHolder) holder;
        despesaHolder.setDescricao(despesa.getDescricao());
        String[] stringArray = ctx.getResources().getStringArray(R.array.categoria_despesas);
        despesaHolder.setCategoria(stringArray[despesa.getCategoriaDespesa().getCodigo()]);
        despesaHolder.setValor(despesa.getValorFormatado());
        despesaHolder.setData(despesa.getDataFormatada());
        if(despesa.getPagamento().equals(Pagamento.DINHEIRO)){
            despesaHolder.iconPagamento.setImageResource(R.drawable.ic_nota_dinheiro);
        }else if(despesa.getPagamento().equals(Pagamento.CARTAO)){
            despesaHolder.iconPagamento.setImageResource(R.drawable.ic_credit_card_black);
        }else{
            despesaHolder.iconPagamento.setImageResource(R.drawable.ic_pagamento_outros);
        }

    }

    @Override
    public int getItemCount() {
        return despesas != null ? despesas.size() : 0;
    }

    public void setDespesas(List<Despesa> lista){
        this.despesas = lista;
        notifyDataSetChanged();
    }


    public class DespesaViewHolder extends RecyclerView.ViewHolder{

        private TextView descricao;
        private TextView categoria;
        private TextView valor;
        private TextView data;
        private ImageView iconPagamento;

        public DespesaViewHolder(View itemView) {
            super(itemView);
            descricao = itemView.findViewById(R.id.despesa_descricao);
            categoria = itemView.findViewById(R.id.despesa_categoria);
            valor = itemView.findViewById(R.id.despesa_valor);
            data = itemView.findViewById(R.id.despesa_data);
            iconPagamento = itemView.findViewById(R.id.icon_pag);
        }

        public TextView getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao.setText(descricao);
        }

        public TextView getCategoria() {
            return categoria;
        }

        public void setCategoria(String categoria) {
            this.categoria.setText(categoria);
        }

        public TextView getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor.setText(valor);
        }

        public TextView getData() {
            return data;
        }

        public void setData(String data) {
            this.data.setText(data);
        }

        public ImageView getIconPagamento() {
            return iconPagamento;
        }

        public void setIconPagamento(ImageView iconPagamento) {
            this.iconPagamento = iconPagamento;
        }
    }
}
