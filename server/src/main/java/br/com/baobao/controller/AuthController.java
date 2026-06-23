package br.com.baobao.controller;

import br.com.baobao.dto.ClienteRegistroRequest;
import br.com.baobao.dto.LoginRequest;
import br.com.baobao.dto.LoginResponse;
import br.com.baobao.exception.UsuarioDuplicadoException;
import br.com.baobao.exception.UsuarioNaoEncontradoException;
import br.com.baobao.model.Cliente;
import br.com.baobao.model.Funcionario;
import br.com.baobao.model.Usuario;
import br.com.baobao.service.UsuarioService;
import br.com.baobao.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticação e registro de usuários.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    // ==================== REGISTRO ====================

    /**
     * Registra um novo cliente.
     */
    @PostMapping("/registrar/cliente")
    public ResponseEntity<LoginResponse> registrarCliente(@RequestBody ClienteRegistroRequest request) {
        try {
            Cliente cliente = new Cliente(
                    request.getEmail(),
                    request.getUsuario(),
                    request.getTelefone(),
                    request.getSenha(),
                    request.getNome()
            );

            Usuario usuarioCriado = usuarioService.criarUsuario(cliente);
            String token = jwtUtil.generateToken(usuarioCriado);

            LoginResponse response = new LoginResponse(token, usuarioCriado.getEmail(), usuarioCriado.getNome());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (UsuarioDuplicadoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Registra um novo funcionário.
     */
    @PostMapping("/registrar/funcionario")
    public ResponseEntity<LoginResponse> registrarFuncionario(@RequestBody ClienteRegistroRequest request) {
        try {
            Funcionario funcionario = new Funcionario(
                    request.getEmail(),
                    request.getUsuario(),
                    request.getTelefone(),
                    request.getSenha(),
                    request.getNome()
            );

            Usuario usuarioCriado = usuarioService.criarUsuario(funcionario);
            String token = jwtUtil.generateToken(usuarioCriado);

            LoginResponse response = new LoginResponse(token, usuarioCriado.getEmail(), usuarioCriado.getNome());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (UsuarioDuplicadoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== LOGIN ====================

    /**
     * Autentica um usuário e retorna um JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autentica o usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getCredencial(),
                            loginRequest.getSenha()
                    )
            );

            // Extrai os detalhes do usuário autenticado
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            // Busca o usuário completo para retornar na resposta
            Usuario usuario = usuarioService.buscarPorCredencial(loginRequest.getCredencial())
                    .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

            LoginResponse response = new LoginResponse(token, usuario.getEmail(), usuario.getNome());
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (UsuarioNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}