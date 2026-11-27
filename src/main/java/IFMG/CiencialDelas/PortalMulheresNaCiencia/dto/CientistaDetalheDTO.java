package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

import java.time.LocalDate;
import java.util.List;

/** Dados completos de uma cientista para a página de Perfil (GET /cientistas/{id}). */
public record CientistaDetalheDTO(
        Integer id,
        String nome,
        String biografia,
        String instituicao,
        LocalDate dataNascimento,
        LocalDate dataFalecimento,
        String nacionalidade,
        List<AreaResumoDTO> areasAtuacao,
        List<ContribuicaoDTO> contribuicoes,
        List<PremioRecebidoDTO> premios) {
}
