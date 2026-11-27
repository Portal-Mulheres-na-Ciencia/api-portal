package IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.StatusSugestao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.SugestaoCientista;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SugestaoCientistaRepository extends JpaRepository<SugestaoCientista, Integer> {

    /** Todas as sugestões, das mais recentes para as mais antigas. */
    List<SugestaoCientista> findAllByOrderByDataSugestaoDescIdDesc();

    List<SugestaoCientista> findByStatusOrderByDataSugestaoDescIdDesc(StatusSugestao status);
}
