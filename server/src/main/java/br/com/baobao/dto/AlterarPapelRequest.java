package br.com.baobao.dto;

import br.com.baobao.model.Papel;

/**
 * DTO para requisição de alteração de papel de funcionário.
 */
public class AlterarPapelRequest {

    private Papel papel;

    public AlterarPapelRequest() {
    }

    public AlterarPapelRequest(Papel papel) {
        this.papel = papel;
    }

    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }
}