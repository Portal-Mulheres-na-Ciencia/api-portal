package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.AreaDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.servico.AreaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/areas")
public class AreaController {

    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    /** Áreas de atuação em ordem alfabética, com o total de cientistas de cada uma. */
    @GetMapping
    public List<AreaDTO> listar() {
        return areaService.listar();
    }
}
