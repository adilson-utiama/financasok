package com.asuprojects.walletok.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.asuprojects.walletok.R;
import com.asuprojects.walletok.model.Receita;
import com.asuprojects.walletok.util.BigDecimalConverter;
import com.asuprojects.walletok.util.CalendarConverter;
import com.asuprojects.walletok.util.CalendarUtil;

import java.util.List;

public class ReceitaAdapter extends RecyclerView.Adapter {

    private List<Receita> receitas;

    public ReceitaAdapter(List<Receita> receitas){
        this.receitas = receitas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receita, parent, false);
        ReceitaViewHolder viewHolder = new ReceitaViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Receita receita = receitas.get(position);
        ReceitaViewHolder receitaHolder = (ReceitaViewHolder) holder;
        receitaHolder.setDescricao(receita.getDescricao());
        receitaHolder.setCategoria(receita.getCategoriaReceita().getDescricao());
        receitaHolder.setData(CalendarUtil.toStringFormatadaPelaRegiao(receita.getData()));
        receitaHolder.setValor(BigDecimalConverter.toStringFormatado(receita.getValor()));
    }

    @Override
    public int getItemCount() {
        return (receitas.size() == 0) ? 0 : receitas.size();
    }

    public void setReceitas(List<Receita> lista){
        this.receitas = lista;
        notifyDataSetChanged();
    }

    public class ReceitaViewHolder extends RecyclerView.ViewHolder{

        private TextView descricao;
        private TextView categoria;
        private TextView data;
        private TextView valor;

        public ReceitaViewHolder(View itemView) {
            super(itemView);

            descricao = itemView.findViewById(R.id.receita_descricao);
            categoria = itemView.findViewById(R.id.receita_categoria);
            data = itemView.findViewById(R.id.receita_data);
            valor = itemView.findViewById(R.id.receita_valor);
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

        public TextView getData() {
            return data;
        }

        public void setData(String data) {
            this.data.setText(data);
        }

        public TextView getValor() {
            return valor;
        }

        public void setValor(String valor) {
            this.valor.setText(valor);
        }
    }
}
