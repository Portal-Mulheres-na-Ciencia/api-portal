package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaDetalheDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaFormDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.servico.CuradoriaCientistaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Cadastro, edição e exclusão de cientistas (área da equipe, exige login). */
@RestController
@RequestMapping("/admin/cientistas")
public class AdminCientistaController {

    private final CuradoriaCientistaService curadoriaService;

    public AdminCientistaController(CuradoriaCientistaService curadoriaService) {
        this.curadoriaService = curadoriaService;
    }

    @PostMapping
    public ResponseEntity<CientistaDetalheDTO> criar(@Valid @RequestBody CientistaFormDTO dados, Principal principal) {
        CientistaDetalheDTO criada = curadoriaService.criar(dados, principal.getName());
        return ResponseEntity.created(URI.create("/cientistas/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    public CientistaDetalheDTO atualizar(@PathVariable Integer id, @Valid @RequestBody CientistaFormDTO dados) {
        return curadoriaService.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) {
        curadoriaService.excluir(id);
    }
}
