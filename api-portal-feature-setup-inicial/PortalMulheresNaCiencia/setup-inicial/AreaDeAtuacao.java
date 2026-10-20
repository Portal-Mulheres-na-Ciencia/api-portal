import java.time.LocalDate;
import java.util.ArrayList;

public class AreaDeAtuacao {
    private int id;
    private String nome;
    private String descricao;
    private ArrayList<Cientista> cientistas;

    public AreaDeAtuacao(int id, String nome, String descricao, ArrayList<Cientista> cientistas) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.cientistas = cientistas;
    }

    public AreaDeAtuacao() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public ArrayList<Cientista> getCientistas() {
        return cientistas;
    }

    public void setCientistas(ArrayList<Cientista> cientistas) {
        this.cientistas = cientistas;
    }
}
