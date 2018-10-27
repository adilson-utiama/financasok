package com.asuprojects.walletok.model.enums;

import com.asuprojects.walletok.R;

public enum CategoriaDespesa {

    ALIMENTACAO(0,"Alimentação"),
    COMPRAS(1,"Compras"),
    CONTAS(2,"Contas"),
    EDUCACAO(3,"Educação"),
    LAZER(4,"Lazer"),
    SAUDE(5,"Saúde"),
    TRANSPORTE(6,"Transporte"),
    OUTROS(7,"Outros"),
    INDEFINIDO(8,"Indefinido");

    private int codigo;
    private String descricao;

    CategoriaDespesa(int codigo, String descricao){
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
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

    public static CategoriaDespesa toEnum(Integer cod){
        for(CategoriaDespesa c : CategoriaDespesa.values()){
            if(c.getCodigo() == cod){
                return c;
            }
        }
        return INDEFINIDO;
    }
}
