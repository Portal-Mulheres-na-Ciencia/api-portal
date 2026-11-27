package IFMG.CiencialDelas.PortalMulheresNaCiencia.servico;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Usuario;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Busca a pessoa da equipe pelo e-mail para o login. */
@Service
public class UsuarioDetalhesService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetalhesService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email.strip().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("E-mail ou senha incorretos."));
        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .roles("EQUIPE")
                .build();
    }
}
