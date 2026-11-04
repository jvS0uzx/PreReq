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

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtrosDeSeguranca(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(rotas -> rotas
                        .requestMatchers("/admin/**").authenticated()
                        .anyRequest().permitAll())
                .formLogin(formulario -> formulario.defaultSuccessUrl("/admin"))
                .logout(saida -> saida.logoutSuccessUrl("/"));
        return http.build();
    }

    @Bean
    public PasswordEncoder codificadorDeSenha() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

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
