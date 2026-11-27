package IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/** Chave da tabela cientista_tem_premio: a cientista e o prêmio. */
@Embeddable
public class PremioRecebidoId implements Serializable {

    @Column(name = "cientista_id_cientista")
    private Integer idCientista;

    @Column(name = "premio_id_premio")
    private Integer idPremio;

    public PremioRecebidoId() {
    }

    public PremioRecebidoId(Integer idCientista, Integer idPremio) {
        this.idCientista = idCientista;
        this.idPremio = idPremio;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PremioRecebidoId outro
                && Objects.equals(idCientista, outro.idCientista)
                && Objects.equals(idPremio, outro.idPremio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idCientista, idPremio);
    }
}
