package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Prêmio que a cientista recebeu e o ano. */
public record PremioRecebidoFormDTO(
        @NotNull(message = "Escolha o prêmio.")
        Integer idPremio,

        @NotNull(message = "Informe o ano.")
        @Min(value = 1000, message = "Informe um ano válido.")
        @Max(value = 2100, message = "Informe um ano válido.")
        Integer ano) {
}
