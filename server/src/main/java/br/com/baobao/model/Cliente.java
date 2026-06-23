package br.com.baobao.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Representa um cliente do sistema.
 * Sempre possui o papel USER.
 */
@Entity
@Table(name = "cliente")
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    public Cliente() {
    }

    public Cliente(String email, String usuario, String telefone, String senha, String nome) {
        super(email, usuario, telefone, senha, nome);
    }

    @Override
    public Papel obterPapel() {
        return Papel.USER;
    }
}