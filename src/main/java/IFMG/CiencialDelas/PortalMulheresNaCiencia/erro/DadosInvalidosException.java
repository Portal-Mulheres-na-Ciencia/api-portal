package IFMG.CiencialDelas.PortalMulheresNaCiencia.erro;

/** Erro de regra num campo do formulário. Vira uma resposta 400 com a mensagem ao lado do campo. */
public class DadosInvalidosException extends RuntimeException {

    private final String campo;

    public DadosInvalidosException(String campo, String mensagem) {
        super(mensagem);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
