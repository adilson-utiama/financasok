package com.asuprojects.walletok.model.enums;

public enum Pagamento {

    DINHEIRO("Dinheiro"),
        CARTAO("Cartao"),
            OUTROS("Outros");

    private String desc;

    Pagamento(String desc){
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static Pagamento toEnum(String value){
        for(Pagamento p : Pagamento.values()){
            if(p.getDesc().equals(value)){
                return p;
            }
        }
        return OUTROS;
    }
}
