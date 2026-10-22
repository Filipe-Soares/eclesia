package br.com.admeclesial.application.catequese;
import java.util.UUID;
public class CatequeseNotFoundException extends RuntimeException { public CatequeseNotFoundException(UUID uuid){super("Catequese não encontrada: "+uuid);} }
