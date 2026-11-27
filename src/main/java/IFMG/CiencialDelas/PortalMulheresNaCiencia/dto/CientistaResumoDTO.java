package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

import java.time.LocalDate;
import java.util.List;

/** Dados resumidos de uma cientista, usados nos cards do catálogo (GET /cientistas). */
public record CientistaResumoDTO(
        Integer id,
        String nome,
        String resumoBiografia,
        String instituicao,
        LocalDate dataNascimento,
        LocalDate dataFalecimento,
        String nacionalidade,
        List<AreaResumoDTO> areasAtuacao) {
}
