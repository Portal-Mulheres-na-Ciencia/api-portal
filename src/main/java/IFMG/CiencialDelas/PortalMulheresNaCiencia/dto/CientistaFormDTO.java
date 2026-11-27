package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * Dados do formulário de cadastro e edição de cientista (área da equipe).
 * Quando idSugestao vem preenchido, a cientista fica ligada à sugestão que deu origem a ela.
 */
public record CientistaFormDTO(
        @NotBlank(message = "Informe o nome da cientista.")
        @Size(max = 150, message = "Use no máximo 150 caracteres.")
        String nome,

        @NotBlank(message = "Escreva a biografia.")
        @Size(max = 5000, message = "Use no máximo 5000 caracteres.")
        String biografia,

        @Size(max = 150, message = "Use no máximo 150 caracteres.")
        String instituicao,

        @PastOrPresent(message = "A data não pode estar no futuro.")
        LocalDate dataNascimento,

        @PastOrPresent(message = "A data não pode estar no futuro.")
        LocalDate dataFalecimento,

        @Size(max = 45, message = "Use no máximo 45 caracteres.")
        String nacionalidade,

        @NotEmpty(message = "Escolha pelo menos uma área.")
        List<Integer> idsAreas,

        @Valid
        List<ContribuicaoFormDTO> contribuicoes,

        @Valid
        List<PremioRecebidoFormDTO> premios,

        Integer idSugestao) {

    public CientistaFormDTO {
        contribuicoes = contribuicoes == null ? List.of() : contribuicoes;
        premios = premios == null ? List.of() : premios;
    }
}
