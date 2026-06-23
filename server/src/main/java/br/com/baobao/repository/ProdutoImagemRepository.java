package br.com.baobao.repository;

import br.com.baobao.model.ProdutoImagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações com imagens de produtos.
 * Implementa soft delete via data_delecaoo.
 */
public interface ProdutoImagemRepository extends JpaRepository<ProdutoImagem, Long> {

    /**
     * Busca a imagem ativa (não deletada) de um produto.
     */
    @Query("SELECT pi FROM ProdutoImagem pi WHERE pi.produto.id = :produtoId AND pi.dataDelecao IS NULL")
    Optional<ProdutoImagem> findImagemAtivaByProdutoId(@Param("produtoId") Long produtoId);

    /**
     * Busca imagens órfãs (deletadas há mais de X dias) para limpeza.
     */
    @Query("SELECT pi FROM ProdutoImagem pi WHERE pi.dataDelecao IS NOT NULL AND pi.dataDelecao < :data")
    List<ProdutoImagem> findImagensDeletadasAntigas(@Param("data") LocalDateTime data);
}