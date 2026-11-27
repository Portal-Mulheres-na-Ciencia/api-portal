package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

/** Pessoa da equipe que está logada. Nunca inclui a senha. */
public record UsuarioDTO(Integer id, String nome, String email) {
}
