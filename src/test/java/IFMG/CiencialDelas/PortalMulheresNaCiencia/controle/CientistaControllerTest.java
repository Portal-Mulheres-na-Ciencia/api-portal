package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.config.CorsConfig;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.AreaResumoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaDetalheDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaResumoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.ContribuicaoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.PremioRecebidoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.erro.RecursoNaoEncontradoException;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.servico.CientistaService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CientistaController.class)
@Import(CorsConfig.class)
class CientistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CientistaService cientistaService;

    @Test
    void deveRetornar200ComAListaNoFormatoDoContrato() throws Exception {
        when(cientistaService.listarResumo()).thenReturn(List.of(new CientistaResumoDTO(
                1, "Marie Curie", "Pioneira da radioatividade.", "Universidade de Paris",
                LocalDate.of(1867, 11, 7), LocalDate.of(1934, 7, 4), "Polonesa e francesa",
                List.of(new AreaResumoDTO(1, "Física"), new AreaResumoDTO(2, "Química")))));

        mockMvc.perform(get("/cientistas"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Marie Curie"))
                .andExpect(jsonPath("$[0].resumoBiografia").value("Pioneira da radioatividade."))
                .andExpect(jsonPath("$[0].instituicao").value("Universidade de Paris"))
                .andExpect(jsonPath("$[0].dataNascimento").value("1867-11-07"))
                .andExpect(jsonPath("$[0].dataFalecimento").value("1934-07-04"))
                .andExpect(jsonPath("$[0].nacionalidade").value("Polonesa e francesa"))
                .andExpect(jsonPath("$[0].areasAtuacao[0].id").value(1))
                .andExpect(jsonPath("$[0].areasAtuacao[0].nome").value("Física"))
                .andExpect(jsonPath("$[0].areasAtuacao[1].nome").value("Química"));
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaCientistas() throws Exception {
        when(cientistaService.listarResumo()).thenReturn(List.of());

        mockMvc.perform(get("/cientistas"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void devePermitirChamadasDoFrontEnd() throws Exception {
        when(cientistaService.listarResumo()).thenReturn(List.of());

        mockMvc.perform(get("/cientistas").header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }

    @Test
    void deveBloquearOrigensNaoPermitidas() throws Exception {
        mockMvc.perform(get("/cientistas").header("Origin", "http://site-desconhecido.com"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornarODetalheDaCientista() throws Exception {
        when(cientistaService.buscarDetalhe(1)).thenReturn(new CientistaDetalheDTO(
                1, "Marie Curie", "Biografia completa.", "Universidade de Paris",
                LocalDate.of(1867, 11, 7), LocalDate.of(1934, 7, 4), "Polonesa e francesa",
                List.of(new AreaResumoDTO(1, "Física")),
                List.of(new ContribuicaoDTO(1, 1898, "Polônio e rádio", "Novos elementos.", "")),
                List.of(new PremioRecebidoDTO(1, "Nobel de Física", "Academia Real", 1903))));

        mockMvc.perform(get("/cientistas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.biografia").value("Biografia completa."))
                .andExpect(jsonPath("$.contribuicoes[0].ano").value(1898))
                .andExpect(jsonPath("$.premios[0].nome").value("Nobel de Física"))
                .andExpect(jsonPath("$.premios[0].ano").value(1903));
    }

    @Test
    void deveRetornar404ComMensagemQuandoNaoExiste() throws Exception {
        when(cientistaService.buscarDetalhe(999))
                .thenThrow(new RecursoNaoEncontradoException("Cientista não encontrada."));

        mockMvc.perform(get("/cientistas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Cientista não encontrada."));
    }
}
