package IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

/** Sugestão de nova cientista enviada por um visitante (tabela sugestao_cientista). */
@Entity
@Table(name = "sugestao_cientista")
public class SugestaoCientista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sugestao_cientista")
    private Integer id;

    @Column(name = "nome_cientista", nullable = false, length = 150)
    private String nomeCientista;

    @Column(name = "justificativa", nullable = false, columnDefinition = "TEXT")
    private String justificativa;

    @Column(name = "nome_sugestor", nullable = false, length = 100)
    private String nomeSugestor;

    @Column(name = "email_sugestor", nullable = false, length = 150)
    private String emailSugestor;

    @Column(name = "data_sugestao", nullable = false)
    private LocalDate dataSugestao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 45)
    private StatusSugestao status;

    /** Pessoa da equipe que avaliou a sugestão (vazio enquanto está pendente). */
    @ManyToOne
    @JoinColumn(name = "id_usuario_revisor")
    private Usuario revisor;

    /** Cientista cadastrada a partir desta sugestão, quando houver. */
    @OneToOne(mappedBy = "sugestao")
    private Cientista cientista;

    public SugestaoCientista() {
    }

    public SugestaoCientista(String nomeCientista, String justificativa, String nomeSugestor, String emailSugestor) {
        this.nomeCientista = nomeCientista;
        this.justificativa = justificativa;
        this.nomeSugestor = nomeSugestor;
        this.emailSugestor = emailSugestor;
        this.dataSugestao = LocalDate.now();
        this.status = StatusSugestao.PENDENTE;
    }

    /** Esquece a cientista ligada (usado quando ela é excluída). A sugestão continua com a decisão registrada. */
    public void desligarCientista() {
        this.cientista = null;
    }

    /** Registra a decisão da equipe e quem decidiu. */
    public void revisar(StatusSugestao novoStatus, Usuario quemRevisou) {
        this.status = novoStatus;
        this.revisor = quemRevisou;
    }

    public Integer getId() {
        return id;
    }

    public String getNomeCientista() {
        return nomeCientista;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public String getNomeSugestor() {
        return nomeSugestor;
    }

    public String getEmailSugestor() {
        return emailSugestor;
    }

    public LocalDate getDataSugestao() {
        return dataSugestao;
    }

    public StatusSugestao getStatus() {
        return status;
    }

    public Usuario getRevisor() {
        return revisor;
    }

    public Cientista getCientista() {
        return cientista;
    }
}
