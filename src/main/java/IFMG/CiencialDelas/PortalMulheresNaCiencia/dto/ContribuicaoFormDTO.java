package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Contribuição informada no formulário da cientista. Sem id, é uma contribuição nova. */
public record ContribuicaoFormDTO(
        Integer id,

        @NotBlank(message = "Informe o título da contribuição.")
        @Size(max = 200, message = "Use no máximo 200 caracteres.")
        String titulo,

        @Size(max = 2000, message = "Use no máximo 2000 caracteres.")
        String descricao,

        @Min(value = 1000, message = "Informe um ano válido.")
        @Max(value = 2100, message = "Informe um ano válido.")
        Integer ano,

        @Size(max = 500, message = "Use no máximo 500 caracteres.")
        @Pattern(regexp = "^$|https?://\\S+", message = "Informe um link que comece com http:// ou https://.")
        String linkReferencia) {
}
