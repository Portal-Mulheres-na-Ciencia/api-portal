package IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio;

import static org.assertj.core.api.Assertions.assertThat;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.AreaDeAtuacao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Cientista;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
class CientistaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CientistaRepository cientistaRepository;

    @Test
    void deveSalvarCientistaComVariasAreas() {
        AreaDeAtuacao fisica = entityManager.persist(new AreaDeAtuacao("Física", "Matéria e energia."));
        AreaDeAtuacao quimica = entityManager.persist(new AreaDeAtuacao("Química", "Substâncias."));

        Cientista curie = new Cientista("Marie Curie", "Pioneira da radioatividade.", "Universidade de Paris",
                LocalDate.of(1867, 11, 7), LocalDate.of(1934, 7, 4), "Polonesa e francesa");
        curie.adicionarArea(fisica);
        curie.adicionarArea(quimica);
        Integer id = entityManager.persistAndFlush(curie).getId();
        entityManager.clear();

        Cientista lida = cientistaRepository.findById(id).orElseThrow();
        assertThat(lida.getNome()).isEqualTo("Marie Curie");
        assertThat(lida.getInstituicao()).isEqualTo("Universidade de Paris");
        assertThat(lida.getDataFalecimento()).isEqualTo(LocalDate.of(1934, 7, 4));
        assertThat(lida.getAreasDeAtuacao()).extracting(AreaDeAtuacao::getNome)
                .containsExactlyInAnyOrder("Física", "Química");
    }

    @Test
    void deveListarEmOrdemAlfabeticaComAsAreas() {
        AreaDeAtuacao matematica = entityManager.persist(new AreaDeAtuacao("Matemática", null));
        Cientista noether = new Cientista("Emmy Noether", null, null, null, null, null);
        noether.adicionarArea(matematica);
        entityManager.persist(noether);
        entityManager.persist(new Cientista("Ada Lovelace", null, null, null, null, null));
        entityManager.flush();
        entityManager.clear();

        List<Cientista> lista = cientistaRepository.findAllByOrderByNomeAsc();

        assertThat(lista).extracting(Cientista::getNome).containsExactly("Ada Lovelace", "Emmy Noether");
        assertThat(lista.get(1).getAreasDeAtuacao()).extracting(AreaDeAtuacao::getNome)
                .containsExactly("Matemática");
    }
}
