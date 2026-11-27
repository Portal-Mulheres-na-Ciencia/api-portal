package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.config.CorsConfig;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.SugestaoCriadaDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.servico.SugestaoService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SugestaoController.class)
@Import(CorsConfig.class)
class SugestaoControllerTest {

    private static final String VALIDA = """
            {"nomeCientista": "Sonia Guimarães",
             "justificativa": "Primeira mulher negra doutora em física no Brasil.",
             "nomeSugestor": "Ana", "emailSugestor": "ana@exemplo.com", "consentimento": true}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SugestaoService sugestaoService;

    @Test
    void deveResponder201QuandoOsDadosSaoValidos() throws Exception {
        when(sugestaoService.registrar(any())).thenReturn(new SugestaoCriadaDTO(
                7, "Sonia Guimarães", "PENDENTE", LocalDate.of(2026, 11, 10), "Sugestão recebida!"));

        mockMvc.perform(post("/sugestoes").contentType(MediaType.APPLICATION_JSON).content(VALIDA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andExpect(jsonPath("$.emailSugestor").doesNotExist());
    }

    @Test
    void deveResponder400ComAMensagemDeCadaCampo() throws Exception {
        String invalida = """
                {"nomeCientista": "", "justificativa": "curta", "nomeSugestor": "Ana",
                 "emailSugestor": "nao-e-email", "consentimento": false}
                """;

        mockMvc.perform(post("/sugestoes").contentType(MediaType.APPLICATION_JSON).content(invalida))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Confira os campos destacados."))
                .andExpect(jsonPath("$.campos.nomeCientista").value("Informe o nome da cientista."))
                .andExpect(jsonPath("$.campos.justificativa").value("A justificativa deve ter entre 20 e 2000 caracteres."))
                .andExpect(jsonPath("$.campos.emailSugestor").value("Informe um e-mail válido."))
                .andExpect(jsonPath("$.campos.consentimento").exists());
        verify(sugestaoService, never()).registrar(any());
    }

    @Test
    void deveResponder400QuandoOCorpoNaoEJson() throws Exception {
        mockMvc.perform(post("/sugestoes").contentType(MediaType.APPLICATION_JSON).content("não é json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Não foi possível ler os dados enviados."));
    }

    @Test
    void devePermitirQueOFrontEndEnvieSugestoes() throws Exception {
        mockMvc.perform(options("/sugestoes")
                        .header("Origin", "http://localhost:4200")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }
}
