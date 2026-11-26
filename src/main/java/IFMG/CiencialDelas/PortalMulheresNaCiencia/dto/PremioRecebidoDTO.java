package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

/** Prêmio recebido, como aparece no perfil: só o ano é exibido. */
public record PremioRecebidoDTO(Integer id, String nome, String concedente, Integer ano) {
}
