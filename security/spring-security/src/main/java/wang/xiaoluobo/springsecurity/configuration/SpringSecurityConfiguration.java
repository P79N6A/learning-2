package wang.xiaoluobo.springsecurity.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import wang.xiaoluobo.springsecurity.model.MenuEntity;
import wang.xiaoluobo.springsecurity.model.RoleEntity;
import wang.xiaoluobo.springsecurity.service.MenuService;
import wang.xiaoluobo.springsecurity.service.UserService;
import wang.xiaoluobo.springsecurity.service.security.MyAuthenticationProcessingFilter;
import wang.xiaoluobo.springsecurity.service.security.MyUserDetailsService;

import java.util.List;

/**
 * @author wangyd
 * @date 2018/8/31
 */
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_PAGE = "/login";
    private static final String FAILURE_URL = "/error";
    private static final String SUCCESS_URL = "/";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    public SpringSecurityConfiguration() {
        super(true);
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.securityContext().securityContextRepository(securityContextRepository());

        List<RoleEntity> roleEntityList = userService.getRoleList();
        List<MenuEntity> menuEntityList = menuService.getMenuList();
        for (MenuEntity menuEntity : menuEntityList) {
            String[] roleIds = menuEntity.getRoleIds().split(",");
            String[] roleNames = new String[roleIds.length];
            for(int i=0;i<roleIds.length;i++) {
                for (RoleEntity roleEntity : roleEntityList) {
                    if(roleEntity.getRoleId() == Long.parseLong(roleIds[i])){
                        roleNames[i] = roleEntity.getName();
                    }
                }
            }
            http.authorizeRequests().antMatchers(menuEntity.getUrl()).hasAnyRole(roleNames);
        }

        http.authorizeRequests()
                .antMatchers("/static/**").permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .antMatchers("/oauth/token").permitAll()
                .antMatchers("/**").hasRole("USER")
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .anonymous()
                .and()
                .formLogin().loginPage(LOGIN_PAGE).failureUrl(FAILURE_URL).defaultSuccessUrl(SUCCESS_URL).permitAll()
                .and()
                .logout().permitAll()
                .and()
                .httpBasic().disable();
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return myUserDetailsService;
    }

    @Bean
    public MyAuthenticationProcessingFilter myAuthenticationProcessingFilter() throws Exception {
        MyAuthenticationProcessingFilter myAuthenticationProcessingFilter = new MyAuthenticationProcessingFilter(LOGIN_PAGE);
        myAuthenticationProcessingFilter.getFailureHandler().setDefaultFailureUrl(FAILURE_URL);
        myAuthenticationProcessingFilter.setAuthenticationManager(this.authenticationManager());
        return myAuthenticationProcessingFilter;
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        HttpSessionSecurityContextRepository httpSessionSecurityContextRepository = new HttpSessionSecurityContextRepository();
        return httpSessionSecurityContextRepository;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
