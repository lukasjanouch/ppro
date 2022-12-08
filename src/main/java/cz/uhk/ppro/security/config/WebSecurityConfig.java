package cz.uhk.ppro.security.config;

import cz.uhk.ppro.user.UserRole;
import cz.uhk.ppro.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.concurrent.TimeUnit;

@Configuration
@AllArgsConstructor//generates a constructor with 1 parameter for each field in your class. Fields marked with @NonNull result in null checks on those parameters.
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {//přesnější požadavky specifikovat dřív než obecnější
        //z kurzu login-registration: (na 3. řádku původně .antMatchers("/api/v*/registration/**")
        http
                //.csrf().disable()//disable jen u postmanu
                //.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                //.and()
                .authorizeRequests()
                .antMatchers(  "/registrace/**", "/register/**", "/error", "/", "/index", "/css/**", "/fonts/**", "/images/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                    .loginPage("/login").permitAll()//zobrazení vlastní přihlašovací stránky
                    .defaultSuccessUrl("/moje-galerie", true)//stránka, která se zobrazí po přihlášení
                    .usernameParameter("username")//souvisí s atributem name v html souborech, musí mít stejnou hodnotu, pokud tuto výchozí, múže se tady tenhle řádek vynechat
                    .passwordParameter("password")
                .and()
                .rememberMe()// session id vydrží déle než výhozích 30 minut (2 týdny)
                    .tokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(30))//prodloužení ze 2 týdnů na 30 dní
                    .key("md5hash")//hash už. jména a hesla a délky trvání session id
                    .rememberMeParameter("remember-me")
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "remember-me")
                    .logoutSuccessUrl("/login");
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
/*http
                .csrf().disable()//pouze, když pracujeme s postmanem, aby se negeneroval token, jinak nepoužívat/zpřístupnit, smazat i s csrf()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(UserRole.USER.name())
                .anyRequest()
                .authenticated()//pouze přihlášený uživatel
                .and()
                .formLogin();
                //.loginPage("/login").permitAll();*/

//basicLogin() - nejde se odhlásit

//.permitAll()//veřejné
//"/api/v1/students" - pasuje s "/api/**"
//.antMatchers("/api/v*/registration/**")
//.antMatchers("/api/**").hasRole(UserRole.USER.name())
//anyRequest se týká antMatchers()
//antMatchers() - stránky, na které nebude potřeba se přihlásit - možná
//na pořadí antMatcherů záleží, vyhodnocují se odshora, když nějaký neplatí, celé se to zastaví