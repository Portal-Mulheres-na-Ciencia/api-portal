package IFMG.CiencialDelas.PortalMulheresNaCiencia.servico;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaDetalheDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.ContribuicaoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.PremioRecebidoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Cientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Contribuicao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Premio;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.PremioRecebido;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.erro.RecursoNaoEncontradoException;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.CientistaRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CientistaServiceDetalheTest {

    @Mock
    private CientistaRepository cientistaRepository;

    @InjectMocks
    private CientistaService cientistaService;

    @Test
    void deveMontarODetalheComContribuicoesEPremiosEmOrdemCronologica() {
        Cientista curie = new Cientista("Marie Curie", "Biografia completa.", "Universidade de Paris",
                LocalDate.of(1867, 11, 7), LocalDate.of(1934, 7, 4), "Polonesa e francesa");
        curie.setId(1);
        curie.adicionarContribuicao(new Contribuicao("Isolamento do rádio", "Rádio puro.", 1910, ""));
        curie.adicionarContribuicao(new Contribuicao("Polônio e rádio", "Novos elementos.", 1898, "https://exemplo.org"));
        Premio quimica = new Premio("Nobel de Química", "Academia Real");
        quimica.setId(2);
        Premio fisica = new Premio("Nobel de Física", "Academia Real");
        fisica.setId(1);
        curie.getPremios().add(new PremioRecebido(curie, quimica, LocalDate.of(1911, 1, 1)));
        curie.getPremios().add(new PremioRecebido(curie, fisica, LocalDate.of(1903, 1, 1)));
        when(cientistaRepository.findById(1)).thenReturn(Optional.of(curie));

        CientistaDetalheDTO detalhe = cientistaService.buscarDetalhe(1);

        assertThat(detalhe.nome()).isEqualTo("Marie Curie");
        assertThat(detalhe.biografia()).isEqualTo("Biografia completa.");
        assertThat(detalhe.contribuicoes()).extracting(ContribuicaoDTO::ano).containsExactly(1898, 1910);
        assertThat(detalhe.contribuicoes().get(0).linkReferencia()).isEqualTo("https://exemplo.org");
        assertThat(detalhe.premios()).containsExactly(
                new PremioRecebidoDTO(1, "Nobel de Física", "Academia Real", 1903),
                new PremioRecebidoDTO(2, "Nobel de Química", "Academia Real", 1911));
    }

    @Test
    void deveAvisarQuandoACientistaNaoExiste() {
        when(cientistaRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cientistaService.buscarDetalhe(999))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Cientista não encontrada.");
    }
}
