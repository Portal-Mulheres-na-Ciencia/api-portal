package IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Premio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PremioRepository extends JpaRepository<Premio, Integer> {

    List<Premio> findAllByOrderByNomeAsc();

    boolean existsByNomeIgnoreCase(String nome);
}
