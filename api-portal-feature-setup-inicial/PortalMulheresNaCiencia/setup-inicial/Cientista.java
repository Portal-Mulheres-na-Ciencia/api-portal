import java.time.LocalDate;
import java.util.ArrayList;

public class Cientista {
    private int id;
    private String nome;
    private String biografia;
    private String instituicao;
    private LocalDate dataNascimento;
    private LocalDate dataFalecimento;
    private String Nacionalidade;
    private ArrayList<AreaDeAtuacao> areasDeAtuacao;

    public Cientista(int id, String nome, String biografia, String instituicao, LocalDate dataNascimento, LocalDate dataFalecimento, String nacionalidade, ArrayList<AreaDeAtuacao> areasDeAtuacao) {
        this.id = id;
        this.nome = nome;
        this.biografia = biografia;
        this.instituicao = instituicao;
        this.dataNascimento = dataNascimento;
        this.dataFalecimento = dataFalecimento;
        Nacionalidade = nacionalidade;
        this.areasDeAtuacao = areasDeAtuacao;
    }

    public Cientista() {
    }

    public ArrayList<AreaDeAtuacao> getAreasDeAtuacao() {
        return areasDeAtuacao;
    }

    public void setAreasDeAtuacao(ArrayList<AreaDeAtuacao> areasDeAtuacao) {
        this.areasDeAtuacao = areasDeAtuacao;
    }

    public String getNacionalidade() {
        return Nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        Nacionalidade = nacionalidade;
    }

    public LocalDate getDataFalecimento() {
        return dataFalecimento;
    }

    public void setDataFalecimento(LocalDate dataFalecimento) {
        this.dataFalecimento = dataFalecimento;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}


