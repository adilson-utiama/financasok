package com.asuprojects.walletok.model.enums;

public enum CategoriaReceita {

    SALARIO("Salario"),
    PRESENTE("Presente"),
    PREMIO("Premio"),
    OUTROS("Outros");

    private String descricao;

    CategoriaReceita(String descricao){
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static CategoriaReceita toEnum(String value){
        for(CategoriaReceita c : CategoriaReceita.values()){
            if(c.getDescricao().equals(value)){
                return c;
            }
        }
        throw new RuntimeException("Objeto nao encontrado");
    }
}
