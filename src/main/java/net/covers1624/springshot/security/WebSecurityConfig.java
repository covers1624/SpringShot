package net.covers1624.springshot.security;

import net.covers1624.springshot.entity.ApiKey;
import net.covers1624.springshot.entity.User;
import net.covers1624.springshot.repo.ApiKeyRepository;
import net.covers1624.springshot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

/**
 * Created by covers1624 on 5/8/20.
 */
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserService userService;
    private final ApiKeyRepository apiKeyRepo;

    public WebSecurityConfig(UserService userService, ApiKeyRepository apiKeyRepo) {
        this.userService = userService;
        this.apiKeyRepo = apiKeyRepo;
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @Order (1)
    @Configuration
    public class ApiSecurity extends WebSecurityConfigurerAdapter {

        private final UserDetailsChecker checker = new ApiUserDetailsChecker();

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            ApiKeyAuthFilter filter = new ApiKeyAuthFilter();
            filter.setAuthenticationManager(authentication -> {
                String principal = (String) authentication.getPrincipal();
                Optional<ApiKey> keyOpt = apiKeyRepo.findBySecret(principal);
                ApiKey key = keyOpt.orElseThrow(() -> new BadCredentialsException("Invalid API Key."));
                User user = key.getUser();
                checker.check(user);
                return new UsernamePasswordAuthenticationToken(user, key, user.getAuthorities());
            });

            http.headers().disable().antMatcher("/api/**")//
                    .csrf().disable()//
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//
                    .and().addFilter(filter).authorizeRequests().anyRequest().authenticated();//
        }
    }

    @Configuration
    public static class WebPanelSecurity extends WebSecurityConfigurerAdapter {

        protected void configure(HttpSecurity http) throws Exception {
            http.headers().disable().authorizeRequests()//
                    .antMatchers("/panel/resource/**").permitAll()
                    .antMatchers("/panel/register/**", "/panel/login/**").permitAll()//
                    .antMatchers("/panel", "/panel/**").authenticated()//
                    .and()//
                    .formLogin()//
                    .loginPage("/panel/login")//
                    .defaultSuccessUrl("/panel/account").permitAll();//
        }

    }
}
