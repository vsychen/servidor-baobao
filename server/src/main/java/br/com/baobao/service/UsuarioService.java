package br.com.baobao.service;

import br.com.baobao.exception.UsuarioNaoEncontradoException;
import br.com.baobao.exception.UsuarioDuplicadoException;
import br.com.baobao.model.Funcionario;
import br.com.baobao.model.Papel;
import br.com.baobao.model.Usuario;
import br.com.baobao.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço de negócio para operações com usuários.
 * Implementa UserDetailsService para integração com Spring Security.
 */
@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== SPRING SECURITY ====================

    /**
     * Carrega um usuário pelo username, email ou telefone.
     * Implementa UserDetailsService para autenticação.
     */
    @Override
    public UserDetails loadUserByUsername(String credencial) throws UsernameNotFoundException {
        return buscarPorCredencial(credencial)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + credencial));
    }

    // ==================== BUSCA ====================

    /**
     * Busca um usuário por username, email ou telefone.
     */
    public Optional<Usuario> buscarPorCredencial(String credencial) {
        return usuarioRepository.findByUsuario(credencial)
                .or(() -> usuarioRepository.findByEmail(credencial))
                .or(() -> usuarioRepository.findByTelefone(credencial));
    }

    /**
     * Busca um usuário por ID.
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // ==================== CRIAÇÃO ====================

    /**
     * Cria um novo usuário após validação de duplicatas.
     */
    public Usuario criarUsuario(Usuario usuario) {
        validarDuplicatas(usuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    // ==================== ATUALIZAÇÃO ====================

    /**
     * Atualiza dados básicos de um usuário (nome e status ativo).
     * TODO
     */
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = obterUsuarioOuLancarExcecao(id);

        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setAtivo(usuarioAtualizado.getAtivo());

        return usuarioRepository.save(usuario);
    }

    /**
     * Altera o papel de um funcionário.
     * Apenas EMPLOYEE e ADMIN são permitidos.
     */
    public Usuario alterarPapelFuncionario(Long id, Papel novoPapel) {
        Funcionario funcionario = usuarioRepository.findById(id)
                .filter(u -> u instanceof Funcionario)
                .map(u -> (Funcionario) u)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Funcionário não encontrado com ID: " + id));

        funcionario.setPapel(novoPapel);
        return usuarioRepository.save(funcionario);
    }

    // ==================== DELEÇÃO ====================

    /**
     * Deleta um usuário por ID.
     */
    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Obtém um usuário por ID ou lança exceção se não encontrado.
     */
    private Usuario obterUsuarioOuLancarExcecao(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }

    /**
     * Valida se email, username ou telefone já estão cadastrados.
     */
    private void validarDuplicatas(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new UsuarioDuplicadoException("Email já cadastrado: " + usuario.getEmail());
        }

        if (usuarioRepository.existsByUsuario(usuario.getUsuario())) {
            throw new UsuarioDuplicadoException("Username já cadastrado: " + usuario.getUsuario());
        }

        if (usuarioRepository.existsByTelefone(usuario.getTelefone())) {
            throw new UsuarioDuplicadoException("Telefone já cadastrado: " + usuario.getTelefone());
        }
    }
}