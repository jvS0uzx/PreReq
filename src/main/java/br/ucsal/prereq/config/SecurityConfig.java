package br.ucsal.prereq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Só a área administrativa exige login. O aluno usa o resto do sistema sem conta.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtrosDeSeguranca(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(rotas -> rotas
                        .requestMatchers("/admin/**").authenticated()
                        // Rotas do aluno (/triagem, /categorias, /prazos, /espera, /indeferimentos),
                        // arquivos estáticos, /error e /actuator/health ficam abertas.
                        .anyRequest().permitAll())
                .formLogin(formulario -> formulario.permitAll())
                .logout(saida -> saida.logoutSuccessUrl("/"));
        return http.build();
    }

    @Bean
    public PasswordEncoder codificadorDeSenha() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /** Usuário único do admin, lido de ADMIN_USER e ADMIN_PASSWORD (.env ou ambiente). */
    @Bean
    public UserDetailsService usuarioAdmin(
            @Value("${ADMIN_USER}") String usuario,
            @Value("${ADMIN_PASSWORD}") String senha,
            PasswordEncoder codificador) {
        return new InMemoryUserDetailsManager(User.withUsername(usuario)
                .password(codificador.encode(senha))
                .roles("ADMIN")
                .build());
    }
}
