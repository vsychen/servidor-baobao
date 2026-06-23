package br.com.baobao.repository;

import br.com.baobao.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositório para operações com produtos.
 */
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    /**
     * Busca produtos por categoria.
     */
    List<Produto> findByCategoria(String categoria);

    /**
     * Busca produtos por nome (case-insensitive).
     */
    List<Produto> findByNomeIgnoreCase(String nome);
}