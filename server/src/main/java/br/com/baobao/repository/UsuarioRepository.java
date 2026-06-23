package br.com.baobao.repository;

import br.com.baobao.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório para operações com usuários (Cliente e Funcionário).
 * Utiliza herança de tipo para trabalhar com a classe base Usuario.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByUsuario(String usuario);

    Optional<Usuario> findByTelefone(String telefone);

    boolean existsByEmail(String email);

    boolean existsByUsuario(String usuario);

    boolean existsByTelefone(String telefone);
}