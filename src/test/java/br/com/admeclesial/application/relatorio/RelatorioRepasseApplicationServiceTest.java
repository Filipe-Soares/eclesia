package br.com.admeclesial.application.relatorio;
import br.com.admeclesial.application.usuario.SegurancaOrganizacaoService; import br.com.admeclesial.infrastructure.persistence.relatorio.RelatorioRepasseRepository; import org.junit.jupiter.api.Test; import org.junit.jupiter.api.extension.ExtendWith; import org.mockito.Mock; import org.mockito.junit.jupiter.MockitoExtension; import java.util.UUID; import static org.junit.jupiter.api.Assertions.*; import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class) class RelatorioRepasseApplicationServiceTest { @Mock RelatorioRepasseRepository repo; @Mock SegurancaOrganizacaoService seguranca;
 @Test void exigeOrganizacao(){var s=new RelatorioRepasseApplicationService(repo,seguranca);var f=new RelatorioRepasseFiltro(null,null,null,null,true,0,25);assertThrows(IllegalArgumentException.class,()->s.pesquisar(f));}
 @Test void validaAcessoAntesDaConsulta(){var org=UUID.randomUUID();var s=new RelatorioRepasseApplicationService(repo,seguranca);var f=new RelatorioRepasseFiltro(org,null,null,null,true,0,25);assertThrows(NullPointerException.class,()->s.pesquisar(f));verify(seguranca).validarAcesso(org);}
}
