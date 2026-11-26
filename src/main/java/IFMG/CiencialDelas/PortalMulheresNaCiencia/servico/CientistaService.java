package IFMG.CiencialDelas.PortalMulheresNaCiencia.servico;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.AreaResumoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaDetalheDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaResumoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.ContribuicaoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.PremioRecebidoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.AreaDeAtuacao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Cientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Contribuicao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.PremioRecebido;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.erro.RecursoNaoEncontradoException;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.CientistaRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CientistaService {

    /** Tamanho máximo do resumo da biografia mostrado no card do catálogo. */
    static final int TAMANHO_RESUMO = 160;

    private final CientistaRepository cientistaRepository;

    public CientistaService(CientistaRepository cientistaRepository) {
        this.cientistaRepository = cientistaRepository;
    }

    /** Lista todas as cientistas em ordem alfabética, no formato resumido do catálogo. */
    @Transactional(readOnly = true)
    public List<CientistaResumoDTO> listarResumo() {
        return cientistaRepository.findAllByOrderByNomeAsc().stream()
                .map(this::paraResumo)
                .toList();
    }

    /**
     * Dados completos de uma cientista, com contribuições e prêmios em ordem cronológica.
     * Lança {@link RecursoNaoEncontradoException} (resposta 404) quando o id não existe.
     */
    @Transactional(readOnly = true)
    public CientistaDetalheDTO buscarDetalhe(Integer id) {
        Cientista cientista = cientistaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cientista não encontrada."));
        List<ContribuicaoDTO> contribuicoes = cientista.getContribuicoes().stream()
                .sorted(Comparator.comparing(Contribuicao::getAno, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(c -> new ContribuicaoDTO(c.getId(), c.getAno(), c.getTitulo(), c.getDescricao(),
                        c.getLinkReferencia()))
                .toList();
        List<PremioRecebidoDTO> premios = cientista.getPremios().stream()
                .sorted(Comparator.comparing(PremioRecebido::getRecebidoEm))
                .map(p -> new PremioRecebidoDTO(p.getPremio().getId(), p.getPremio().getNome(),
                        p.getPremio().getInstituicaoConcedente(), p.getRecebidoEm().getYear()))
                .toList();
        return new CientistaDetalheDTO(
                cientista.getId(),
                cientista.getNome(),
                cientista.getBiografia(),
                cientista.getInstituicao(),
                cientista.getDataNascimento(),
                cientista.getDataFalecimento(),
                cientista.getNacionalidade(),
                areasDe(cientista),
                contribuicoes,
                premios);
    }

    private List<AreaResumoDTO> areasDe(Cientista cientista) {
        return cientista.getAreasDeAtuacao().stream()
                .sorted(Comparator.comparing(AreaDeAtuacao::getNome))
                .map(area -> new AreaResumoDTO(area.getId(), area.getNome()))
                .toList();
    }

    private CientistaResumoDTO paraResumo(Cientista cientista) {
        List<AreaResumoDTO> areas = areasDe(cientista);
        return new CientistaResumoDTO(
                cientista.getId(),
                cientista.getNome(),
                resumir(cientista.getBiografia()),
                cientista.getInstituicao(),
                cientista.getDataNascimento(),
                cientista.getDataFalecimento(),
                cientista.getNacionalidade(),
                areas);
    }

    /**
     * Início da biografia com até {@value #TAMANHO_RESUMO} caracteres, sem cortar palavras no meio.
     * Quando o texto é cortado, termina com "…". Biografia vazia vira resumo vazio.
     */
    static String resumir(String biografia) {
        if (biografia == null || biografia.isBlank()) {
            return "";
        }
        String texto = biografia.strip().replaceAll("\\s+", " ");
        if (texto.length() <= TAMANHO_RESUMO) {
            return texto;
        }
        String corte = texto.substring(0, TAMANHO_RESUMO);
        int ultimoEspaco = corte.lastIndexOf(' ');
        if (ultimoEspaco > 0) {
            corte = corte.substring(0, ultimoEspaco);
        }
        // Não termina o resumo com pontuação solta antes das reticências (ex.: "radioatividade,…").
        return corte.replaceAll("[\\s,;:.—–-]+$", "") + "…";
    }
}
