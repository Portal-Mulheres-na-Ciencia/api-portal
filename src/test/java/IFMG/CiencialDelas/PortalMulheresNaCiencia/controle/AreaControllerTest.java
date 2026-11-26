package IFMG.CiencialDelas.PortalMulheresNaCiencia.controle;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import IFMG.CiencialDelas.PortalMulheresNaCiencia.config.CorsConfig;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.dto.AreaDTO;
import IFMG.CiencialDelas.PortalMulheresNaCiencia.servico.AreaService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AreaController.class)
@Import(CorsConfig.class)
class AreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AreaService areaService;

    @Test
    void deveRetornarAsAreasComOTotalDeCientistas() throws Exception {
        when(areaService.listar()).thenReturn(List.of(new AreaDTO(1, "Física", "Matéria e energia.", 2L)));

        mockMvc.perform(get("/areas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Física"))
                .andExpect(jsonPath("$[0].descricao").value("Matéria e energia."))
                .andExpect(jsonPath("$[0].totalCientistas").value(2));
    }
}
