package com.asuprojects.walletok.model.enums;

public enum CategoriaDespesa {

    ALIMENTACAO("Alimentação"),
    COMPRAS("Compras"),
    CONTAS("Contas"),
    EDUCACAO("Educação"),
    LAZER("Lazer"),
    SAUDE("Saúde"),
    TRANSPORTE("Trasnporte"),
    OUTROS("Outros"),
    INDEFINIDO("Indefinido");

    private String descricao;

    CategoriaDespesa(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static CategoriaDespesa toEnum(String value){
        for(CategoriaDespesa c : CategoriaDespesa.values()){
            if(c.getDescricao().equals(value)){
                return c;
            }
        }
        return INDEFINIDO;
    }
}
