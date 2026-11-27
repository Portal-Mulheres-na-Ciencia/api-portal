package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados enviados pelo formulário "Sugerir cientista" (POST /sugestoes).
 * O consentimento é obrigatório (LGPD): sem ele, a sugestão não é gravada.
 */
public record NovaSugestaoDTO(
        @NotBlank(message = "Informe o nome da cientista.")
        @Size(max = 150, message = "O nome da cientista deve ter no máximo 150 caracteres.")
        String nomeCientista,

        @NotBlank(message = "Conte por que ela deveria estar no portal.")
        @Size(min = 20, max = 2000, message = "A justificativa deve ter entre 20 e 2000 caracteres.")
        String justificativa,

        @NotBlank(message = "Informe o seu nome.")
        @Size(max = 100, message = "O seu nome deve ter no máximo 100 caracteres.")
        String nomeSugestor,

        @NotBlank(message = "Informe o seu e-mail.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
        String emailSugestor,

        @AssertTrue(message = "É preciso concordar com o uso dos seus dados para responder à sugestão.")
        boolean consentimento) {
}
