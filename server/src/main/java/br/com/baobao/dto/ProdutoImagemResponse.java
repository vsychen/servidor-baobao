package br.com.baobao.dto;

/**
 * DTO para resposta de imagem de produto.
 */
public class ProdutoImagemResponse {

    private Long id;
    private String tipoMime;
    private Long tamanho;
    private byte[] imagem;

    public ProdutoImagemResponse() {
    }

    public ProdutoImagemResponse(Long id, String tipoMime, Long tamanho, byte[] imagem) {
        this.id = id;
        this.tipoMime = tipoMime;
        this.tamanho = tamanho;
        this.imagem = imagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoMime() {
        return tipoMime;
    }

    public void setTipoMime(String tipoMime) {
        this.tipoMime = tipoMime;
    }

    public Long getTamanho() {
        return tamanho;
    }

    public void setTamanho(Long tamanho) {
        this.tamanho = tamanho;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }
}