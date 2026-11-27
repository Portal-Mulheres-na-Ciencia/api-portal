package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.LoginDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.UsuarioDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.erro.RecursoNaoEncontradoException;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Login da equipe. A saída (POST /auth/logout) é feita pelo próprio Spring Security. */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final SecurityContextRepository contextoRepository = new HttpSessionSecurityContextRepository();

    public AuthController(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
    }

    /** Confere e-mail e senha e abre a sessão. Senha errada responde 401. */
    @PostMapping("/login")
    public UsuarioDTO entrar(@Valid @RequestBody LoginDTO dados, HttpServletRequest requisicao,
            HttpServletResponse resposta) {
        Authentication autenticacao = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(dados.email().strip().toLowerCase(), dados.senha()));

        // Troca o identificador da sessão no login, para ninguém reaproveitar uma sessão antiga.
        if (requisicao.getSession(false) != null) {
            requisicao.changeSessionId();
        }
        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(autenticacao);
        SecurityContextHolder.setContext(contexto);
        contextoRepository.saveContext(contexto, requisicao, resposta);
        return usuario(autenticacao.getName());
    }

    /** Quem está logado. Sem sessão, responde 401. */
    @GetMapping("/eu")
    public UsuarioDTO eu(Principal principal) {
        return usuario(principal.getName());
    }

    private UsuarioDTO usuario(String email) {
        return usuarioRepository.findByEmail(email)
                .map(u -> new UsuarioDTO(u.getId(), u.getNome(), u.getEmail()))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }
}
