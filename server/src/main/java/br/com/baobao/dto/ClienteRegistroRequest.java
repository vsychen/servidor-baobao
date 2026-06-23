package br.com.baobao.dto;

/**
 * DTO para registro de cliente.
 */
public class ClienteRegistroRequest {

    private String email;
    private String usuario;
    private String telefone;
    private String senha;
    private String nome;

    public ClienteRegistroRequest() {
    }

    public ClienteRegistroRequest(String email, String usuario, String telefone, String senha, String nome) {
        this.email = email;
        this.usuario = usuario;
        this.telefone = telefone;
        this.senha = senha;
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}