package br.com.admeclesial.application.relatorio;
import java.time.LocalDate; import java.util.UUID;
public record RelatorioSacramentoRegistroItem(UUID uuid,String tipo,LocalDate dataCelebracao,String pessoaPrincipal,String local,String status,String livro,String folha,String termo,String numeroRegistro) {}
