package br.com.baobao.exception;

/**
 * Exceção lançada quando uma imagem é inválida.
 */
public class ImagemInvalidaException extends RuntimeException {
    public ImagemInvalidaException(String mensagem) {
        super(mensagem);
    }
}