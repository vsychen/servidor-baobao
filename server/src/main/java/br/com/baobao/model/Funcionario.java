package br.com.baobao.model;

import jakarta.persistence.*;

/**
 * Representa um funcionário do sistema.
 * Pode ter papel EMPLOYEE ou ADMIN.
 */
@Entity
@Table(name = "funcionario")
@DiscriminatorValue("FUNCIONARIO")
public class Funcionario extends Usuario {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel = Papel.EMPLOYEE;

    public Funcionario() {
    }

    public Funcionario(String email, String usuario, String telefone, String senha, String nome) {
        super(email, usuario, telefone, senha, nome);
    }

    public Funcionario(String email, String usuario, String telefone, String senha, String nome, Papel papel) {
        super(email, usuario, telefone, senha, nome);
        setPapel(papel);
    }

    @Override
    public Papel obterPapel() {
        return papel;
    }

    /**
     * Define o papel do funcionário com validação.
     * Apenas EMPLOYEE e ADMIN são permitidos.
     */
    public void setPapel(Papel papel) {
        if (papel == null || (papel != Papel.EMPLOYEE && papel != Papel.ADMIN)) {
            throw new IllegalArgumentException("Funcionário deve ter papel EMPLOYEE ou ADMIN");
        }
        this.papel = papel;
    }

    public Papel getPapel() {
        return papel;
    }
}