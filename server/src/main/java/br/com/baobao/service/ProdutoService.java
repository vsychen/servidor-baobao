package br.com.baobao.service;

import br.com.baobao.exception.ProdutoNaoEncontradoException;
import br.com.baobao.exception.ImagemInvalidaException;
import br.com.baobao.model.Produto;
import br.com.baobao.model.ProdutoImagem;
import br.com.baobao.repository.ProdutoImagemRepository;
import br.com.baobao.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de negócio para operações com produtos e imagens.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoImagemRepository imagemRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ProdutoImagemRepository imagemRepository) {
        this.produtoRepository = produtoRepository;
        this.imagemRepository = imagemRepository;
    }

    // ==================== CRUD PRODUTO ====================

    /**
     * Cria um novo produto após validação.
     */
    public Produto criar(Produto produto) {
        validarProduto(produto);
        return produtoRepository.save(produto);
    }

    /**
     * Busca um produto por ID.
     */
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    /**
     * Lista todos os produtos.
     */
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    /**
     * Lista produtos por categoria.
     */
    public List<Produto> listarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    /**
     * Busca produtos por nome (case-insensitive).
     */
    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeIgnoreCase(nome);
    }

    /**
     * Atualiza um produto existente.
     */
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        validarProduto(produtoAtualizado);
        produtoAtualizado.setId(id);
        return produtoRepository.save(produtoAtualizado);
    }

    /**
     * Diminui a quantidade de um produto em estoque.
     * Valida se há quantidade suficiente.
     */
    public Produto diminuirQuantidade(Long id, Integer quantidadeComprada) {
        Produto produto = obterProdutoOuLancarExcecao(id);

        if (quantidadeComprada <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        if (produto.getQuantidade() < quantidadeComprada) {
            throw new IllegalArgumentException("Quantidade insuficiente em estoque");
        }

        produto.setQuantidade(produto.getQuantidade() - quantidadeComprada);
        return produtoRepository.save(produto);
    }

    /**
     * Deleta um produto por ID.
     */
    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    // ==================== OPERAÇÕES COM IMAGEM ====================

    /**
     * Adiciona uma nova imagem ao produto.
     * Se já existe imagem ativa, faz soft delete da antiga.
     */
    public void adicionarImagem(Long produtoId, MultipartFile arquivo) throws IOException {
        Produto produto = obterProdutoOuLancarExcecao(produtoId);
        validarArquivo(arquivo);

        // Soft delete da imagem anterior (se existir)
        deletarImagem(produto);

        // Criar e salvar nova imagem
        ProdutoImagem novaImagem = new ProdutoImagem(
                produto,
                arquivo.getBytes(),
                arquivo.getContentType()
        );

        produto.getImagens().add(novaImagem);
        produtoRepository.save(produto);
    }

    /**
     * Obtém a imagem ativa de um produto.
     */
    public Optional<ProdutoImagem> obterImagemAtiva(Long produtoId) {
        return imagemRepository.findImagemAtivaByProdutoId(produtoId);
    }

    /**
     * Deleta a imagem ativa de um produto (soft delete).
     */
    public void deletarImagem(Long produtoId) {
        Produto produto = obterProdutoOuLancarExcecao(produtoId);
        deletarImagem(produto);
    }

    /**
     * Limpa imagens órfãs (deletadas há mais de 7 dias).
     * Deve ser executado via @Scheduled.
     */
    public void limparImagensOrfas() {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(7);
        List<ProdutoImagem> orfas = imagemRepository.findImagensDeletadasAntigas(dataLimite);
        imagemRepository.deleteAll(orfas);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Marca a imagem ativa de um produto como deletada (soft delete).
     */
    private void deletarImagem(Produto produto) {
        ProdutoImagem imagemAtiva = produto.getImagemAtiva();
        if (imagemAtiva != null) {
            imagemAtiva.deletar();
            imagemRepository.save(imagemAtiva);
        }
    }

    /**
     * Obtém um produto por ID ou lança exceção se não encontrado.
     */
    private Produto obterProdutoOuLancarExcecao(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto não encontrado com ID: " + id));
    }

    /**
     * Valida os dados básicos de um produto.
     */
    private void validarProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode estar vazio");
        }

        if (produto.getPreco() == null || produto.getPreco().signum() < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }

        if (produto.getQuantidade() == null || produto.getQuantidade() < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }

        if (produto.getCategoria() == null || produto.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("Categoria não pode estar vazia");
        }
    }

    /**
     * Valida arquivo de imagem.
     * Verifica tipo MIME, tamanho máximo e extensão.
     */
    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ImagemInvalidaException("Arquivo não pode estar vazio");
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ImagemInvalidaException("Arquivo deve ser uma imagem válida");
        }

        // Validar extensão
        String filename = arquivo.getOriginalFilename();
        if (filename == null || !isExtensaoValida(filename)) {
            throw new ImagemInvalidaException("Extensão de arquivo não permitida. Use: jpg, jpeg, png, gif");
        }

        // Validar tamanho (máximo 5MB)
        long maxSize = 5 * 1024 * 1024;
        if (arquivo.getSize() > maxSize) {
            throw new ImagemInvalidaException("Imagem não pode exceder 5MB");
        }
    }

    /**
     * Verifica se a extensão do arquivo é válida.
     */
    private boolean isExtensaoValida(String filename) {
        String[] extensoesValidas = {"jpg", "jpeg", "png", "gif"};
        String extensao = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        for (String ext : extensoesValidas) {
            if (ext.equals(extensao)) {
                return true;
            }
        }
        return false;
    }
}