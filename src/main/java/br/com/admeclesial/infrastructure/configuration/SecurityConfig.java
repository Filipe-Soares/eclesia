package br.com.admeclesial.infrastructure.configuration;
import org.springframework.context.annotation.*; import org.springframework.http.HttpMethod; import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; import org.springframework.security.config.annotation.web.builders.HttpSecurity; import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.security.web.SecurityFilterChain;
@Configuration @EnableMethodSecurity public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{ http.authorizeHttpRequests(a->a
  .requestMatchers("/login","/acesso-negado","/css/**","/js/**","/images/**","/webjars/**","/actuator/health").permitAll()
  .requestMatchers("/usuarios/**").hasRole("ADMINISTRADOR")
  .requestMatchers(HttpMethod.GET,"/dashboard").hasAnyRole("ADMINISTRADOR","FINANCEIRO","SECRETARIA","CATEQUESE","CONSULTA")
  .requestMatchers(HttpMethod.GET,"/relatorios/pessoas/**").hasAnyRole("ADMINISTRADOR","SECRETARIA","CATEQUESE","FINANCEIRO","CONSULTA")
  .requestMatchers(HttpMethod.GET,"/relatorios/catequese/**").hasAnyRole("ADMINISTRADOR","SECRETARIA","CATEQUESE","CONSULTA")
  .requestMatchers(HttpMethod.GET,"/relatorios/sacramentos/**").hasAnyRole("ADMINISTRADOR","SECRETARIA","CATEQUESE","CONSULTA")
  .requestMatchers(HttpMethod.GET,"/relatorios/contribuicoes/**").hasAnyRole("ADMINISTRADOR","FINANCEIRO")
  .requestMatchers(HttpMethod.GET,"/relatorios/movimentacoes-financeiras/**").hasAnyRole("ADMINISTRADOR","FINANCEIRO")
  .requestMatchers(HttpMethod.GET,"/relatorios/contas-a-pagar/**").hasAnyRole("ADMINISTRADOR","FINANCEIRO")
  .requestMatchers(HttpMethod.GET,"/relatorios/repasses-curia/**").hasAnyRole("ADMINISTRADOR","FINANCEIRO")
  .requestMatchers(HttpMethod.GET,"/relatorios/projetos-obras/**").hasAnyRole("ADMINISTRADOR","FINANCEIRO","CONSULTA")
  .requestMatchers("/financeiro/**","/api/v1/contas-financeiras/**","/api/v1/categorias-financeiras/**","/api/v1/centros-custo/**","/api/v1/fornecedores/**","/api/v1/movimentacoes-financeiras/**","/api/v1/contribuicoes/**","/api/v1/destinacoes-contribuicao/**","/api/v1/dizimistas/**","/api/v1/transferencias-financeiras/**","/api/v1/regras-repasse/**","/api/v1/obrigacoes-repasse/**","/api/v1/contas-a-pagar/**","/api/v1/pagamentos/**","/api/v1/orcamentos/**","/api/v1/recursos-vinculados/**").hasAnyRole("ADMINISTRADOR","FINANCEIRO")
  .requestMatchers(HttpMethod.GET,"/catequeses/**","/api/v1/catequeses/**","/api/v1/etapas-catequese/**","/api/v1/turmas-catequese/**","/api/v1/inscricoes-catequese/**","/api/v1/catequistas-turma/**").hasAnyRole("ADMINISTRADOR","SECRETARIA","CATEQUESE","CONSULTA")
  .requestMatchers("/catequeses/**","/api/v1/catequeses/**","/api/v1/etapas-catequese/**","/api/v1/turmas-catequese/**","/api/v1/inscricoes-catequese/**","/api/v1/catequistas-turma/**").hasAnyRole("ADMINISTRADOR","CATEQUESE")
  .requestMatchers(HttpMethod.GET,"/sacramentos/**","/api/v1/sacramentos/**","/api/v1/participantes-sacramento/**","/api/v1/registros-sacramentais/**").hasAnyRole("ADMINISTRADOR","SECRETARIA","CATEQUESE","CONSULTA")
  .requestMatchers("/sacramentos/**","/api/v1/sacramentos/**","/api/v1/participantes-sacramento/**","/api/v1/registros-sacramentais/**").hasAnyRole("ADMINISTRADOR","SECRETARIA")
  .requestMatchers(HttpMethod.GET,"/matrimonios/**","/processos-matrimoniais/**","/api/v1/processos-matrimoniais/**","/api/v1/celebracoes-matrimoniais/**","/api/v1/matrimonios/**").hasAnyRole("ADMINISTRADOR","SECRETARIA","CONSULTA")
  .requestMatchers("/matrimonios/**","/processos-matrimoniais/**","/api/v1/processos-matrimoniais/**","/api/v1/celebracoes-matrimoniais/**","/api/v1/matrimonios/**").hasAnyRole("ADMINISTRADOR","SECRETARIA")
  .requestMatchers(HttpMethod.GET,"/projetos/**","/api/v1/projetos/**").hasAnyRole("ADMINISTRADOR","SECRETARIA","FINANCEIRO","CONSULTA")
  .requestMatchers("/projetos/**","/api/v1/projetos/**").hasAnyRole("ADMINISTRADOR","FINANCEIRO")
  .requestMatchers(HttpMethod.GET,"/pessoas/**","/api/v1/pessoas/**","/familias/**","/api/v1/familias/**","/api/v1/membros-familia/**","/api/v1/relacionamentos-pessoa/**").hasAnyRole("ADMINISTRADOR","SECRETARIA","CATEQUESE","FINANCEIRO","CONSULTA")
  .requestMatchers("/pessoas/**","/api/v1/pessoas/**","/familias/**","/api/v1/familias/**","/api/v1/membros-familia/**","/api/v1/relacionamentos-pessoa/**").hasAnyRole("ADMINISTRADOR","SECRETARIA")
  .requestMatchers(HttpMethod.GET,"/organizacoes-eclesiais/**","/api/v1/organizacoes-eclesiais/**","/funcoes-eclesiais/**","/vinculos-pessoa-organizacao/**","/vinculos-funcoes-eclesiais/**","/api/v1/funcoes-eclesiais/**","/api/v1/vinculos-pessoa-organizacao/**","/api/v1/vinculos-funcoes-eclesiais/**").authenticated()
  .requestMatchers("/organizacoes-eclesiais/**","/api/v1/organizacoes-eclesiais/**").hasRole("ADMINISTRADOR")
  .requestMatchers("/funcoes-eclesiais/**","/vinculos-pessoa-organizacao/**","/vinculos-funcoes-eclesiais/**","/api/v1/funcoes-eclesiais/**","/api/v1/vinculos-pessoa-organizacao/**","/api/v1/vinculos-funcoes-eclesiais/**").hasAnyRole("ADMINISTRADOR","SECRETARIA")
  .anyRequest().authenticated())
  .formLogin(f->f.loginPage("/login").defaultSuccessUrl("/",true).permitAll())
  .logout(l->l.logoutSuccessUrl("/login?logout").permitAll())
  .exceptionHandling(e->e.accessDeniedPage("/acesso-negado")); return http.build(); }
}
