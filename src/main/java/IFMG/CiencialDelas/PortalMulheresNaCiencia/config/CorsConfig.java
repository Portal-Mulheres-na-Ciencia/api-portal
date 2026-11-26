package IFMG.CiencialDelas.PortalMulheresNaCiencia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Permite que o front-end Angular, que roda em outro endereço, chame a API pelo navegador. */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] origensPermitidas;

    public CorsConfig(@Value("${app.cors.origens}") String[] origensPermitidas) {
        this.origensPermitidas = origensPermitidas;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(origensPermitidas)
                .allowedMethods("GET");
    }
}
