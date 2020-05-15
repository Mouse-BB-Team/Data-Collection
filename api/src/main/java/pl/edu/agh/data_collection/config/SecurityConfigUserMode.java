package pl.edu.agh.data_collection.config;

import org.hibernate.validator.constraints.br.CNPJ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile(ProfileType.USERS_CREATE_THEMSELVES)
@EnableWebSecurity
class SecurityConfigUserMode extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceConfig userDetailsServiceConfig;
    private final PasswordEncoder encoder;

    @Autowired
    public SecurityConfigUserMode(UserDetailsServiceConfig userDetailsServiceConfig, PasswordEncoder encoder) {
        this.userDetailsServiceConfig = userDetailsServiceConfig;
        this.encoder = encoder;
    }

    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceConfig).passwordEncoder(encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.POST, ContextPath.USER_MAIN_PATH + ContextPath.USER_CREATE_PATH);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
