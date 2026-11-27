package IFMG.CiencialDelas.PortalMulheresNaCiencia.dto;

import java.time.LocalDate;

/** Resposta ao enviar uma sugestão. Não devolve o e-mail de quem sugeriu. */
public record SugestaoCriadaDTO(Integer id, String nomeCientista, String status, LocalDate dataSugestao,
        String mensagem) {
}
