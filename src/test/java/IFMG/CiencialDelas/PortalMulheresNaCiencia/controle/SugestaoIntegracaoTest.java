package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.StatusSugestao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.SugestaoCientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.SugestaoCientistaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Do formulário até o banco: a sugestão enviada fica gravada como PENDENTE. */
@SpringBootTest
@AutoConfigureMockMvc
class SugestaoIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SugestaoCientistaRepository sugestaoRepository;

    @AfterEach
    void limpar() {
        sugestaoRepository.deleteAll();
    }

    @Test
    void deveGravarASugestaoNoBanco() throws Exception {
        mockMvc.perform(post("/sugestoes").contentType(MediaType.APPLICATION_JSON).content("""
                        {"nomeCientista": "Sonia Guimarães",
                         "justificativa": "Primeira mulher negra doutora em física no Brasil.",
                         "nomeSugestor": "Ana", "emailSugestor": "ana@exemplo.com", "consentimento": true}
                        """))
                .andExpect(status().isCreated());

        assertThat(sugestaoRepository.findAll()).singleElement().satisfies(s -> {
            assertThat(s.getNomeCientista()).isEqualTo("Sonia Guimarães");
            assertThat(s.getStatus()).isEqualTo(StatusSugestao.PENDENTE);
            assertThat(s.getDataSugestao()).isNotNull();
        });
    }
}
