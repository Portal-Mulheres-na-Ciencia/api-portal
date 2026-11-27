package IFMG.CiencialDelas.PortalMulheresNaCiencia.config;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Usuario;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria a primeira conta da equipe quando a tabela usuario está vazia.
 * O e-mail e a senha vêm das variáveis de ambiente ADMIN_EMAIL e ADMIN_SENHA, para nenhuma senha ficar no código.
 */
@Component
public class InicializadorUsuario implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(InicializadorUsuario.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String nome;
    private final String email;
    private final String senha;

    public InicializadorUsuario(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            @Value("${app.admin.nome}") String nome,
            @Value("${app.admin.email}") String email,
            @Value("${app.admin.senha}") String senha) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() || senha.isBlank() || usuarioRepository.count() > 0) {
            return;
        }
        // A coluna usuario.nome aceita até 45 caracteres.
        String nomeCurto = nome.strip().length() > 45 ? nome.strip().substring(0, 45) : nome.strip();
        usuarioRepository.save(new Usuario(nomeCurto, email.strip().toLowerCase(), passwordEncoder.encode(senha)));
        LOG.info("Conta da equipe criada para {}", email);
    }
}
