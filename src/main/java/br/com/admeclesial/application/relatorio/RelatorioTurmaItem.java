package br.com.admeclesial.application.relatorio;
import java.util.UUID;
public record RelatorioTurmaItem(UUID uuid,String turma,String catequese,String etapa,Integer ano,String turno,String status,Integer capacidade,long inscritos,long vagas,String catequistas) {}
