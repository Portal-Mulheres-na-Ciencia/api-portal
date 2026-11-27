package IFMG.CiencialDelas.PortalMulheresNaCiencia.servico;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.PremioDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.PremioFormDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Premio;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.erro.DadosInvalidosException;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.PremioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Prêmios que podem ser ligados às cientistas. */
@Service
public class PremioService {

    private final PremioRepository premioRepository;

    public PremioService(PremioRepository premioRepository) {
        this.premioRepository = premioRepository;
    }

    @Transactional(readOnly = true)
    public List<PremioDTO> listar() {
        return premioRepository.findAllByOrderByNomeAsc().stream().map(PremioService::paraDTO).toList();
    }

    @Transactional
    public PremioDTO criar(PremioFormDTO dados) {
        String nome = dados.nome().strip();
        if (premioRepository.existsByNomeIgnoreCase(nome)) {
            throw new DadosInvalidosException("nome", "Já existe um prêmio com este nome.");
        }
        return paraDTO(premioRepository.save(new Premio(nome, textoOuNulo(dados.concedente()))));
    }

    private static PremioDTO paraDTO(Premio premio) {
        return new PremioDTO(premio.getId(), premio.getNome(), premio.getInstituicaoConcedente());
    }

    static String textoOuNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.strip();
    }
}
