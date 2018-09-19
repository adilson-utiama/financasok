package com.asuprojects.walletok.helper;

import com.asuprojects.walletok.model.enums.CategoriaDespesa;

public class CategoriaUtil {

    public static CategoriaDespesa getCategoriaFrom(String valorString){
        CategoriaDespesa[] values = CategoriaDespesa.values();
        for (CategoriaDespesa cat : values){
            if (cat.getDescricao().equals(valorString)){
                return cat;
            }
        }
        return CategoriaDespesa.SEM_CATEGORIA;
    }
}
