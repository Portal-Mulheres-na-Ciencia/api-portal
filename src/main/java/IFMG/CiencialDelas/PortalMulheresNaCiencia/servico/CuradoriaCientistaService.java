package IFMG.CiencialDelas.PortalMulheresNaCiencia.servico;

import static IFMG.CiencialDelas.PortalMulheresNaCiencia.servico.PremioService.textoOuNulo;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaDetalheDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.CientistaFormDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.ContribuicaoFormDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.PremioRecebidoFormDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.AreaDeAtuacao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Cientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Contribuicao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.Premio;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.PremioRecebido;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.StatusSugestao;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.SugestaoCientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.erro.DadosInvalidosException;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.erro.RecursoNaoEncontradoException;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.AreaDeAtuacaoRepository;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.CientistaRepository;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.ContribuicaoRepository;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.PremioRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cadastro, edição e exclusão de cientistas pela equipe. */
@Service
public class CuradoriaCientistaService {

    private final CientistaRepository cientistaRepository;
    private final AreaDeAtuacaoRepository areaRepository;
    private final PremioRepository premioRepository;
    private final ContribuicaoRepository contribuicaoRepository;
    private final CuradoriaSugestaoService sugestaoService;
    private final CientistaService cientistaService;

    public CuradoriaCientistaService(CientistaRepository cientistaRepository, AreaDeAtuacaoRepository areaRepository,
            PremioRepository premioRepository, ContribuicaoRepository contribuicaoRepository,
            CuradoriaSugestaoService sugestaoService, CientistaService cientistaService) {
        this.cientistaRepository = cientistaRepository;
        this.areaRepository = areaRepository;
        this.premioRepository = premioRepository;
        this.contribuicaoRepository = contribuicaoRepository;
        this.sugestaoService = sugestaoService;
        this.cientistaService = cientistaService;
    }

    /** Cadastra a cientista. Se veio de uma sugestão, liga as duas e marca a sugestão como aprovada. */
    @Transactional
    public CientistaDetalheDTO criar(CientistaFormDTO dados, String emailUsuario) {
        Cientista cientista = new Cientista();
        preencher(cientista, dados);
        if (dados.idSugestao() != null) {
            ligarSugestao(cientista, dados.idSugestao(), emailUsuario);
        }
        cientistaRepository.save(cientista);
        return cientistaService.paraDetalhe(cientista);
    }

    @Transactional
    public CientistaDetalheDTO atualizar(Integer id, CientistaFormDTO dados) {
        Cientista cientista = buscar(id);
        preencher(cientista, dados);
        return cientistaService.paraDetalhe(cientista);
    }

    /** Exclui a cientista, os prêmios recebidos e as contribuições que eram só dela. */
    @Transactional
    public void excluir(Integer id) {
        Cientista cientista = buscar(id);
        for (Contribuicao contribuicao : List.copyOf(cientista.getContribuicoes())) {
            desligar(cientista, contribuicao);
        }
        // A sugestão de origem continua gravada (aprovada), só sem a cientista.
        if (cientista.getSugestao() != null) {
            cientista.getSugestao().desligarCientista();
            cientista.setSugestao(null);
        }
        cientistaRepository.delete(cientista);
    }

    private Cientista buscar(Integer id) {
        return cientistaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cientista não encontrada."));
    }

    private void preencher(Cientista cientista, CientistaFormDTO dados) {
        if (dados.dataNascimento() != null && dados.dataFalecimento() != null
                && dados.dataFalecimento().isBefore(dados.dataNascimento())) {
            throw new DadosInvalidosException("dataFalecimento", "A data de falecimento deve ser depois do nascimento.");
        }
        cientista.setNome(dados.nome().strip());
        cientista.setBiografia(dados.biografia().strip());
        cientista.setInstituicao(textoOuNulo(dados.instituicao()));
        cientista.setDataNascimento(dados.dataNascimento());
        cientista.setDataFalecimento(dados.dataFalecimento());
        cientista.setNacionalidade(textoOuNulo(dados.nacionalidade()));
        atualizarAreas(cientista, dados.idsAreas());
        atualizarContribuicoes(cientista, dados.contribuicoes());
        atualizarPremios(cientista, dados.premios());
    }

