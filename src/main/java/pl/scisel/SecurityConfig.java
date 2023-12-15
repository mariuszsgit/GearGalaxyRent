package pl.scisel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/create-user").permitAll()
                        .requestMatchers("/**").permitAll()

                        //.requestMatchers("/admin/**").hasRole("ADMIN")
                        //.requestMatchers("/user/**").hasRole("USER")

                        .requestMatchers("/about").permitAll()
                        //.requestMatchers("/about").authenticated()

                        //.anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login") // Określa ścieżkę do Twojej strony logowania
                                .loginProcessingUrl("/perform_login") // URL, na który formularz logowania będzie wysyłany
                                .defaultSuccessUrl("/", true) // Strona docelowa po udanym logowaniu
                                .permitAll() // Zezwól wszystkim na dostęp do strony logowania

                )
                .logout(logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // Użyj GET jako metody dla wylogowania
                                //.logoutSuccessUrl("/login?logout") // URL przekierowania po wylogowaniu
                                .logoutSuccessUrl("/?logout") // URL przekierowania po wylogowaniu
                                .invalidateHttpSession(true) // Unieważnienie sesji po wylogowaniu
                                .deleteCookies("JSESSIONID") // Usunięcie ciasteczka sesji
                                .clearAuthentication(true) // Usunięcie danych uwierzytelnienia
                                .permitAll()
                /*)
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL, który inicjuje proces wylogowania
                        .logoutSuccessUrl("/login?logout") // URL przekierowania po wylogowaniu
                        .invalidateHttpSession(true) // Niezbędne, aby unieważnić sesję po wylogowaniu
                        .deleteCookies("JSESSIONID") // Usuń ciasteczka sesji (opcjonalnie)
                        .permitAll()*/
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

/*    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("password")
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }*/

/*    @Bean
    public UserDetailsService users() {
        UserDetails user = User.builder()
                .username("user")
                .password("$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("admin")
                .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }*/

}