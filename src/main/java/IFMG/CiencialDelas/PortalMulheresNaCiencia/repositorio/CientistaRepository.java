package IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Cientista;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CientistaRepository extends JpaRepository<Cientista, Integer> {

    /**
     * Todas as cientistas em ordem alfabética, já com as áreas carregadas.
     * O EntityGraph busca tudo em uma consulta, em vez de uma consulta extra por cientista.
     */
    @EntityGraph(attributePaths = "areasDeAtuacao")
    List<Cientista> findAllByOrderByNomeAsc();
}
