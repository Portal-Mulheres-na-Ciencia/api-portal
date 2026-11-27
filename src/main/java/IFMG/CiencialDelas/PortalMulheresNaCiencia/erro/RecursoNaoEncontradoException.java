package IFMG.CiencialDelas.PortalMulheresNaCiencia.erro;

/** Lançada quando o item pedido não existe; vira uma resposta 404. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
