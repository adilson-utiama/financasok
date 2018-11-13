package com.asuprojects.walletok.model;

import com.asuprojects.walletok.util.BigDecimalConverter;

import java.io.Serializable;
import java.math.BigDecimal;

public class CartaoCredito implements Serializable{

    private long id;
    private int diaVencimentoFatura;
    private String emissor;
    private String bandeira;
    private BigDecimal limiteCredito;

    public CartaoCredito() {
    }

    public CartaoCredito(int diaVencimentoFatura, String emissor, String bandeira, BigDecimal limiteCredito) {
        this.diaVencimentoFatura = diaVencimentoFatura;
        this.emissor = emissor;
        this.bandeira = bandeira;
        this.limiteCredito = limiteCredito;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDiaVencimentoFatura() {
        return diaVencimentoFatura;
    }

    public void setDiaVencimentoFatura(int diaVencimentoFatura) {
        this.diaVencimentoFatura = diaVencimentoFatura;
    }

    public String getEmissor() {
        return emissor;
    }

    public void setEmissor(String emissor) {
        this.emissor = emissor;
    }

    public String getBandeira() {
        return bandeira;
    }

    public void setBandeira(String bandeira) {
        this.bandeira = bandeira;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ID: ").append(getId())
                .append("\nBandeira: ").append(getBandeira())
                .append("\nEmissor: ").append(getEmissor())
                .append("\nDia do Vencimento da Fatura: ").append(getDiaVencimentoFatura())
                .append("\nLimite Credito").append(BigDecimalConverter.toStringFormatado(getLimiteCredito()));
        return builder.toString();
    }
}
