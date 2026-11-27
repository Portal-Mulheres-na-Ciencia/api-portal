package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.StatusSugestao;
import jakarta.validation.constraints.NotNull;

/** Decisão da equipe sobre uma sugestão: APROVADA ou RECUSADA. */
public record RevisaoSugestaoDTO(
        @NotNull(message = "Escolha aprovar ou recusar.")
        StatusSugestao status) {
}
