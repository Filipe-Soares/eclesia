package br.com.admeclesial.application.financeiro;

import br.com.admeclesial.domain.financeiro.*;
import br.com.admeclesial.infrastructure.persistence.financeiro.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObrigacaoRepasseApplicationServiceTest {
    @Mock ObrigacaoRepasseRepository repo;
    @Mock RegraRepasseRepository regra;
    @Mock ContaAPagarRepository contas;
    @Mock ParcelaAPagarRepository parcelas;

    @Test
    void deveCalcularRepasseSobreBaseLiquida() {
        var r = new RegraRepasse();
        r.setTipoBase(TipoBaseRepasse.CONTRIBUICOES);
        r.setPercentual(new BigDecimal("10"));
        UUID u = UUID.randomUUID();
        when(regra.findByUuid(u)).thenReturn(Optional.of(r));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(contas.save(any())).thenAnswer(i -> i.getArgument(0));
        when(parcelas.save(any())).thenAnswer(i -> i.getArgument(0));
        var s = new ObrigacaoRepasseApplicationService(repo, regra, contas, parcelas);
        var out = s.gerar(new ObrigacaoRepasseRequest(u, LocalDate.of(2026,9,1), new BigDecimal("1000"), new BigDecimal("200"), LocalDate.of(2026,10,10), null));
        assertEquals(new BigDecimal("80.00"), out.getValorRepasse().setScale(2));
    }
}
