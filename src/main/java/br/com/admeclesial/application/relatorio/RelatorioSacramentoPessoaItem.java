package br.com.admeclesial.application.relatorio;
import java.time.LocalDate; import java.util.UUID;
public record RelatorioSacramentoPessoaItem(UUID pessoaUuid,String pessoa,String tipo,LocalDate dataCelebracao,UUID sacramentoUuid,String status) {}
