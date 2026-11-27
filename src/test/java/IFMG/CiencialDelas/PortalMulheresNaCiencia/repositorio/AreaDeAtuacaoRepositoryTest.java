package IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.AreaDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.AreaDeAtuacao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Cientista;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

@DataJpaTest
class AreaDeAtuacaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AreaDeAtuacaoRepository areaRepository;

    @Test
    void deveListarAreasEmOrdemAlfabeticaComOTotalDeCientistas() {
        AreaDeAtuacao quimica = entityManager.persist(new AreaDeAtuacao("Química", "Substâncias."));
        AreaDeAtuacao fisica = entityManager.persist(new AreaDeAtuacao("Física", "Matéria e energia."));
        entityManager.persist(new AreaDeAtuacao("Computação", null));
        Cientista curie = new Cientista("Marie Curie", null, null, null, null, null);
        curie.adicionarArea(fisica);
        curie.adicionarArea(quimica);
        entityManager.persist(curie);
        Cientista meitner = new Cientista("Lise Meitner", null, null, null, null, null);
        meitner.adicionarArea(fisica);
        entityManager.persist(meitner);
        entityManager.flush();
        entityManager.clear();

        assertThat(areaRepository.listarComTotalDeCientistas())
                .extracting(AreaDTO::nome, AreaDTO::totalCientistas)
                .containsExactly(
                        tuple("Computação", 0L),
                        tuple("Física", 2L),
                        tuple("Química", 1L));
    }
}
