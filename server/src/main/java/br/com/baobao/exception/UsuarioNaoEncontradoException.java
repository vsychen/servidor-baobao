package br.com.baobao.exception;

/**
 * Exceção lançada quando um usuário não é encontrado.
 */
public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}