package IFMG.CiencialDelas.PortalMulheresNaCiencia.servico;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.SugestaoAdminDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.StatusSugestao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.SugestaoCientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Usuario;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.erro.DadosInvalidosException;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.erro.RecursoNaoEncontradoException;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.SugestaoCientistaRepository;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Avaliação das sugestões enviadas pelo site. */
@Service
public class CuradoriaSugestaoService {

    private final SugestaoCientistaRepository sugestaoRepository;
    private final UsuarioRepository usuarioRepository;

    public CuradoriaSugestaoService(SugestaoCientistaRepository sugestaoRepository, UsuarioRepository usuarioRepository) {
        this.sugestaoRepository = sugestaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** Sugestões mais recentes primeiro. Sem status, traz todas. */
    @Transactional(readOnly = true)
    public List<SugestaoAdminDTO> listar(StatusSugestao status) {
        List<SugestaoCientista> sugestoes = status == null
                ? sugestaoRepository.findAllByOrderByDataSugestaoDescIdDesc()
                : sugestaoRepository.findByStatusOrderByDataSugestaoDescIdDesc(status);
        return sugestoes.stream().map(CuradoriaSugestaoService::paraDTO).toList();
    }

    /** Aprova ou recusa a sugestão e registra quem decidiu. */
    @Transactional
    public SugestaoAdminDTO revisar(Integer id, StatusSugestao status, String emailUsuario) {
        SugestaoCientista sugestao = buscar(id);
        if (status == StatusSugestao.PENDENTE) {
            throw new DadosInvalidosException("status", "Escolha aprovar ou recusar.");
        }
        if (status == StatusSugestao.RECUSADA && sugestao.getCientista() != null) {
            throw new DadosInvalidosException("status", "Esta sugestão já virou um cadastro e não pode ser recusada.");
        }
        sugestao.revisar(status, usuario(emailUsuario));
        return paraDTO(sugestao);
    }

    SugestaoCientista buscar(Integer id) {
        return sugestaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sugestão não encontrada."));
    }

    Usuario usuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    private static SugestaoAdminDTO paraDTO(SugestaoCientista s) {
        return new SugestaoAdminDTO(
                s.getId(),
                s.getNomeCientista(),
                s.getJustificativa(),
                s.getNomeSugestor(),
                s.getEmailSugestor(),
                s.getDataSugestao(),
                s.getStatus().name(),
                s.getRevisor() == null ? null : s.getRevisor().getNome(),
                s.getCientista() == null ? null : s.getCientista().getId());
    }
}
