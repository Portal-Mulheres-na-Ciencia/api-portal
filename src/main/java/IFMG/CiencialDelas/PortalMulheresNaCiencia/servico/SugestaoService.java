package IFMG.CiencialDelas.PortalMulheresNaCiencia.servico;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.NovaSugestaoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.SugestaoCriadaDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.SugestaoCientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.SugestaoCientistaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SugestaoService {

    static final String MENSAGEM_RECEBIDA =
            "Sugestão recebida! A equipe vai avaliar e responder pelo e-mail informado.";

    private final SugestaoCientistaRepository sugestaoRepository;

    public SugestaoService(SugestaoCientistaRepository sugestaoRepository) {
        this.sugestaoRepository = sugestaoRepository;
    }

    /** Grava a sugestão com status PENDENTE, sem espaços sobrando e com o e-mail em minúsculas. */
    @Transactional
    public SugestaoCriadaDTO registrar(NovaSugestaoDTO dados) {
        SugestaoCientista sugestao = sugestaoRepository.save(new SugestaoCientista(
                dados.nomeCientista().strip(),
                dados.justificativa().strip(),
                dados.nomeSugestor().strip(),
                dados.emailSugestor().strip().toLowerCase()));
        return new SugestaoCriadaDTO(sugestao.getId(), sugestao.getNomeCientista(), sugestao.getStatus().name(),
                sugestao.getDataSugestao(), MENSAGEM_RECEBIDA);
    }
}
