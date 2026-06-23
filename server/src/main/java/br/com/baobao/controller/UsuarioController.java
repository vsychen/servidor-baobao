package br.com.baobao.controller;

import br.com.baobao.dto.AlterarPapelRequest;
import br.com.baobao.exception.UsuarioNaoEncontradoException;
import br.com.baobao.model.Usuario;
import br.com.baobao.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para operações com usuários.
 * Endpoints protegidos por autenticação e autorização.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ==================== DADOS DO USUÁRIO AUTENTICADO ====================

    /**
     * Obtém os dados do usuário autenticado.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Usuario> obterDadosAtuais(Authentication authentication) {
        String credencial = authentication.getName();
        return usuarioService.buscarPorCredencial(credencial)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualiza os dados do usuário autenticado.
     */
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Usuario> atualizarDadosAtuais(
            @RequestBody Usuario usuarioAtualizado,
            Authentication authentication) {
        try {
            String credencial = authentication.getName();
            Usuario usuarioAtual = usuarioService.buscarPorCredencial(credencial)
                    .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

            Usuario usuarioAtualizadoResult = usuarioService.atualizar(usuarioAtual.getId(), usuarioAtualizado);
            return ResponseEntity.ok(usuarioAtualizadoResult);

        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== OPERAÇÕES ADMINISTRATIVAS ====================

    /**
     * Busca um usuário por ID.
     * Requer role EMPLOYEE ou ADMIN.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualiza um usuário existente.
     * Requer role ADMIN.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Long id,
            @RequestBody Usuario usuarioAtualizado) {
        try {
            Usuario usuario = usuarioService.atualizar(id, usuarioAtualizado);
            return ResponseEntity.ok(usuario);

        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Altera o papel de um funcionário.
     * Requer role ADMIN.
     */
    @PutMapping("/{id}/papel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Usuario> alterarPapel(
            @PathVariable Long id,
            @RequestBody AlterarPapelRequest request) {
        try {
            Usuario usuario = usuarioService.alterarPapelFuncionario(id, request.getPapel());
            return ResponseEntity.ok(usuario);

        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deleta um usuário.
     * Requer role ADMIN.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        try {
            usuarioService.deletar(id);
            return ResponseEntity.noContent().build();

        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }
}