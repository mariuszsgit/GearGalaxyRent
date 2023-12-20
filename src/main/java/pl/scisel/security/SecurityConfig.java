package pl.scisel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(
                        (requests) -> requests
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/user/**").hasAuthority("ROLE_USER")
                                .requestMatchers("/user/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/user/item/edit/*").hasAuthority("ROLE_USER")
                                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers(
                                        "/user/item/list",
                                        "/user/item/add",
                                        "/user/item/edit",
                                        "/user/item/delete/*").authenticated()
                                .requestMatchers(
                                        "/user/rental/list",
                                        "/user/rental/add",
                                        "/user/rental/edit",
                                        "/user/rental/delete/*",
                                        "/user/lease/list").authenticated()

                                .requestMatchers("/item/add", "/item/list", "/item/edit/*").authenticated()

                        // .requestMatchers("/about").permitAll()
                        //.requestMatchers("/create-user").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // Określa ścieżkę do Twojej strony logowania
                        .loginProcessingUrl("/perform_login") // URL, na który formularz logowania będzie wysyłany
                        .permitAll() // Zezwól wszystkim na dostęp do strony logowania
                        .successHandler(new SavedRequestAwareAuthenticationSuccessHandler())
                        //.defaultSuccessUrl("/", true) // Strona docelowa po udanym logowaniu, wyklucza .successHandler
                        .failureUrl("/login?error=true") // Strona docelowa po nieudanym logowaniu
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // Użyj GET jako metody dla wylogowania
                        .logoutSuccessUrl("/?logout") // URL przekierowania po wylogowaniu
                        .invalidateHttpSession(true) // Unieważnienie sesji po wylogowaniu
                        .deleteCookies("JSESSIONID") // Usunięcie ciasteczka sesji (opcjonalnie)
                        .clearAuthentication(true) // Usunięcie danych uwierzytelnienia
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}