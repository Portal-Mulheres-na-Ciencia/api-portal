package IFMG.CiencialDelas.PortalMulheresNaCiencia.servico;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.AreaDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.AreaDeAtuacaoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AreaService {

    private final AreaDeAtuacaoRepository areaRepository;

    public AreaService(AreaDeAtuacaoRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @Transactional(readOnly = true)
    public List<AreaDTO> listar() {
        return areaRepository.listarComTotalDeCientistas();
    }
}
