package IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.AreaDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.AreaDeAtuacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AreaDeAtuacaoRepository extends JpaRepository<AreaDeAtuacao, Integer> {

    /** Todas as áreas em ordem alfabética, com quantas cientistas atuam em cada uma (inclusive zero). */
    @Query("""
            select new IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.AreaDTO(a.id, a.nome, a.descricao, count(c))
            from AreaDeAtuacao a left join a.cientistas c
            group by a.id, a.nome, a.descricao
            order by a.nome
            """)
    List<AreaDTO> listarComTotalDeCientistas();
}
