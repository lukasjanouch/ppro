package cz.uhk.ppro.security.config;

import cz.uhk.ppro.user.UserRole;
import cz.uhk.ppro.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@AllArgsConstructor//generates a constructor with 1 parameter for each field in your class. Fields marked with @NonNull result in null checks on those parameters.
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {//přesnější požadavky specifikovat dřív než obecnější
        http
                .csrf().disable()//pouze, když pracujeme s postmanem, jinak nepoužívat/zpřístupnit
                .authorizeRequests()
                .antMatchers("/api/v*/registration/**")
                .permitAll()//veřejné
                .anyRequest()
                .authenticated()//pouze přihlášený uživatel
                .and()
                .formLogin();
                //.loginPage("/login").permitAll();

        //.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
        //.antMatchers("/api/**").hasRole(UserRole.USER.name())
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);//bcrypt - oblíbená kryptografycká hashovací fce
        provider.setUserDetailsService(userService);
        return provider;
    }
}
