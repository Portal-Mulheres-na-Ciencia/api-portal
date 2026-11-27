package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.SugestaoCientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.SugestaoCientistaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Área da equipe, do pedido HTTP até o banco H2: avaliação das sugestões enviadas pelo site. */
@SpringBootTest
@AutoConfigureMockMvc
class CuradoriaIntegracaoTest {

    /** Mesmo e-mail da conta criada para os testes (src/test/resources/application.properties). */
    private static final RequestPostProcessor EQUIPE = user("equipe@teste.local");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SugestaoCientistaRepository sugestaoRepository;

    @Autowired
    private JdbcTemplate jdbc;

    @AfterEach
    void limpar() {
        jdbc.update("DELETE FROM sugestao_cientista");
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder pedido, String corpo) {
        return pedido.contentType(MediaType.APPLICATION_JSON).content(corpo).with(EQUIPE).with(csrf());
    }

    private SugestaoCientista novaSugestao() {
        return sugestaoRepository.save(new SugestaoCientista("Fulana",
                "Justificativa com mais de vinte caracteres.", "Ana", "ana@exemplo.com"));
    }

    @Test
    void deveListarAsPendentesComOEmailERecusarUmaSugestao() throws Exception {
        SugestaoCientista sugestao = novaSugestao();

        mockMvc.perform(get("/admin/sugestoes").param("status", "PENDENTE").with(EQUIPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].emailSugestor").value("ana@exemplo.com"));

        mockMvc.perform(json(patch("/admin/sugestoes/" + sugestao.getId()), "{\"status\": \"RECUSADA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECUSADA"))
                .andExpect(jsonPath("$.revisor").value("Equipe de Teste"));

        mockMvc.perform(json(patch("/admin/sugestoes/" + sugestao.getId()), "{\"status\": \"PENDENTE\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveResponder404ParaSugestaoQueNaoExiste() throws Exception {
        mockMvc.perform(json(patch("/admin/sugestoes/999999"), "{\"status\": \"APROVADA\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRecusarStatusDesconhecidoNoFiltro() throws Exception {
        mockMvc.perform(get("/admin/sugestoes").param("status", "QUALQUER").with(EQUIPE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveExigirOTokenCsrfParaAvaliar() throws Exception {
        SugestaoCientista sugestao = novaSugestao();
        mockMvc.perform(patch("/admin/sugestoes/" + sugestao.getId()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"RECUSADA\"}").with(EQUIPE))
                .andExpect(status().isForbidden());
    }
}
