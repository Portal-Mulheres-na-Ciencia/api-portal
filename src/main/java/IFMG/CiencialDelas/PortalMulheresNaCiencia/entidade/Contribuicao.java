package IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/** Contribuição científica (tabela contribuicao). Uma contribuição pode ter mais de uma cientista. */
@Entity
@Table(name = "contribuicao")
public class Contribuicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contribuicao")
    private Integer id;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "ano")
    private Integer ano;

    @Column(name = "link_referencia", length = 500)
    private String linkReferencia;

    /** Lado inverso da relação com Cientista, que é quem grava a tabela cientista_tem_contribuicao. */
    @ManyToMany(mappedBy = "contribuicoes")
    private Set<Cientista> cientistas = new HashSet<>();

    public Contribuicao() {
    }

    public Contribuicao(String titulo, String descricao, Integer ano, String linkReferencia) {
        atualizar(titulo, descricao, ano, linkReferencia);
    }

    public void atualizar(String titulo, String descricao, Integer ano, String linkReferencia) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.ano = ano;
        this.linkReferencia = linkReferencia;
    }

    public Integer getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getAno() {
        return ano;
    }

    public String getLinkReferencia() {
        return linkReferencia;
    }

    public Set<Cientista> getCientistas() {
        return cientistas;
    }
}
