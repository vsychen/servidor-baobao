package br.com.baobao.exception;

/**
 * Exceção lançada quando um produto não é encontrado.
 */
public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}