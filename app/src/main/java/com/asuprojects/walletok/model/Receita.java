package com.asuprojects.walletok.model;

import com.asuprojects.walletok.model.enums.CategoriaReceita;
import com.asuprojects.walletok.util.CalendarConverter;
import com.asuprojects.walletok.util.CalendarUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

public class Receita implements Serializable {

    @Expose(serialize = false)
    private long _id;

    @SerializedName("description") @Expose
    private String descricao;

    @SerializedName("category") @Expose
    private CategoriaReceita categoriaReceita;

    @SerializedName("value") @Expose
    private BigDecimal valor;

    @SerializedName("date") @Expose
    private Calendar data;

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

    public CategoriaReceita getCategoriaReceita() {
        return categoriaReceita;
    }

    public void setCategoriaReceita(CategoriaReceita categoriaReceita) {
        this.categoriaReceita = categoriaReceita;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Calendar getData() {
        return data;
    }

    public void setData(Calendar data) {
        this.data = data;
    }

    public String getDataFormatada() {
        return CalendarUtil.toStringFormatadaPelaRegiao(getData());
    }
}
