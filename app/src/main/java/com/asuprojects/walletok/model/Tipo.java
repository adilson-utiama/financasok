package com.asuprojects.walletok.model;

public enum Tipo {

    RECEITA(1, "Receita"),
    DESPESA(2, "Despesa"),
    GRAFICO(3, "Grafico"),
    NENHUM(4, "Nenhum");

    private int codigo;
    private String descricao;

    Tipo(int codigo, String descricao){
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static Tipo toEnum(Integer cod){
        for (Tipo t : Tipo.values()) {
            if(t.getCodigo() == cod){
                return t;
            }
        }
        throw new IllegalArgumentException("Codigo inválido: " + cod);
    }
}
