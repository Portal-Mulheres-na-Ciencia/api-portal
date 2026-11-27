package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.AreaDeAtuacao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Premio;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.SugestaoCientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.AreaDeAtuacaoRepository;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.PremioRepository;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.SugestaoCientistaRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Área da equipe, do pedido HTTP até o banco H2: cadastro, edição, exclusão e avaliação de sugestões. */
@SpringBootTest
@AutoConfigureMockMvc
class CuradoriaIntegracaoTest {

    /** Mesmo e-mail da conta criada para os testes (src/test/resources/application.properties). */
    private static final RequestPostProcessor EQUIPE = user("equipe@teste.local");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AreaDeAtuacaoRepository areaRepository;

    @Autowired
    private PremioRepository premioRepository;

    @Autowired
    private SugestaoCientistaRepository sugestaoRepository;

    @Autowired
    private JdbcTemplate jdbc;

    private Integer idFisica;
    private Integer idQuimica;
    private Integer idNobel;

    @BeforeEach
    void preparar() {
        idFisica = areaRepository.save(new AreaDeAtuacao("Física", "Matéria e energia.")).getId();
        idQuimica = areaRepository.save(new AreaDeAtuacao("Química", "Substâncias.")).getId();
        idNobel = premioRepository.save(new Premio("Nobel de Física", "Academia Real")).getId();
    }

    @AfterEach
    void limpar() {
        for (String tabela : new String[] {"cientista_tem_area_atuacao", "cientista_tem_premio",
                "cientista_tem_contribuicao", "contribuicao", "cientista", "premio", "area_atuacao",
                "sugestao_cientista"}) {
            jdbc.update("DELETE FROM " + tabela);
        }
    }

    private String formulario(String nome, String extra) {
        return """
                {"nome": "%s", "biografia": "Física pioneira no estudo da radioatividade.",
                 "instituicao": "Universidade de Paris", "dataNascimento": "1867-11-07",
                 "dataFalecimento": "1934-07-04", "nacionalidade": "Polonesa",
                 "idsAreas": [%d],
                 "contribuicoes": [{"titulo": "Descoberta do polônio", "descricao": "Novo elemento.", "ano": 1898,
                                    "linkReferencia": "https://www.nobelprize.org"}],
                 "premios": [{"idPremio": %d, "ano": 1903}]%s}
                """.formatted(nome, idFisica, idNobel, extra);
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder pedido, String corpo) {
        return pedido.contentType(MediaType.APPLICATION_JSON).content(corpo).with(EQUIPE).with(csrf());
    }

