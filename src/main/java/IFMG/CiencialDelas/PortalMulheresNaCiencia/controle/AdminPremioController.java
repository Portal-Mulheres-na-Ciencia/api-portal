package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.PremioDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.PremioFormDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.servico.PremioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Prêmios para o formulário de cientista (área da equipe, exige login). */
@RestController
@RequestMapping("/admin/premios")
public class AdminPremioController {

    private final PremioService premioService;

    public AdminPremioController(PremioService premioService) {
        this.premioService = premioService;
    }

    @GetMapping
    public List<PremioDTO> listar() {
        return premioService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PremioDTO criar(@Valid @RequestBody PremioFormDTO dados) {
        return premioService.criar(dados);
    }
}
