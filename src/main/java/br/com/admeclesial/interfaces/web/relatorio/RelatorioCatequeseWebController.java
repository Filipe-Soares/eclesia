package br.com.admeclesial.interfaces.web.relatorio;
import br.com.admeclesial.application.relatorio.*; import br.com.admeclesial.domain.catequese.*; import org.springframework.stereotype.Controller; import org.springframework.ui.Model; import org.springframework.web.bind.annotation.*; import java.util.UUID;
@Controller @RequestMapping("/relatorios/catequese") public class RelatorioCatequeseWebController {
 private final RelatorioCatequeseApplicationService service; private final RelatorioContextoService contexto;
 public RelatorioCatequeseWebController(RelatorioCatequeseApplicationService s,RelatorioContextoService c){service=s;contexto=c;}
 @GetMapping public String consultar(@RequestParam(required=false) UUID organizacaoUuid,@RequestParam(required=false) UUID catequeseUuid,@RequestParam(required=false) UUID etapaUuid,@RequestParam(required=false) Integer ano,@RequestParam(required=false) StatusCatequese status,@RequestParam(required=false) TurnoCatequese turno,@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="25") int tamanho,Model m){
  m.addAttribute("organizacoes",contexto.organizacoesAutorizadas());m.addAttribute("organizacaoUuid",organizacaoUuid);m.addAttribute("catequeseUuid",catequeseUuid);m.addAttribute("etapaUuid",etapaUuid);m.addAttribute("ano",ano);m.addAttribute("status",status);m.addAttribute("turno",turno);m.addAttribute("statusOpcoes",StatusCatequese.values());m.addAttribute("turnoOpcoes",TurnoCatequese.values());
  if(organizacaoUuid!=null){m.addAttribute("catequeses",service.catequeses(organizacaoUuid));m.addAttribute("etapas",service.etapas(organizacaoUuid,catequeseUuid));m.addAttribute("resultado",service.consultar(new RelatorioCatequeseFiltro(organizacaoUuid,catequeseUuid,etapaUuid,ano,status,turno,pagina,tamanho)));} return "relatorio/catequese";
 }
}
