package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Login da equipe com o banco H2. A conta de teste é criada pelo InicializadorUsuario
 * com os dados de src/test/resources/application.properties.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession entrar(String senha) throws Exception {
        return (MockHttpSession) mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"Equipe@Teste.local\", \"senha\": \"" + senha + "\"}"))
                .andReturn().getRequest().getSession();
    }

    @Test
    void deveEntrarComEmailESenhaCorretosEManterASessao() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"equipe@teste.local\", \"senha\": \"senha-de-teste\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Equipe de Teste"))
                .andExpect(jsonPath("$.senhaHash").doesNotExist());

        MockHttpSession sessao = entrar("senha-de-teste");
        mockMvc.perform(get("/auth/eu").session(sessao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("equipe@teste.local"));
    }

    @Test
    void deveRecusarSenhaErradaSemDizerQualCampoEstaErrado() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"equipe@teste.local\", \"senha\": \"errada\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("E-mail ou senha incorretos."));
    }

    @Test
    void deveExigirEmailESenha() throws Exception {
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"email\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.email").value("Informe o seu e-mail."))
                .andExpect(jsonPath("$.campos.senha").value("Informe a sua senha."));
    }

    @Test
    void deveResponder401EmJsonQuandoNaoHaSessao() throws Exception {
        mockMvc.perform(get("/auth/eu"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Entre com seu e-mail e senha para continuar."));
        mockMvc.perform(get("/admin/sugestoes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveEncerrarASessaoAoSair() throws Exception {
        MockHttpSession sessao = entrar("senha-de-teste");

        mockMvc.perform(post("/auth/logout").session(sessao).with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/auth/eu").session(sessao))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveManterAsPaginasPublicasAbertas() throws Exception {
        mockMvc.perform(get("/cientistas")).andExpect(status().isOk());
        mockMvc.perform(get("/areas")).andExpect(status().isOk());
    }
}
