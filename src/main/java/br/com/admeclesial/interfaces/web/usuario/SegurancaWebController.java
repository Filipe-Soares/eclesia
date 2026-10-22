package br.com.admeclesial.interfaces.web.usuario;
import org.springframework.stereotype.Controller; import org.springframework.web.bind.annotation.GetMapping;
@Controller public class SegurancaWebController { @GetMapping("/login") String login(){return "seguranca/login";} @GetMapping("/acesso-negado") String negado(){return "seguranca/acesso-negado";} @GetMapping("/") String inicio(){return "redirect:/pessoas";} }
