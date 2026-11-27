package IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade;

/** Situação de uma sugestão de cientista. */
public enum StatusSugestao {
    /** Aguardando a avaliação da equipe. */
    PENDENTE,
    /** A equipe cadastrou a cientista sugerida. */
    APROVADA,
    /** A equipe decidiu não cadastrar a cientista. */
    RECUSADA
}
