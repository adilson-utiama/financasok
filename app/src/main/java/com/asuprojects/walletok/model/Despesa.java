package com.asuprojects.walletok.model;

import com.asuprojects.walletok.model.enums.CategoriaDespesa;
import com.asuprojects.walletok.model.enums.Pagamento;
import com.asuprojects.walletok.util.BigDecimalConverter;
import com.asuprojects.walletok.util.CalendarConverter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

public class Despesa implements Serializable {

    private long _id;
    private String descricao;
    private CategoriaDespesa categoriaDespesa;
    private BigDecimal valor;
    private Calendar data;
    private Pagamento pagamento;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public CategoriaDespesa getCategoriaDespesa() {
        return categoriaDespesa;
    }

    public void setCategoriaDespesa(CategoriaDespesa categoriaDespesa) {
        this.categoriaDespesa = categoriaDespesa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getValorFormatado(){
        return BigDecimalConverter.toStringFormatado(getValor());
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Calendar getData() {
        return data;
    }

    public String getDataFormatada(){
        return CalendarConverter.toStringFormatadaBR(getData());
    }

    public void setData(Calendar data) {
        this.data = data;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ID: ").append(get_id()).append(" DESCRICAO: ").append(getDescricao())
                .append(" DATA: ").append(getDataFormatada())
                .append(" CATEGORIA: ").append(getCategoriaDespesa().getDescricao())
                .append(" VALOR: ").append(getValorFormatado());
        return builder.toString();
    }
}
