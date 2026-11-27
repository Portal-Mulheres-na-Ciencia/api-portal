package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.RevisaoSugestaoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.SugestaoAdminDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.StatusSugestao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.servico.CuradoriaSugestaoService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Avaliação das sugestões enviadas pelo site (área da equipe, exige login). */
@RestController
@RequestMapping("/admin/sugestoes")
public class AdminSugestaoController {

    private final CuradoriaSugestaoService sugestaoService;

    public AdminSugestaoController(CuradoriaSugestaoService sugestaoService) {
        this.sugestaoService = sugestaoService;
    }

    /** Lista as sugestões. Use ?status=PENDENTE, APROVADA ou RECUSADA para filtrar. */
    @GetMapping
    public List<SugestaoAdminDTO> listar(@RequestParam(required = false) StatusSugestao status) {
        return sugestaoService.listar(status);
    }

    @PatchMapping("/{id}")
    public SugestaoAdminDTO revisar(@PathVariable Integer id, @Valid @RequestBody RevisaoSugestaoDTO dados,
            Principal principal) {
        return sugestaoService.revisar(id, dados.status(), principal.getName());
    }
}
