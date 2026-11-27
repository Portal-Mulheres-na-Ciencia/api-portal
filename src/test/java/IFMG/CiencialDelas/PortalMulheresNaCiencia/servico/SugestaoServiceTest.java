package IFMG.CiencialDelas.PortalMulheresNaCiencia.servico;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.NovaSugestaoDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.SugestaoCriadaDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.entidade.SugestaoCientista;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.repositorio.SugestaoCientistaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SugestaoServiceTest {

    @Mock
    private SugestaoCientistaRepository sugestaoRepository;

    @InjectMocks
    private SugestaoService sugestaoService;

    @Test
    void deveGravarASugestaoSemEspacosSobrandoEComEmailEmMinusculas() {
        when(sugestaoRepository.save(any())).thenAnswer(chamada -> chamada.getArgument(0));

        SugestaoCriadaDTO resposta = sugestaoService.registrar(new NovaSugestaoDTO(
                "  Sonia Guimarães ", "Primeira mulher negra doutora em física no Brasil.", " Ana ",
                " Ana@Exemplo.COM ", true));

        ArgumentCaptor<SugestaoCientista> salva = ArgumentCaptor.forClass(SugestaoCientista.class);
        verify(sugestaoRepository).save(salva.capture());
        assertThat(salva.getValue().getNomeCientista()).isEqualTo("Sonia Guimarães");
        assertThat(salva.getValue().getNomeSugestor()).isEqualTo("Ana");
        assertThat(salva.getValue().getEmailSugestor()).isEqualTo("ana@exemplo.com");
        assertThat(resposta.nomeCientista()).isEqualTo("Sonia Guimarães");
        assertThat(resposta.mensagem()).isEqualTo(SugestaoService.MENSAGEM_RECEBIDA);
    }
}
