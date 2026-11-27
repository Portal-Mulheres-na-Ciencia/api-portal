package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.NovaSugestaoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.SugestaoCriadaDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.servico.SugestaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sugestoes")
public class SugestaoController {

    private final SugestaoService sugestaoService;

    public SugestaoController(SugestaoService sugestaoService) {
        this.sugestaoService = sugestaoService;
    }

    /** Recebe a sugestão do formulário e grava no banco. Dados inválidos: 400 com a mensagem de cada campo. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SugestaoCriadaDTO sugerir(@Valid @RequestBody NovaSugestaoDTO dados) {
        return sugestaoService.registrar(dados);
    }
}
