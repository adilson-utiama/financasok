package com.asuprojects.walletok.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.asuprojects.walletok.database.BancoSQLite3;
import com.asuprojects.walletok.database.TabelaUsuario;
import com.asuprojects.walletok.model.Usuario;

public class UsuarioDAO {

    private BancoSQLite3 banco;

    public UsuarioDAO(Context context) {
        this.banco = new BancoSQLite3(context);
    }

    public long insert(Usuario usuario){
        SQLiteDatabase db = banco.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TabelaUsuario.COLUNA_USUARIO, usuario.getUsuario());
        values.put(TabelaUsuario.COLUNA_SENHA, usuario.getSenha());
        values.put(TabelaUsuario.COLUNA_PERGUNTA, usuario.getPergunta());
        values.put(TabelaUsuario.COLUNA_RESPOSTA, usuario.getResposta());

        long idSalvo = db.insertOrThrow(TabelaUsuario.NOME_TABELA, null, values);
        return idSalvo;

    }

    public Usuario findBy(String usuarioLogin){
        SQLiteDatabase db = banco.getReadableDatabase();

        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ")
                .append(TabelaUsuario.NOME_TABELA)
                .append(" WHERE login = ? ;");

        Cursor cursor = db.rawQuery(query.toString(), new String[]{usuarioLogin});
        boolean first = cursor.moveToFirst();
        Usuario usuario = new Usuario();
        if(first){
            if(cursor != null){
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(TabelaUsuario.COLUNA_ID));
                String login = cursor.getString(cursor.getColumnIndexOrThrow(TabelaUsuario.COLUNA_USUARIO));
                String senha = cursor.getString(cursor.getColumnIndexOrThrow(TabelaUsuario.COLUNA_SENHA));
                String pergunta = cursor.getString(cursor.getColumnIndexOrThrow(TabelaUsuario.COLUNA_PERGUNTA));
                String resposta = cursor.getString(cursor.getColumnIndexOrThrow(TabelaUsuario.COLUNA_RESPOSTA));

                usuario.set_id(id);
                usuario.setUsuario(login);
                usuario.setSenha(senha);
                usuario.setPergunta(pergunta);
                usuario.setResposta(resposta);
            }
        } else {
            cursor.close();
            return null;
        }

        cursor.close();
        return usuario;
    }
}
