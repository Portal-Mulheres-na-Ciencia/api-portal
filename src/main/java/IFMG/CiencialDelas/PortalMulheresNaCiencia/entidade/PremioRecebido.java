package IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** Prêmio recebido por uma cientista e a data em que ela recebeu (tabela cientista_tem_premio). */
@Entity
@Table(name = "cientista_tem_premio")
public class PremioRecebido {

    @EmbeddedId
    private PremioRecebidoId id = new PremioRecebidoId();

    @MapsId("idCientista")
    @ManyToOne(optional = false)
    @JoinColumn(name = "cientista_id_cientista")
    private Cientista cientista;

    @MapsId("idPremio")
    @ManyToOne(optional = false)
    @JoinColumn(name = "premio_id_premio")
    private Premio premio;

    @Column(name = "recebido_em", nullable = false)
    private LocalDate recebidoEm;

    public PremioRecebido() {
    }

    public PremioRecebido(Cientista cientista, Premio premio, LocalDate recebidoEm) {
        this.cientista = cientista;
        this.premio = premio;
        this.recebidoEm = recebidoEm;
    }

    public Cientista getCientista() {
        return cientista;
    }

    public Premio getPremio() {
        return premio;
    }

    public LocalDate getRecebidoEm() {
        return recebidoEm;
    }

    public void setRecebidoEm(LocalDate recebidoEm) {
        this.recebidoEm = recebidoEm;
    }
}
