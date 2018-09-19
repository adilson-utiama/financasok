package com.asuprojects.walletok.model.enums;

public enum CategoriaDespesa {

    SUPERMERCADO("Supermercado"),
    LANCHES("Lanches"),
    CONTAS("Contas"),
    REMEDIOS("Remedios"),
    SEGURANCA("Segurança"),
    TRANSPORTE("Transporte"),
    CURSOS("Cursos"),
    UTILITARIOS("Utilitarios"),
    ELETRONICOS("Eletronicos"),
    SEM_CATEGORIA("Outros");

    private String descricao;

    CategoriaDespesa(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
