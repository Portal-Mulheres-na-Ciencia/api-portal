package IFMG.CiencialDelas.PortalMulheresNaCiencia.erro;

import java.util.Map;

/**
 * Formato único das respostas de erro da API.
 * {@code campos} lista os problemas de cada campo quando o erro é de validação; senão fica vazio.
 */
public record ErroDTO(int status, String mensagem, Map<String, String> campos) {
}
