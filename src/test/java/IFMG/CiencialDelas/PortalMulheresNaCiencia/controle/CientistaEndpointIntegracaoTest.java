package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.AreaDeAtuacao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Cientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Contribuicao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Premio;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.springframework.transaction.support.TransactionTemplate;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.AreaDeAtuacaoRepository;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.CientistaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** Teste de ponta a ponta: banco H2 → repositório → serviço → controller → JSON. */
@SpringBootTest
@AutoConfigureMockMvc
class CientistaEndpointIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CientistaRepository cientistaRepository;

    @Autowired
    private AreaDeAtuacaoRepository areaRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transacao;

    @AfterEach
    void limpar() {
        transacao.executeWithoutResult(t -> {
            entityManager.createQuery("delete from PremioRecebido").executeUpdate();
            entityManager.createQuery("delete from Premio").executeUpdate();
            entityManager.createQuery("delete from Contribuicao").executeUpdate();
        });
        cientistaRepository.deleteAll();
        areaRepository.deleteAll();
    }

    @Test
    void deveListarAsCientistasDoBancoEmOrdemAlfabetica() throws Exception {
        AreaDeAtuacao computacao = areaRepository.save(new AreaDeAtuacao("Computação", null));
        AreaDeAtuacao matematica = areaRepository.save(new AreaDeAtuacao("Matemática", null));

        Cientista ada = new Cientista("Ada Lovelace", "Matemática inglesa e autora do primeiro algoritmo.",
                "Pesquisadora independente", null, null, "Britânica");
        ada.adicionarArea(matematica);
        ada.adicionarArea(computacao);
        cientistaRepository.save(ada);
        cientistaRepository.save(new Cientista("Bertha Lutz", "Bióloga do Museu Nacional.", "Museu Nacional",
                null, null, "Brasileira"));

        mockMvc.perform(get("/cientistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Ada Lovelace"))
                .andExpect(jsonPath("$[0].instituicao").value("Pesquisadora independente"))
                .andExpect(jsonPath("$[0].areasAtuacao.length()").value(2))
                .andExpect(jsonPath("$[0].areasAtuacao[0].nome").value("Computação"))
                .andExpect(jsonPath("$[1].nome").value("Bertha Lutz"))
                .andExpect(jsonPath("$[1].areasAtuacao.length()").value(0));
    }

    @Test
    void deveRetornarOPerfilCompletoDoBanco() throws Exception {
        Integer id = transacao.execute(t -> {
            Cientista curie = new Cientista("Marie Curie", "Biografia completa.", "Universidade de Paris",
                    LocalDate.of(1867, 11, 7), LocalDate.of(1934, 7, 4), "Polonesa e francesa");
            curie.adicionarContribuicao(new Contribuicao("Polônio e rádio", "Novos elementos.", 1898, ""));
            entityManager.persist(curie);
            Premio nobel = new Premio("Nobel de Física", "Academia Real");
            entityManager.persist(nobel);
            curie.adicionarPremio(nobel, LocalDate.of(1903, 1, 1));
            return curie.getId();
        });

        mockMvc.perform(get("/cientistas/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Marie Curie"))
                .andExpect(jsonPath("$.dataFalecimento").value("1934-07-04"))
                .andExpect(jsonPath("$.contribuicoes[0].titulo").value("Polônio e rádio"))
                .andExpect(jsonPath("$.premios[0].ano").value(1903));

        mockMvc.perform(get("/cientistas/999999")).andExpect(status().isNotFound());
    }
}
