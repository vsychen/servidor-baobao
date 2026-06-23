package br.com.baobao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Representa uma imagem de produto armazenada no banco de dados.
 *
 * Implementa soft delete via 'data_deleção'.
 * No máximo, uma imagem por produto deve estar ativa (data_deleção = null).
 */
@Entity
@Table(name = "produto_imagem")
public class ProdutoImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    @JsonIgnore
    private Produto produto;

    @Column(columnDefinition = "BYTEA", nullable = false)
    private byte[] imagem;

    @Column(nullable = false, length = 50)
    private String tipoMime; // image/png, image/jpeg, etc

    @Column(nullable = false)
    private Long tamanho; // em bytes

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_delecao")
    private LocalDateTime dataDelecao; // soft delete

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    // ==================== CONSTRUTORES ====================

    public ProdutoImagem() {
    }

    /**
     * Construtor para criar uma nova imagem de produto.
     */
    public ProdutoImagem(Produto produto, byte[] imagem, String tipoMime) {
        this.produto = produto;
        this.imagem = imagem;
        this.tipoMime = tipoMime;
        this.tamanho = (long) imagem.length;
    }

    // ==================== GETTERS E SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
        this.tamanho = imagem != null ? (long) imagem.length : null;
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataDelecao() {
        return dataDelecao;
    }

    public void setDataDelecao(LocalDateTime dataDelecao) {
        this.dataDelecao = dataDelecao;
    }

    // ==================== MÉTODOS DE NEGÓCIO ====================

    /**
     * Verifica se a imagem está ativa (não foi deletada).
     */
    public boolean isAtivo() {
        return dataDelecao == null;
    }

    /**
     * Marca a imagem como deletada (soft delete).
     */
    public void deletar() {
        this.dataDelecao = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "ProdutoImagem{" +
                "id=" + id +
                ", tipoMime='" + tipoMime + '\'' +
                ", tamanho=" + tamanho +
                ", ativo=" + isAtivo() +
                ", dataCriacao=" + dataCriacao +
                ", dataDelecao=" + dataDelecao +
                '}';
    }
}