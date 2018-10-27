package com.asuprojects.walletok.model.enums;

public enum CategoriaReceita {

    SALARIO(0,"Salario"),
    PRESENTE(1,"Presente"),
    PREMIO(2,"Premio"),
    OUTROS(3,"Outros"),
    INDEFINIDO(4,"Indefinido");

    private String descricao;
    private int codigo;

    CategoriaReceita(int codigo, String descricao){
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
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
        return INDEFINIDO;
    }

    public static CategoriaReceita toEnum(Integer cod){
        for(CategoriaReceita c : CategoriaReceita.values()){
            if(c.getCodigo() == cod){
                return c;
            }
        }
        return INDEFINIDO;
    }
}
