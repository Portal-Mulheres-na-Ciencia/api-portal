package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

/** Área de atuação com o número de cientistas, usada nos filtros e na página inicial (GET /areas). */
public record AreaDTO(Integer id, String nome, String descricao, Long totalCientistas) {
}
