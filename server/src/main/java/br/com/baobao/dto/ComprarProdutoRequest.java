package br.com.baobao.dto;

/**
 * DTO para requisição de compra de produto.
 */
public class ComprarProdutoRequest {

    private Integer quantidade;

    public ComprarProdutoRequest() {
    }

    public ComprarProdutoRequest(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}