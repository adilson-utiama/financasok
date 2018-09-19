package com.asuprojects.walletok.model;

public class Usuario {

    private long _id;
    private String usuario;
    private String senha;
    private String pergunta;
    private String resposta;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ID: ").append(get_id())
                .append(" Usuario: ").append(getUsuario())
                .append(" SENHA: ").append(getSenha())
                .append(" PERGUNTA_SECRETA: ").append(getPergunta())
                .append(" RESPOSTA: ").append(getResposta());
        return builder.toString();
    }
}
