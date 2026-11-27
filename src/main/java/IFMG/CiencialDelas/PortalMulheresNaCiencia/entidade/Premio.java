package IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Prêmio que pode ser concedido a uma ou mais cientistas (tabela premio). */
@Entity
@Table(name = "premio")
public class Premio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_premio")
    private Integer id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    /** Instituição que concede o prêmio (coluna instituicao). */
    @Column(name = "instituicao", length = 45)
    private String instituicaoConcedente;

    public Premio() {
    }

    public Premio(String nome, String instituicaoConcedente) {
        this.nome = nome;
        this.instituicaoConcedente = instituicaoConcedente;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getInstituicaoConcedente() {
        return instituicaoConcedente;
    }

    public void setInstituicaoConcedente(String instituicaoConcedente) {
        this.instituicaoConcedente = instituicaoConcedente;
    }
}
