package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaDetalheDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaResumoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.servico.CientistaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cientistas")
public class CientistaController {

    private final CientistaService cientistaService;

    public CientistaController(CientistaService cientistaService) {
        this.cientistaService = cientistaService;
    }

    /** Lista resumida das cientistas para os cards do catálogo. Sem cientistas, devolve lista vazia. */
    @GetMapping
    public List<CientistaResumoDTO> listar() {
        return cientistaService.listarResumo();
    }

    /** Dados completos de uma cientista para a página de Perfil. Id inexistente: 404. */
    @GetMapping("/{id}")
    public CientistaDetalheDTO buscar(@PathVariable Integer id) {
        return cientistaService.buscarDetalhe(id);
    }
}
