package IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/** Cientista exibida no portal (tabela cientista). */
@Entity
@Table(name = "cientista")
public class Cientista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cientista")
    private Integer id;

    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "biografia", columnDefinition = "TEXT")
    private String biografia;

    @Column(name = "instituicao_principal", length = 150)
    private String instituicao;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    /** A coluna mantém a grafia do modelo relacional entregue (data_falescimento). */
    @Column(name = "data_falescimento")
    private LocalDate dataFalecimento;

    @Column(name = "nacionalidade", length = 45)
    private String nacionalidade;

    /** Áreas em que a cientista atua (tabela cientista_tem_area_atuacao). */
    @ManyToMany
    @JoinTable(
            name = "cientista_tem_area_atuacao",
            joinColumns = @JoinColumn(name = "cientista_id_cientista"),
            inverseJoinColumns = @JoinColumn(name = "area_atuacao_id_area_atuacao"))
    private Set<AreaDeAtuacao> areasDeAtuacao = new HashSet<>();

    /** Contribuições da cientista (tabela cientista_tem_contribuicao). */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "cientista_tem_contribuicao",
            joinColumns = @JoinColumn(name = "cientista_id_cientista"),
            inverseJoinColumns = @JoinColumn(name = "contribuicao_id_contribuicao"))
    private Set<Contribuicao> contribuicoes = new HashSet<>();

    /** Prêmios recebidos (tabela cientista_tem_premio). Saem do banco junto com a cientista. */
    @OneToMany(mappedBy = "cientista", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PremioRecebido> premios = new HashSet<>();

    public Cientista() {
    }

    public Cientista(String nome, String biografia, String instituicao, LocalDate dataNascimento,
            LocalDate dataFalecimento, String nacionalidade) {
        this.nome = nome;
        this.biografia = biografia;
        this.instituicao = instituicao;
        this.dataNascimento = dataNascimento;
        this.dataFalecimento = dataFalecimento;
        this.nacionalidade = nacionalidade;
    }

    /** Liga a cientista à área dos dois lados da relação. */
    public void adicionarArea(AreaDeAtuacao area) {
        areasDeAtuacao.add(area);
        area.getCientistas().add(this);
    }

    /** Liga a cientista à contribuição dos dois lados da relação. */
    public void adicionarContribuicao(Contribuicao contribuicao) {
        contribuicoes.add(contribuicao);
        contribuicao.getCientistas().add(this);
    }

    /** Desfaz a ligação com a contribuição dos dois lados da relação. */
    public void removerContribuicao(Contribuicao contribuicao) {
        contribuicoes.remove(contribuicao);
        contribuicao.getCientistas().remove(this);
    }

    public void adicionarPremio(Premio premio, LocalDate recebidoEm) {
        premios.add(new PremioRecebido(this, premio, recebidoEm));
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

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public LocalDate getDataFalecimento() {
        return dataFalecimento;
    }

    public void setDataFalecimento(LocalDate dataFalecimento) {
        this.dataFalecimento = dataFalecimento;
    }

    public String getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public Set<AreaDeAtuacao> getAreasDeAtuacao() {
        return areasDeAtuacao;
    }

    public void setAreasDeAtuacao(Set<AreaDeAtuacao> areasDeAtuacao) {
        this.areasDeAtuacao = areasDeAtuacao;
    }

    public Set<Contribuicao> getContribuicoes() {
        return contribuicoes;
    }

    public Set<PremioRecebido> getPremios() {
        return premios;
    }
}
