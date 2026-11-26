package IFMG.CiencialDelas.PortalMulheresNaCiencia.servico;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.AreaResumoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaResumoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.AreaDeAtuacao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Cientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.CientistaRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CientistaServiceTest {

    @Mock
    private CientistaRepository cientistaRepository;

    @InjectMocks
    private CientistaService cientistaService;

    private static AreaDeAtuacao area(int id, String nome) {
        AreaDeAtuacao area = new AreaDeAtuacao(nome, null);
        area.setId(id);
        return area;
    }

    @Test
    void deveConverterCientistaParaResumo() {
        Cientista curie = new Cientista("Marie Curie", "Pioneira da radioatividade.", "Universidade de Paris",
                LocalDate.of(1867, 11, 7), LocalDate.of(1934, 7, 4), "Polonesa e francesa");
        curie.setId(1);
        curie.adicionarArea(area(2, "Química"));
        curie.adicionarArea(area(1, "Física"));
        when(cientistaRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(curie));

        List<CientistaResumoDTO> resultado = cientistaService.listarResumo();

        assertThat(resultado).containsExactly(new CientistaResumoDTO(
                1, "Marie Curie", "Pioneira da radioatividade.", "Universidade de Paris",
                LocalDate.of(1867, 11, 7), LocalDate.of(1934, 7, 4), "Polonesa e francesa",
                List.of(new AreaResumoDTO(1, "Física"), new AreaResumoDTO(2, "Química"))));
    }

    @Test
    void deveManterAOrdemDoRepositorio() {
        when(cientistaRepository.findAllByOrderByNomeAsc()).thenReturn(List.of(
                new Cientista("Ada Lovelace", null, null, null, null, null),
                new Cientista("Bertha Lutz", null, null, null, null, null)));

        assertThat(cientistaService.listarResumo()).extracting(CientistaResumoDTO::nome)
                .containsExactly("Ada Lovelace", "Bertha Lutz");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaCientistas() {
        when(cientistaRepository.findAllByOrderByNomeAsc()).thenReturn(List.of());

        assertThat(cientistaService.listarResumo()).isEmpty();
    }

    @Test
    void resumoDeTextoCurtoFicaIgual() {
        assertThat(CientistaService.resumir("  Matemática   inglesa. ")).isEqualTo("Matemática inglesa.");
    }

    @Test
    void resumoDeTextoLongoNaoCortaPalavraETerminaComReticencias() {
        String biografia = "palavra ".repeat(40);

        String resumo = CientistaService.resumir(biografia);

        assertThat(resumo).endsWith("palavra…");
        assertThat(resumo.length()).isLessThanOrEqualTo(CientistaService.TAMANHO_RESUMO + 1);
    }

    @Test
    void resumoNaoTerminaComPontuacaoAntesDasReticencias() {
        String biografia = "a".repeat(150) + ", " + "b".repeat(30);

        assertThat(CientistaService.resumir(biografia)).isEqualTo("a".repeat(150) + "…");
    }

    @Test
    void resumoDeBiografiaVaziaOuNulaEVazio() {
        assertThat(CientistaService.resumir(null)).isEmpty();
        assertThat(CientistaService.resumir("   ")).isEmpty();
    }
}