    private void atualizarAreas(Cientista cientista, List<Integer> idsAreas) {
        Set<Integer> ids = new LinkedHashSet<>(idsAreas);
        List<AreaDeAtuacao> areas = areaRepository.findAllById(ids);
        if (areas.size() != ids.size()) {
            throw new DadosInvalidosException("idsAreas", "Escolha áreas que existem na lista.");
        }
        cientista.getAreasDeAtuacao().clear();
        cientista.getAreasDeAtuacao().addAll(areas);
    }

    /** Atualiza as contribuições que já existiam, cria as novas e desliga as que saíram da lista. */
    private void atualizarContribuicoes(Cientista cientista, List<ContribuicaoFormDTO> lista) {
        Map<Integer, Contribuicao> atuais = new HashMap<>();
        for (Contribuicao c : cientista.getContribuicoes()) {
            atuais.put(c.getId(), c);
        }
        Set<Contribuicao> mantidas = new HashSet<>();
        for (int i = 0; i < lista.size(); i++) {
            ContribuicaoFormDTO dados = lista.get(i);
            String titulo = dados.titulo().strip();
            String descricao = textoOuNulo(dados.descricao());
            String link = textoOuNulo(dados.linkReferencia());
            if (dados.id() == null) {
                Contribuicao nova = new Contribuicao(titulo, descricao, dados.ano(), link);
                cientista.adicionarContribuicao(nova);
                mantidas.add(nova);
            } else {
                Contribuicao existente = atuais.get(dados.id());
                if (existente == null) {
                    throw new DadosInvalidosException("contribuicoes[" + i + "].titulo",
                            "Esta contribuição não pertence a esta cientista.");
                }
                existente.atualizar(titulo, descricao, dados.ano(), link);
                mantidas.add(existente);
            }
        }
        for (Contribuicao contribuicao : List.copyOf(cientista.getContribuicoes())) {
            if (!mantidas.contains(contribuicao)) {
                desligar(cientista, contribuicao);
            }
        }
    }

    /** Tira a contribuição da cientista e apaga a contribuição se ela não tiver mais nenhuma cientista. */
    private void desligar(Cientista cientista, Contribuicao contribuicao) {
        cientista.removerContribuicao(contribuicao);
        if (contribuicao.getCientistas().isEmpty()) {
            contribuicaoRepository.delete(contribuicao);
        }
    }

    /** Mantém os prêmios que continuam na lista (atualizando o ano), inclui os novos e remove os que saíram. */
    private void atualizarPremios(Cientista cientista, List<PremioRecebidoFormDTO> lista) {
        Set<Integer> ids = new HashSet<>();
        for (int i = 0; i < lista.size(); i++) {
            if (!ids.add(lista.get(i).idPremio())) {
                throw new DadosInvalidosException("premios[" + i + "].idPremio", "Este prêmio já está na lista.");
            }
        }
        Map<Integer, Premio> premios = new HashMap<>();
        for (Premio premio : premioRepository.findAllById(ids)) {
            premios.put(premio.getId(), premio);
        }
        for (int i = 0; i < lista.size(); i++) {
            if (!premios.containsKey(lista.get(i).idPremio())) {
                throw new DadosInvalidosException("premios[" + i + "].idPremio", "Escolha um prêmio da lista.");
            }
        }

        cientista.getPremios().removeIf(recebido -> !ids.contains(recebido.getPremio().getId()));
        Map<Integer, PremioRecebido> atuais = new HashMap<>();
        for (PremioRecebido recebido : cientista.getPremios()) {
            atuais.put(recebido.getPremio().getId(), recebido);
        }
        for (PremioRecebidoFormDTO dados : lista) {
            PremioRecebido existente = atuais.get(dados.idPremio());
            if (existente == null) {
                // Só o ano do prêmio é informado; a data fica em 1º de janeiro e o site mostra apenas o ano.
                cientista.adicionarPremio(premios.get(dados.idPremio()), LocalDate.of(dados.ano(), 1, 1));
            } else if (existente.getRecebidoEm().getYear() != dados.ano()) {
                existente.setRecebidoEm(LocalDate.of(dados.ano(), 1, 1));
            }
        }
    }

    private void ligarSugestao(Cientista cientista, Integer idSugestao, String emailUsuario) {
        SugestaoCientista sugestao = sugestaoService.buscar(idSugestao);
        if (sugestao.getCientista() != null) {
            throw new DadosInvalidosException("idSugestao", "Esta sugestão já virou um cadastro.");
        }
        cientista.setSugestao(sugestao);
        sugestao.revisar(StatusSugestao.APROVADA, sugestaoService.usuario(emailUsuario));
    }
}
