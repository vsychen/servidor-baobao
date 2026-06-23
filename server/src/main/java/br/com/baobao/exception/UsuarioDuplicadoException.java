package br.com.baobao.exception;

/**
 * Exceção lançada quando um usuário duplicado é detectado.
 */
public class UsuarioDuplicadoException extends RuntimeException {
    public UsuarioDuplicadoException(String mensagem) {
        super(mensagem);
    }
}