    private Integer criar(String corpo) throws Exception {
        String resposta = mockMvc.perform(json(post("/admin/cientistas"), corpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.id");
    }

    @Test
    void deveCadastrarCientistaComAreaContribuicaoEPremio() throws Exception {
        mockMvc.perform(json(post("/admin/cientistas"), formulario("Marie Curie", "")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern(".*/cientistas/[0-9]+")))
                .andReturn();
    }

    @Test
    void deveMostrarNoPerfilPublicoACientistaCadastrada() throws Exception {
        Integer id = criar(formulario("Marie Curie", ""));

        mockMvc.perform(get("/cientistas/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Marie Curie"))
                .andExpect(jsonPath("$.areasAtuacao[0].nome").value("Física"))
                .andExpect(jsonPath("$.contribuicoes[0].titulo").value("Descoberta do polônio"))
                .andExpect(jsonPath("$.premios[0].ano").value(1903));
    }

    @Test
    void deveAtualizarDadosAreasContribuicoesEPremios() throws Exception {
        Integer id = criar(formulario("Marie Curie", ""));
        Integer idContribuicao = JsonPath.read(
                mockMvc.perform(get("/cientistas/" + id)).andReturn().getResponse().getContentAsString(),
                "$.contribuicoes[0].id");

        mockMvc.perform(json(put("/admin/cientistas/" + id), """
                        {"nome": "Marie Skłodowska-Curie", "biografia": "Biografia revisada.",
                         "idsAreas": [%d, %d],
                         "contribuicoes": [{"id": %d, "titulo": "Polônio e rádio", "ano": 1898},
                                           {"titulo": "Isolamento do rádio", "ano": 1910}],
                         "premios": [{"idPremio": %d, "ano": 1911}]}
                        """.formatted(idFisica, idQuimica, idContribuicao, idNobel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Marie Skłodowska-Curie"))
                .andExpect(jsonPath("$.areasAtuacao.length()").value(2))
                .andExpect(jsonPath("$.contribuicoes.length()").value(2))
                .andExpect(jsonPath("$.contribuicoes[0].id").value(idContribuicao))
                .andExpect(jsonPath("$.contribuicoes[0].titulo").value("Polônio e rádio"))
                .andExpect(jsonPath("$.premios[0].ano").value(1911));
    }

    @Test
    void deveApagarAContribuicaoQueSaiuDaLista() throws Exception {
        Integer id = criar(formulario("Marie Curie", ""));

        mockMvc.perform(json(put("/admin/cientistas/" + id), """
                        {"nome": "Marie Curie", "biografia": "Biografia.", "idsAreas": [%d]}
                        """.formatted(idFisica)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contribuicoes.length()").value(0))
                .andExpect(jsonPath("$.premios.length()").value(0));

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM contribuicao", Integer.class)).isZero();
    }

    @Test
    void deveExcluirACientistaComPremiosEContribuicoes() throws Exception {
        Integer id = criar(formulario("Marie Curie", ""));

        mockMvc.perform(delete("/admin/cientistas/" + id).with(EQUIPE).with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/cientistas/" + id)).andExpect(status().isNotFound());
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM contribuicao", Integer.class)).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM cientista_tem_premio", Integer.class)).isZero();
    }

    @Test
    void deveResponder404AoEditarOuExcluirCientistaQueNaoExiste() throws Exception {
        mockMvc.perform(json(put("/admin/cientistas/9999"), formulario("Ninguém", "")))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/admin/cientistas/9999").with(EQUIPE).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveMostrarAMensagemDeCadaCampoInvalido() throws Exception {
        mockMvc.perform(json(post("/admin/cientistas"), """
                        {"nome": "", "biografia": "", "idsAreas": [],
                         "contribuicoes": [{"titulo": "", "linkReferencia": "site sem http"}]}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").value("Informe o nome da cientista."))
                .andExpect(jsonPath("$.campos.biografia").value("Escreva a biografia."))
                .andExpect(jsonPath("$.campos.idsAreas").value("Escolha pelo menos uma área."))
                .andExpect(jsonPath("$.campos['contribuicoes[0].titulo']").value("Informe o título da contribuição."))
                .andExpect(jsonPath("$.campos['contribuicoes[0].linkReferencia']").exists());
    }

    @Test
    void deveRecusarFalecimentoAntesDoNascimento() throws Exception {
        mockMvc.perform(json(post("/admin/cientistas"), """
                        {"nome": "Teste", "biografia": "Bio.", "idsAreas": [%d],
                         "dataNascimento": "1900-01-01", "dataFalecimento": "1899-01-01"}
                        """.formatted(idFisica)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.dataFalecimento")
                        .value("A data de falecimento deve ser depois do nascimento."));
    }

    @Test
    void deveRecusarAreaOuPremioQueNaoExiste() throws Exception {
        mockMvc.perform(json(post("/admin/cientistas"), """
                        {"nome": "Teste", "biografia": "Bio.", "idsAreas": [9999]}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.idsAreas").value("Escolha áreas que existem na lista."));

        mockMvc.perform(json(post("/admin/cientistas"), """
                        {"nome": "Teste", "biografia": "Bio.", "idsAreas": [%d],
                         "premios": [{"idPremio": 9999, "ano": 2000}]}
                        """.formatted(idFisica)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos['premios[0].idPremio']").value("Escolha um prêmio da lista."));
    }

    @Test
    void deveExigirOTokenCsrfParaAlterarDados() throws Exception {
        mockMvc.perform(post("/admin/cientistas").contentType(MediaType.APPLICATION_JSON)
                        .content(formulario("Marie Curie", "")).with(EQUIPE))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveCadastrarAPartirDaSugestaoEMarcarComoAprovada() throws Exception {
        SugestaoCientista sugestao = sugestaoRepository.save(new SugestaoCientista("Sonia Guimarães",
                "Primeira mulher negra doutora em física no Brasil.", "Ana", "ana@exemplo.com"));

        Integer id = criar(formulario("Sonia Guimarães", ", \"idSugestao\": " + sugestao.getId()));

        mockMvc.perform(get("/admin/sugestoes").param("status", "APROVADA").with(EQUIPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].revisor").value("Equipe de Teste"))
                .andExpect(jsonPath("$[0].idCientista").value(id));

        mockMvc.perform(json(post("/admin/cientistas"), formulario("Outra", ", \"idSugestao\": " + sugestao.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.idSugestao").value("Esta sugestão já virou um cadastro."));
    }

    @Test
    void deveExcluirCientistaQueVeioDeUmaSugestaoMantendoASugestao() throws Exception {
        SugestaoCientista sugestao = sugestaoRepository.save(new SugestaoCientista("Sonia Guimarães",
                "Primeira mulher negra doutora em física no Brasil.", "Ana", "ana@exemplo.com"));
        Integer id = criar(formulario("Sonia Guimarães", ", \"idSugestao\": " + sugestao.getId()));

        mockMvc.perform(delete("/admin/cientistas/" + id).with(EQUIPE).with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/cientistas/" + id)).andExpect(status().isNotFound());
        mockMvc.perform(get("/admin/sugestoes").param("status", "APROVADA").with(EQUIPE))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idCientista").doesNotExist());
    }

    @Test
    void deveListarAsPendentesComOEmailERecusarUmaSugestao() throws Exception {
        SugestaoCientista sugestao = sugestaoRepository.save(new SugestaoCientista("Fulana",
                "Justificativa com mais de vinte caracteres.", "Ana", "ana@exemplo.com"));

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
    void deveListarECriarPremiosSemRepetirONome() throws Exception {
        mockMvc.perform(json(post("/admin/premios"), "{\"nome\": \"Prêmio Faz Diferença\", \"concedente\": \"O Globo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        mockMvc.perform(json(post("/admin/premios"), "{\"nome\": \"nobel de física\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nome").value("Já existe um prêmio com este nome."));

        mockMvc.perform(get("/admin/premios").with(EQUIPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Nobel de Física"));
    }
}
