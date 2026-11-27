package IFMG.CiencialDelas.PortalMulheresNaCiencia.config;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Regras de acesso da API.
 * <ul>
 * <li>As consultas e o envio de sugestões são públicos.</li>
 * <li>A área da equipe (/admin) exige login. O login cria uma sessão guardada num cookie que o JavaScript não lê.</li>
 * <li>Ações que alteram dados exigem o token CSRF, que o Angular envia sozinho (cookie XSRF-TOKEN).</li>
 * </ul>
 */
@Configuration
public class SegurancaConfig {

    private static final String JSON_401 =
            "{\"status\":401,\"mensagem\":\"Entre com seu e-mail e senha para continuar.\",\"campos\":{}}";
    private static final String JSON_403 =
            "{\"status\":403,\"mensagem\":\"Não foi possível confirmar esta ação. Recarregue a página e tente de novo.\",\"campos\":{}}";

    @Bean
    SecurityFilterChain filtroDeSeguranca(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                // O envio de sugestão e o login não usam sessão, por isso dispensam o token CSRF.
                .csrf(csrf -> csrf.spa().ignoringRequestMatchers("/sugestoes", "/auth/login"))
                .authorizeHttpRequests(regras -> regras
                        .requestMatchers(HttpMethod.GET, "/cientistas", "/cientistas/*", "/areas").permitAll()
                        .requestMatchers(HttpMethod.POST, "/sugestoes", "/auth/login").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(erros -> erros
                        .authenticationEntryPoint((requisicao, resposta, e) -> responder(resposta, 401, JSON_401))
                        .accessDeniedHandler((requisicao, resposta, e) -> responder(resposta, 403, JSON_403)))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(saida -> saida
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((requisicao, resposta, autenticacao) -> resposta.setStatus(204)));
        return http.build();
    }

    /** Criptografa as senhas da equipe antes de gravar no banco. */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static void responder(HttpServletResponse resposta, int status, String json) throws IOException {
        resposta.setStatus(status);
        resposta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resposta.setCharacterEncoding("UTF-8");
        resposta.getWriter().write(json);
    }
}
