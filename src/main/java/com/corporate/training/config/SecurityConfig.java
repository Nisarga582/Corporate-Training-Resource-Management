package com.corporate.training.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
 @Bean UserDetailsService users(PasswordEncoder encoder){
  return new InMemoryUserDetailsManager(
   User.withUsername("admin").password(encoder.encode("Admin@123")).roles("ADMIN").build(),
   User.withUsername("manager").password(encoder.encode("Manager@123")).roles("MANAGER").build(),
   User.withUsername("trainer").password(encoder.encode("Trainer@123")).roles("TRAINER").build());
 }
 @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
  http.authorizeHttpRequests(a->a.requestMatchers("/login","/css/**","/js/**","/images/**").permitAll()
    .requestMatchers(HttpMethod.GET,"/api/modules/**","/api/modules").hasAnyRole("ADMIN","MANAGER","TRAINER")
   .requestMatchers("/api/**").hasAnyRole("ADMIN","MANAGER")
    .requestMatchers("/modules/**","/allocation","/maintenance","/").hasAnyRole("ADMIN","MANAGER","TRAINER")
    .anyRequest().authenticated())
   .formLogin(f->f.loginPage("/login").defaultSuccessUrl("/",true).permitAll())
   .logout(l->l.logoutSuccessUrl("/login?logout"))
   .csrf(c->c.ignoringRequestMatchers("/api/**"));
  return http.build();
 }
}
