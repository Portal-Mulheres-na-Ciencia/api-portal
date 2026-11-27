package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

import java.time.LocalDate;

/**
 * Sugestão como a equipe vê na área administrativa. Inclui o e-mail de quem sugeriu,
 * que só aparece aqui e serve apenas para responder à sugestão.
 */
public record SugestaoAdminDTO(
        Integer id,
        String nomeCientista,
        String justificativa,
        String nomeSugestor,
        String emailSugestor,
        LocalDate dataSugestao,
        String status,
        String revisor,
        Integer idCientista) {
}
