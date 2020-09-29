package se.sigma.boostapp.boost_app_java.security;

import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * 
 * @author SigmaIT
 *
 */
@EnableWebSecurity
@Profile("prod")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity http) throws Exception {
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().mvcMatcher("/steps/**")
            .authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .oauth2ResourceServer().jwt();
    }
}
