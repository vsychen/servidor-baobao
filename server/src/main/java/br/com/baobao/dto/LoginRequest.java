package br.com.baobao.dto;

/**
 * DTO para requisição de login.
 */
public class LoginRequest {

    private String credencial; // username, email ou telefone
    private String senha;

    public LoginRequest() {
    }

    public LoginRequest(String credencial, String senha) {
        this.credencial = credencial;
        this.senha = senha;
    }

    public String getCredencial() {
        return credencial;
    }

    public void setCredencial(String credencial) {
        this.credencial = credencial;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}