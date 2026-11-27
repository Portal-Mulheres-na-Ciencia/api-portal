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

/** Área de atuação científica (tabela area_atuacao). */
@Entity
@Table(name = "area_atuacao")
public class AreaDeAtuacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_area_atuacao")
    private Integer id;

    @Column(name = "nome", nullable = false, length = 45)
    private String nome;

    @Column(name = "descricao", length = 45)
    private String descricao;

    /** Lado inverso da relação com Cientista, que é quem grava a tabela cientista_tem_area_atuacao. */
    @ManyToMany(mappedBy = "areasDeAtuacao")
    private Set<Cientista> cientistas = new HashSet<>();

    public AreaDeAtuacao() {
    }

    public AreaDeAtuacao(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Set<Cientista> getCientistas() {
        return cientistas;
    }

    public void setCientistas(Set<Cientista> cientistas) {
        this.cientistas = cientistas;
    }
}
