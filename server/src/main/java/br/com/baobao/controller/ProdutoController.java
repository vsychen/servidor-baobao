package br.com.baobao.controller;

import br.com.baobao.dto.ComprarProdutoRequest;
import br.com.baobao.dto.ProdutoImagemResponse;
import br.com.baobao.exception.ProdutoNaoEncontradoException;
import br.com.baobao.exception.ImagemInvalidaException;
import br.com.baobao.model.Produto;
import br.com.baobao.model.ProdutoImagem;
import br.com.baobao.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller para operações com produtos e imagens.
 */
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // ==================== CRUD PRODUTO ====================

    /**
     * Cria um novo produto.
     * Requer role EMPLOYEE ou ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        try {
            Produto produtoCriado = produtoService.criar(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoCriado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Lista todos os produtos.
     */
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        List<Produto> produtos = produtoService.listarTodos();
        return ResponseEntity.ok(produtos);
    }

    /**
     * Busca um produto por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca produtos por categoria.
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Produto>> buscarPorCategoria(@PathVariable String categoria) {
        List<Produto> produtos = produtoService.listarPorCategoria(categoria);
        if (produtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(produtos);
    }

    /**
     * Busca produtos por nome (case-insensitive).
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Produto>> buscarPorNome(@RequestParam String nome) {
        List<Produto> produtos = produtoService.buscarPorNome(nome);
        if (produtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(produtos);
    }

    /**
     * Atualiza um produto existente.
     * Requer role EMPLOYEE ou ADMIN.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Produto> atualizar(
            @PathVariable Long id,
            @RequestBody Produto produto) {
        try {
            Produto produtoAtualizado = produtoService.atualizar(id, produto);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (ProdutoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Compra um produto (diminui quantidade em estoque).
     * Requer autenticação.
     */
    @PutMapping("/{id}/comprar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Produto> comprar(
            @PathVariable Long id,
            @RequestBody ComprarProdutoRequest request) {
        try {
            Produto produtoAtualizado = produtoService.diminuirQuantidade(id, request.getQuantidade());
            return ResponseEntity.ok(produtoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (ProdutoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deleta um produto.
     * Requer role ADMIN.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            produtoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (ProdutoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== OPERAÇÕES COM IMAGEM ====================

    /**
     * Adiciona uma nova imagem ao produto.
     * Se já existe imagem ativa, faz soft delete da antiga.
     * Requer role EMPLOYEE ou ADMIN.
     */
    @PostMapping("/{id}/imagem")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<String> adicionarImagem(
            @PathVariable Long id,
            @RequestParam("arquivo") MultipartFile arquivo) {
        try {
            produtoService.adicionarImagem(id, arquivo);
            return ResponseEntity.ok("Imagem adicionada com sucesso");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao processar imagem");
        } catch (ImagemInvalidaException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (ProdutoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtém a imagem ativa de um produto.
     */
    @GetMapping("/{id}/imagem")
    public ResponseEntity<?> obterImagem(@PathVariable Long id) {
        return produtoService.obterImagemAtiva(id)
                .map(imagem -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(imagem.getTipoMime()))
                        .body(imagem.getImagem()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deleta a imagem ativa de um produto (soft delete).
     * Requer role EMPLOYEE ou ADMIN.
     */
    @DeleteMapping("/{id}/imagem")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Void> deletarImagem(@PathVariable Long id) {
        try {
            produtoService.deletarImagem(id);
            return ResponseEntity.noContent().build();
        } catch (ProdutoNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }
}