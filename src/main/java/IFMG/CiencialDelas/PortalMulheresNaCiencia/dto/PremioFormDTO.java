package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Novo prêmio criado pela equipe. */
public record PremioFormDTO(
        @NotBlank(message = "Informe o nome do prêmio.")
        @Size(max = 150, message = "Use no máximo 150 caracteres.")
        String nome,

        @Size(max = 45, message = "Use no máximo 45 caracteres.")
        String concedente) {
}
