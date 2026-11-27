package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

import jakarta.validation.constraints.NotBlank;

/** E-mail e senha digitados na tela de login. */
public record LoginDTO(
        @NotBlank(message = "Informe o seu e-mail.")
        String email,

        @NotBlank(message = "Informe a sua senha.")
        String senha) {
}
