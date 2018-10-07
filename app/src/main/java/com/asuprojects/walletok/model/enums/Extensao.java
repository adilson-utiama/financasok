package com.asuprojects.walletok.model.enums;

public enum Extensao {

    CSV(".csv"),
    JSON(".json");

    private String extensao;

    Extensao(String extensao){
        this.extensao = extensao;
    }

    public String getExtensao() {
        return extensao;
    }
}
